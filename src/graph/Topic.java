package graph;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Topic {
    public final String name;
    private final List<Agent> subs = new CopyOnWriteArrayList<>();
    private final List<Agent> pubs = new CopyOnWriteArrayList<>();
    private Message lastMessage;

    public Topic(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Topic name cannot be null or empty");
        }
        this.name = name;
    }

    public void subscribe(Agent agent) {
        if (!subs.contains(agent)) {
            subs.add(agent);
        }
    }

    public void unsubscribe(Agent agent) {
        subs.remove(agent);
    }

    public void publish(Message message) {
        lastMessage = message;
        for (Agent agent : subs) {
            agent.callback(this.name, message);
        }
    }
    public void addPublisher(Agent a){
        if (!pubs.contains(a)) {
            pubs.add(a);
        }
    }

    public void removePublisher(Agent a){
        pubs.remove(a);
    }
    public Message getMsg() {
        return lastMessage;
    }
    public List<Agent> getSubs() {
        return subs;
    }
    public List<Agent> getPubs() {
        return pubs;
    }
}