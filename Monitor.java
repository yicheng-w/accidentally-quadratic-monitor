import java.util.*;
import java.io.*;
import java.util.concurrent.*;

public class Monitor {
    // Instance Variables
    private List<MonitoredSystem> systems;
    private BlockingQueue<PerformanceReport> updates;

    private int reportEvery;

    // We want to report the performance of each system...
    private Map<String, PerformanceReport> systemPerformance;

    // ... and also some global aggregates
    private float averageCPU;
    private float maxRAM;
    private float minDiskSpace;

    // Constructors
    public Monitor(List<MonitoredSystem> systems, int reportEvery) {
        this.systems = systems;
        this.reportEvery = reportEvery;

        // pad the update queue to be 100x the # of systems
        this.updates = new ArrayBlockingQueue<PerformanceReport>(systems.size() * 100);
        this.systemPerformance = new HashMap<String, PerformanceReport>();
    }

    public void monitor() throws InterruptedException{
        // we launch a thread that pokes each system at a fixed interval
        for (MonitoredSystem sys : this.systems) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            updates.put(sys.reportStats());
                            Thread.sleep(reportEvery);
                        }
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                }
            });
            t.start();
        }
        
        // and then we print the outputs in the main thread
        while (true) {
            PerformanceReport perf = updates.take(); // blocks until available
            systemPerformance.put(perf.name, perf);

            this.maxRAM = 0;
            this.minDiskSpace = 999999999;

            float totalCpu = 0;
            float n = 0;
            for (PerformanceReport p : systemPerformance.values()) {
                totalCpu += p.CPUUtilization;
                n += 1;
                this.maxRAM = Math.max(this.maxRAM, p.memoryUsage);
                this.minDiskSpace = Math.min(this.minDiskSpace, p.diskSpaceRemaining);
            }
            this.averageCPU = totalCpu / n;

            showStats();
        }
    }

    // Functions
    
    private void showStats() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
        System.out.println("Per-system Performance:");
        System.out.format("%15s|%10s|%10s|%10s\n", "System", "CPU", "RAM", "Disk");
        for (MonitoredSystem sys : this.systems) {
            PerformanceReport perf = systemPerformance.get(sys.name);
            if (perf == null) {
                continue;
            }
            System.out.format("%15s|%10.3f|%10.3f|%10.3f\n",
                    sys.name, perf.CPUUtilization, perf.memoryUsage,
                    perf.diskSpaceRemaining);
        }
        System.out.println();
        System.out.println("Overall Statistics:");
        System.out.format("Avg CPU: %f \t Max RAM Usage: %f Mb \t Min Disk Space Remaining: %f Mb\n",
                this.averageCPU, this.maxRAM, this.minDiskSpace);
    }

    public static void main(String[] args) {
        List<MonitoredSystem> systems = new ArrayList<MonitoredSystem>();
        for (int i = 0 ; i < 10 ; i++) {
            systems.add(new MonitoredSystem("System " + i));
        }
        Monitor m = new Monitor(systems, 100);

        try {
            m.monitor();
        }
        catch (InterruptedException e) {
            System.exit(0);
        }
    }
}
