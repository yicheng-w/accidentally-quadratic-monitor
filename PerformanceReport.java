import java.util.*;
import java.io.*;

// A class that represents the fake performance report
// sent by a node in a distributed system
public class PerformanceReport {
    // Instance Variables
    public String name;
    public float CPUUtilization;     // percentage CPU
    public float memoryUsage;        // memory usage in mbs
    public float diskSpaceRemaining; // disk space remaining in mbs

    // Constructors
    public PerformanceReport(String name, float cpu, float memory, float diskSpace) {
        this.name = name;
        this.CPUUtilization = cpu;
        this.memoryUsage = memory;
        this.diskSpaceRemaining = diskSpace;
    }
}
