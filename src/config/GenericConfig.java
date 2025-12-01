package config;

import config.Config;
import graph.Agent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class GenericConfig implements Config {
    private String confFile;
    private final List<ParallelAgent> agents;

    public GenericConfig() {
        this.agents = new ArrayList<>();
    }

    @Override
    public void create() {

        if (confFile == null || confFile.isEmpty()) {
            throw new IllegalStateException("Configuration file is not set.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(confFile)))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }

            if (lines.size() % 3 != 0) {
                throw new IllegalArgumentException("Invalid configuration format.");
            }

            for (int i = 0; i < lines.size(); i += 3) {
                String className = lines.get(i);
                String[] subs = lines.get(i + 1).split(",");
                String[] pubs = lines.get(i + 2).split(",");
                Class<?> agentClass = Class.forName(className);
                Constructor<?> constructor = agentClass.getConstructor(String[].class, String[].class);
                Agent agent = (Agent) constructor.newInstance((Object) subs, (Object) pubs);

                ParallelAgent parallelAgent = new ParallelAgent(agent);
                agents.add(parallelAgent);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create configuration", e);
        }
    }

    @Override
    public String getName() {
        return "GenericConfig";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void close() {
        for (ParallelAgent agent : agents) {
            agent.close();
        }
    }

    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }
}
