package config;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.Set;


public class Node {
    private String name;
    private List<Node> edges;
    private graph.Message msg;

    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<Node>();
        this.msg = null;
    }
    //Getter and setters

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Node> getEdges() {
        return edges;
    }
    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }
    public graph.Message getMsg() {
        return msg;
    }
    public void setMsg(graph.Message msg) {
        this.msg = msg;
    }

    //add Node to edge list
    public void addEdge(Node n) {
        edges.add(n);
    }


    //hasCycle - Check for cycles in the graph
    public Boolean hasCycles() {
        Set<Node> visited = new HashSet<Node>();
        Set<Node> stack = new HashSet<Node>();
        return hasCycleLP(this,visited,stack);
    }
    private Boolean hasCycleLP(Node current,Set<Node> visited, Set<Node> stack) {
        if (visited.contains(current)) {
            return true; //Cycle detected
        }
        if(stack.contains(current)) {
            return false; //Node already processed
        }
        visited.add(current);
        stack.add(current);
        for (Node n : current.getEdges()) {
            if (hasCycleLP(n,visited,stack)) {
                return true;
            }
        }
        stack.remove(current);
        return false;
    }
}