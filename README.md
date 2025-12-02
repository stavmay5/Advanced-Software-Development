# 🚀 Computational Graph & Agent Server

A comprehensive Java-based infrastructure for managing computational graphs, autonomous agents, and a topic-based messaging system.
This project demonstrates advanced software engineering concepts including **Multithreading**, **Design Patterns**, and a **Custom HTTP Server**.

## 🛠️ Architecture & Design Patterns

The project is built upon several core components:

### 1. Topic-Based Messaging System (Pub/Sub)
* **`TopicManagerSingleton`:** Centralized management of topics using the **Singleton Pattern** to ensure a single instance manages all communication channels.
* **`Topic` & `Message`:** Encapsulates data flow between agents.

### 2. Agent System
Autonomous units that perform calculations based on messages they receive.
* **`BinOpAgent` / `PlusAgent`:** Agents performing binary operations.
* **`ParallelAgent`:** Implements the **Decorator Pattern** to wrap agents, enabling future parallel execution.
* **`Graph`:** Represents the network of agents and their dependencies.

### 3. Custom HTTP Server
A multi-threaded server built from scratch using Java Sockets.
* **`MyHTTPServer`:** Handles concurrent client connections using a Thread Pool.
* **`RequestParser`:** Parses raw HTTP requests.
* **`Servlet` Interface:** Defines how specific URI paths are handled.

## 📂 Project Structure

The source code is organized into packages:

* `src/server`: Contains the HTTP server implementation, request parsing, and servlet interfaces.
* `src/graph`: Contains the core messaging infrastructure (Topics, Messages, Agent interface).
* `src/config`: Contains specific agent implementations, graph logic, and the main entry point (`MainTrain`).

## 💻 Usage

### Prerequisites
* Java Development Kit (JDK) 8 or higher.

### Running the Project
1. Compile all files from the `src` directory.
2. Run the `MainTrain` class to test the system components (Cycles, Binary Graph, Topics Graph, and HTTP Server).

```bash
javac -d bin src/**/*.java
java -cp bin config.MainTrain
```
