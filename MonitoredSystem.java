import java.util.*;
import java.io.*;

// This is a mock up for a real node in a distributed system
// because the context is "performance reporting", we expose
// a function [reportStats] that would normally be a remote
// procedure call to the actual system.

public class MonitoredSystem {
    // Instance Variables
    public String name;
    private Random rand; // used to generate random stats

    // Constructors
    public MonitoredSystem(String name) {
        this.name = name;
        this.rand = new Random();
    }

    // Functions
    public PerformanceReport reportStats() {
        float cpu       = rand.nextFloat() * 100;
        float memory    = rand.nextFloat() * 8 * 1024;
        float diskSpace = rand.nextFloat() * 500 * 1024;
        return new PerformanceReport(this.name, cpu, memory, diskSpace);
    }
}
