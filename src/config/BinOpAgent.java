package config;

import java.util.function.BinaryOperator;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;


public class BinOpAgent implements Agent {

    private Agent agent;
    private String agentName;
    private String firstTopicName;
    private String secondTopicName;
    private String outputTopicName;
    private BinaryOperator<Double> func;

    public BinOpAgent(String agentName, String firstTopicName, String secondTopicName, String outputTopicName, BinaryOperator<Double> func) {
        TopicManager tm = TopicManagerSingleton.get();

        // Get topics from TopicManager
        Topic firstTopic = tm.getTopic(firstTopicName);
        Topic secondTopic = tm.getTopic(secondTopicName);
        Topic outputTopic = tm.getTopic(outputTopicName);

        // Validate that topics are not null
        if (firstTopic == null || secondTopic == null || outputTopic == null) {
            throw new IllegalArgumentException("Topics cannot be null");
        }

        this.agentName = agentName;
        this.firstTopicName = firstTopicName;
        this.secondTopicName = secondTopicName;
        this.outputTopicName = outputTopicName;
        this.func = func;

        // Define the agent
        this.agent = new Agent() {
            @Override
            public String getName() {
                return agentName;
            }

            @Override
            public void reset() {
                System.out.println("Resetting topics to default values.");
                firstTopic.publish(new Message(0.0));
                secondTopic.publish(new Message(0.0));
            }

            @Override
            public void callback(String topic, Message msg) {
                System.out.println("Callback received: " + topic + " -> " + msg.asDouble);
                if (topic.equals(firstTopicName)) {
                    // Message came from the first topic
                    double x = msg.asDouble;
                    double y = (secondTopic.getMsg() != null) ? secondTopic.getMsg().asDouble : 0;
                    double result = func.apply(x, y);
                    outputTopic.publish(new Message(result));
                } else if (topic.equals(secondTopicName)) {
                    // Message came from the second topic
                    double y = msg.asDouble;
                    double x = (firstTopic.getMsg() != null) ? firstTopic.getMsg().asDouble : 0;
                    double result = func.apply(x, y);
                    outputTopic.publish(new Message(result));
                }
            }

            @Override
            public void close() {
                System.out.println("Closing agent.");
                // No additional cleanup needed here
            }
        };

        // Subscribe agent to topics
        firstTopic.subscribe(this.agent);
        secondTopic.subscribe(this.agent);
        outputTopic.addPublisher(this.agent);
    }

    // Reset topics to default values
    public void reset(TopicManager tm) {
        System.out.println("Resetting topics.");
        tm.getTopic(firstTopicName).publish(new Message(0.0));
        tm.getTopic(secondTopicName).publish(new Message(0.0));
    }

    // Close the agent and unsubscribe from topics
    public void closeAgent() {
        System.out.println("Unsubscribing agent from topics.");
        TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(firstTopicName).unsubscribe(agent);
        tm.getTopic(secondTopicName).unsubscribe(agent);
        tm.getTopic(outputTopicName).removePublisher(agent);
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void reset() {

    }

    @Override
    public void callback(String topic, Message msg) {

    }

    @Override
    public void close() {

    }

}
