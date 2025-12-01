package config;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;

public class IncAgent implements Agent {
    private final String name;
    private final Topic input;
    private final Topic output;

    public IncAgent(String[] subs, String[] pubs) {
        if (subs.length < 1 || pubs.length < 1) {
            throw new IllegalArgumentException("IncAgent requires at least 1 sub and 1 pub.");
        }

        this.name = "IncAgent";
        TopicManagerSingleton.TopicManager manager = TopicManagerSingleton.get();
        this.input = manager.getTopic(subs[0]);
        this.output = manager.getTopic(pubs[0]);

        this.input.subscribe(this);
        this.output.addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {

    }
    ;
    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(input.name)) {
            double incrementedValue = msg.asDouble + 1;
            output.publish(new Message(incrementedValue));
        }
    }

    @Override
    public void close() {
        input.unsubscribe(this);
        output.removePublisher(this);
    }
}