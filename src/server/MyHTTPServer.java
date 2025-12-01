package server;



import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;
import java.net.ServerSocket;

/**
 * A simple HTTP server implementation that manages and dispatches HTTP requests
 * to the appropriate servlets based on the request type and URI.
 */
public class MyHTTPServer extends Thread implements HTTPServer {

    /** Concurrent map to manage servlets for GET,POST,DELETE requests. */
    private ConcurrentHashMap<String, Servlet> getServlets = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Servlet> postServlets = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Servlet> deleteServlets = new ConcurrentHashMap<>();

    /** Thread pool to handle multiple client connections concurrently. */
    private ExecutorService requestHandlerPool;

    /** Socket used to accept client connections. */
    private ServerSocket serverSocket;

    /** Flag to indicate if the server should stop accepting requests. */
    private volatile boolean isServerStopped = false;

    /** Port number on which the server listens for incoming connections. */
    private final int port;

    /** Number of threads in the thread pool for handling requests. */
    private final int threadCount;

    /**
     * Constructs a new HTTP server instance with the specified port and thread count.
     *
     * @param port The port number for the server to listen on.
     * @param threadCount The number of threads in the thread pool.
     */
    public MyHTTPServer(int port, int threadCount) {
        // Initialize the thread pool with a fixed number of threads
        requestHandlerPool = Executors.newFixedThreadPool(threadCount);
        this.port = port;
        this.threadCount = threadCount;
    }

    /**
     * Registers a servlet to handle requests for a specific HTTP command and URI.
     *
     * @param httpCommand The HTTP command (e.g., GET, POST, DELETE) for which the servlet will handle requests.
     * @param uri The URI that the servlet will handle.
     * @param servlet The servlet instance to handle the requests.
     */
    public void addServlet(String httpCommand, String uri, Servlet servlet) {
        if (uri == null || servlet == null) {
            return;
        }

        httpCommand = httpCommand.toUpperCase();

        switch (httpCommand) {
            case "GET":
                getServlets.put(uri, servlet);
                break;
            case "POST":
                postServlets.put(uri, servlet);
                break;
            case "DELETE":
                deleteServlets.put(uri, servlet);
                break;
        }
    }

    /**
     * Removes a servlet that handles requests for a specific HTTP command and URI.
     *
     * @param httpCommand The HTTP command (e.g., GET, POST, DELETE) for which the servlet was handling requests.
     * @param uri The URI that the servlet was handling.
     */
    public void removeServlet(String httpCommand, String uri) {
        if (uri == null) {
            return;
        }

        httpCommand = httpCommand.toUpperCase();

        switch (httpCommand) {
            case "GET":
                getServlets.remove(uri);
                break;
            case "POST":
                postServlets.remove(uri);
                break;
            case "DELETE":
                deleteServlets.remove(uri);
                break;
        }
    }

    /**
     * Starts the HTTP server to listen for and handle client connections.
     */
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            serverSocket.setSoTimeout(1000); // Set timeout for socket accept operations

            while (!isServerStopped) {
                try {
                    // Accept a new client connection
                    Socket clientSocket = serverSocket.accept();

                    // Handle the client request in a separate thread
                    requestHandlerPool.submit(() -> {
                        try {
                            Thread.sleep(125); // Delay to ensure proper request reception
                            BufferedReader requestReader = createBufferedReader(clientSocket);

                            // Parse the incoming request
                            RequestParser.RequestInfo requestInfo = RequestParser.parseRequest(requestReader);
                            ConcurrentHashMap<String, Servlet> servletMap;

                            if (requestInfo != null) {
                                switch (requestInfo.getHttpCommand()) {
                                    case "GET":
                                        servletMap = getServlets;
                                        break;
                                    case "POST":
                                        servletMap = postServlets;
                                        break;
                                    case "DELETE":
                                        servletMap = deleteServlets;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Unsupported HTTP command: " + requestInfo.getHttpCommand());
                                }

                                // Find the best matching servlet based on the longest URI match
                                String bestMatchUri = "";
                                Servlet matchingServlet = null;
                                for (Map.Entry<String, Servlet> entry : servletMap.entrySet()) {
                                    if (requestInfo.getUri().startsWith(entry.getKey()) && entry.getKey().length() > bestMatchUri.length()) {
                                        bestMatchUri = entry.getKey();
                                        matchingServlet = entry.getValue();
                                    }
                                }

                                // Handle the request using the matching servlet
                                if (matchingServlet != null) {
                                    matchingServlet.handle(requestInfo, clientSocket.getOutputStream());
                                }
                            }
                            requestReader.close();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            // Close the client connection
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    // Handle socket accept timeout exception
                    if (isServerStopped) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a BufferedReader to read from the client socket.
     *
     * @param clientSocket The client socket.
     * @return A BufferedReader to read from the socket.
     * @throws IOException If an I/O error occurs.
     */
    private static BufferedReader createBufferedReader(Socket clientSocket) throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        int availableBytes = inputStream.available();
        byte[] buffer = new byte[availableBytes];
        int bytesRead = inputStream.read(buffer, 0, availableBytes);

        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(buffer, 0, bytesRead)
                )
        );
    }

    /**
     * Starts the HTTP server to begin accepting and handling requests.
     */
    public void start() {
        isServerStopped = false;
        super.start();
    }

    /**
     * Stops the HTTP server and shuts down the thread pool.
     */
    public void close() {
        isServerStopped = true;
        requestHandlerPool.shutdownNow();
    }

    /**
     * Gets the thread pool used by the server for handling client requests.
     *
     * @return The thread pool.
     */
    public Object getThreadPool() {
        return requestHandlerPool;
    }
}