package config;

import java.util.*;
import graph.Agent;
import graph.Topic;
import graph.Message;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

public class Graph extends ArrayList<Node> {
    private HashMap<String, Node> nodeMap; // Maps names to Nodes for quick access

    public Graph() {
        super();
        nodeMap = new HashMap<>();
    }

    // Create graph from topics in TopicManager
    public void createFromTopics() {
        this.clear();
        nodeMap.clear();

        TopicManager tm = TopicManagerSingleton.get();
        Collection<Topic> topics = tm.getTopics();

        // Create Nodes for Topics
        for (Topic topic : topics) {
            String topicNodeName = "T" + topic.name;
            Node topicNode = nodeMap.computeIfAbsent(topicNodeName, Node::new);
            this.add(topicNode);
        }

        // Create Edges based on subscriptions and publications
        for (Topic topic : topics) {
            String topicNodeName = "T" + topic.name;
            Node topicNode = nodeMap.get(topicNodeName);

            if (topicNode == null) {
                throw new IllegalStateException("Topic node not found: " + topicNodeName);
            }

            for (Agent subscriber : topic.getSubs()) {
                Node subscriberNode = findOrCreateAgentNode(subscriber);
                topicNode.addEdge(subscriberNode); // topicNode -> subscriberNode
            }

            for (Agent publisher : topic.getPubs()) {
                Node publisherNode = findOrCreateAgentNode(publisher);
                publisherNode.addEdge(topicNode); // publisherNode -> topicNode
            }
        }
    }


    // Find or create a Node for an Agent
    private Node findOrCreateAgentNode(Agent agent) {
        String agentNodeName = "A" + agent.getName();
        Node node = nodeMap.get(agentNodeName);
        if(node == null) {
            node = new Node(agentNodeName);
            this.add(node);
            nodeMap.put(agentNodeName, node);
        }
        return node;
    }

    // Check for cycles in the graph
    public boolean hasCycles() {
        for (Node node : this) {
            if (hasCyclesHelper(node, new HashSet<>(), new HashSet<>())) {
                return true; // Cycle detected
            }
        }
        return false;
    }

    // Helper method for cycle detection
    private boolean hasCyclesHelper(Node current, Set<Node> visited, Set<Node> stack) {
        if (stack.contains(current)) {
            return true; // Cycle detected
        }
        if (visited.contains(current)) {
            return false; // Node already processed
        }

        visited.add(current);
        stack.add(current);

        for (Node neighbor : current.getEdges()) {
            if (hasCyclesHelper(neighbor, visited, stack)) {
                return true;
            }
        }

        stack.remove(current);
        return false;
    }

}
