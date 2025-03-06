package com.authms.examples;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// Load Balancing Strategies
enum LoadBalancingStrategy {
    ROUND_ROBIN, RANDOM
}

// Load Balancer Class
class LoadBalancer {
    private static final int MAX_INSTANCES = 10;
    private final List<String> instances = new ArrayList<>();
    private final Random random = new Random();
    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);
    private LoadBalancingStrategy strategy;

    public LoadBalancer(LoadBalancingStrategy strategy) {
        this.strategy = strategy;
    }

    // Add a new instance (max 10 instances)
    public boolean addInstance(String instance) {
        if (instances.size() < MAX_INSTANCES) {
            instances.add(instance);
            return true;
        }
        return false; // Reached max instances
    }

    // Remove an instance
    public boolean removeInstance(String instance) {
        return instances.remove(instance);
    }

    // Get an instance based on the current strategy
    public String getInstance() {
        if (instances.isEmpty()) {
            throw new IllegalStateException("No available instances");
        }
        return switch (strategy) {
            case ROUND_ROBIN -> getRoundRobinInstance();
            case RANDOM -> getRandomInstance();
        };
    }

    private String getRoundRobinInstance() {
        int index = roundRobinIndex.getAndIncrement() % instances.size();
        return instances.get(index);
    }

    private String getRandomInstance() {
        int index = random.nextInt(instances.size());
        return instances.get(index);
    }

    // Set the load balancing strategy
    public void setStrategy(LoadBalancingStrategy strategy) {
        this.strategy = strategy;
    }
}

// Example Usage
public class LoadBalancerMain {
    public static void main(String[] args) {
        LoadBalancer loadBalancer = new LoadBalancer(LoadBalancingStrategy.ROUND_ROBIN);
        
        // Adding instances
        for (int i = 1; i <= 10; i++) {
            loadBalancer.addInstance("Instance-" + i);
        }
        
        // Testing ROUND ROBIN strategy
        System.out.println("Round Robin Load Balancing:");
        for (int i = 0; i < 5; i++) {
            System.out.println(loadBalancer.getInstance());
        }

        // Switch to RANDOM strategy
        loadBalancer.setStrategy(LoadBalancingStrategy.RANDOM);
        System.out.println("\nRandom Load Balancing:");
        for (int i = 0; i < 5; i++) {
            System.out.println(loadBalancer.getInstance());
        }
    }
}
