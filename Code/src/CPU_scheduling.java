/*
          --------------------------------------------
           CPU Scheduling Using Round Robin Algorithm
                 CPCS-361 - Group project - CS1
          --------------------------------------------

         Students: -
         - Mahmued Alardawi - 2135209 - mmalardawi@stu.kau.edu.sa
         - Yazeed Alsahafi - 2036556
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class CPU_scheduling {
    // Global variables used across the scheduling system
    static int timeQuantum = 10;  // Default time quantum for processes
    public static int systemTime = 0;  // Simulation current time
    public static int memory = 0;  // Total available memory
    public static int device = 0;  // Total available devices
    public static int timeStamp = 0;  // Time marker for certain operations
    public static ArrayList<Job> jobsMatrix = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Project part I)

        // Setup input and output streams
        Scanner input = new Scanner(new File("input.txt"));
        File file = new File("outputPart(DRR).txt");
        PrintWriter outputPart_I = new PrintWriter(file);

        System.out.println("Dynamic Round Robin)");
        // Start the Round Robin scheduling process
        dynamicRoundRobin(input, outputPart_I);

        // Close input and output streams
        input.close();
        outputPart_I.close();

        // Project part II)

        // Resetting time quantum and jobs matrix for part II of the project
        timeQuantum = 10;
        jobsMatrix.clear();

        // Setup input and output streams
        input = new Scanner(new File("input.txt"));
        File file1 = new File("outputPart(RR).txt");
        PrintWriter outputPart_II = new PrintWriter(file1);

        System.out.println("Round Robin)");
        // Start the Round Robin scheduling process
        roundRobin(input, outputPart_II);

        // Close input and output streams
        input.close();
        outputPart_II.close();
    }

    // Main Dynamic Round Robin scheduling function
    public static void dynamicRoundRobin(Scanner input, PrintWriter output) throws IOException {
        ArrayList<Job> jobs = new ArrayList<>();
        Queues queues = new Queues();

        // Initial system configuration
        systemConfiguration(input, new Job());
        final int memory0 = memory;
        final int device0 = device;

        // Track job execution progress
        int jobTracker = 0;

        while (input.hasNext() || !queues.areEmpty() || !jobs.isEmpty()) {
            // Process job arrivals and configurations
            Job job = new Job();
            systemConfiguration(input, job);
            if (job.isJob()) {
                jobs.add(job);
                jobsMatrix.add(new Job().copy(job));
            }

            // Process jobs waiting in hold queues
            processHoldingQueues(queues);

            // Process new arrivals and manage ready jobs
            dynamicProcessNewArrivals(jobs, queues, memory0, device0);

            // Print current system state to output file
            outputSystemState(jobs, queues, jobTracker, output);

            // Printing data base on current time stamp
            if (systemTime == timeStamp) {timeStampPrint(jobs, queues, jobTracker);}

            // Run jobs in the ready queue and handle time quantum
            jobTracker = dynamicProcessReadyQueue(queues, jobTracker);

            // Advance simulation time
            systemTime++;
        }
        // Final state output after all operations
        outputSystemState(jobs, queues, jobTracker, output);
        performanceMetrics(output);
    }

    // Main Round Robin scheduling function
    public static void roundRobin(Scanner input, PrintWriter output) throws IOException {
        ArrayList<Job> jobs = new ArrayList<>();
        Queues queues = new Queues();

        // Initial system configuration
        systemConfiguration(input, new Job());
        final int memory0 = memory;
        final int device0 = device;

        // Track job execution progress
        int jobTracker = 0;

        while (input.hasNext() || !queues.areEmpty() || !jobs.isEmpty()) {
            // Process job arrivals and configurations
            Job job = new Job();
            systemConfiguration(input, job);
            if (job.isJob()) {
                jobs.add(job);
                jobsMatrix.add(new Job().copy(job));
            }

            // Process jobs waiting in hold queues
            processHoldingQueues(queues);

            // Process new arrivals and manage ready jobs
            processNewArrivals(jobs, queues, memory0, device0);

            // Print current system state to output file
            outputSystemState(jobs, queues, jobTracker, output);

            // Printing data base on current time stamp
            if (systemTime == timeStamp) {timeStampPrint(jobs, queues, jobTracker);}

            // Run jobs in the ready queue and handle time quantum
            jobTracker = processReadyQueue(queues, jobTracker);

            // Advance simulation time
            systemTime++;
        }
        // Final state output after all operations
        outputSystemState(jobs, queues, jobTracker, output);
        performanceMetrics(output);
    }

    private static void processHoldingQueues(Queues queues) {
        // Process jobs in holding queues, moving them to the ready queue when resources are available
        if (!queues.getHoldQueue1().isEmpty() &&
                Objects.requireNonNull(queues.getHoldQueue1().peek()).canProcess(memory, device)) {
            queues.getReadyQueue().add(queues.getHoldQueue1().poll());
            memory -= Objects.requireNonNull(queues.getReadyQueue().peek()).getMemoryRequired();
            device -= Objects.requireNonNull(queues.getReadyQueue().peek()).getDevicesRequired();
        }
        else if (!queues.getHoldQueue2().isEmpty() &&
                Objects.requireNonNull(queues.getHoldQueue2().peek()).canProcess(memory, device)) {
            queues.getReadyQueue().add(queues.getHoldQueue2().poll());
            memory -= Objects.requireNonNull(queues.getReadyQueue().peek()).getMemoryRequired();
            device -= Objects.requireNonNull(queues.getReadyQueue().peek()).getDevicesRequired();
        }
    }

    // Read system configuration from the input
    public static void systemConfiguration(Scanner input, Job job) throws IOException {
        if (input.hasNext()) {
            String[] line = input.nextLine().split("[ =]");  // Split input line into parts
            String command = line[0];  // First part is the command

            switch (command) {
                case "C":  // Configuration command
                    systemTime = Integer.parseInt(line[1]);  // Set the simulation current time

                    // Parse and set memory and device configurations
                    for (int i = 2; i < line.length; i += 2) {
                        switch (line[i]) {
                            case "M": memory = Integer.parseInt(line[i + 1]);break;
                            case "S": device = Integer.parseInt(line[i + 1]);break;
                        }
                    }
                    break;

                // Job arrival command
                case "A": job.setArrivalTime(Integer.parseInt(line[1]));  // Set job arrival time

                    // Parse and set job parameters
                    for (int i = 2; i < line.length; i += 2) {
                        switch (line[i]) {
                            case "J": job.setJobId(Integer.parseInt(line[i + 1]));break;
                            case "M": job.setMemoryRequired(Integer.parseInt(line[i + 1]));break;
                            case "S": job.setDevicesRequired(Integer.parseInt(line[i + 1]));break;
                            case "R": job.setBurstTime(Integer.parseInt(line[i + 1]));break;
                            case "P": job.setPriority(Integer.parseInt(line[i + 1]));break;
                        }
                    }
                    break;

                // Display time command
                case "D": timeStamp = Integer.parseInt(line[1]);break;

                // Exit on unrecognized command
                default: System.exit(1);
            }
        }
    }

    private static void dynamicProcessNewArrivals(ArrayList<Job> jobs, Queues queues, int memory0, int device0) {
        // Process new job arrivals and decide on job placement based on resource availability
        if (!jobs.isEmpty()) {
            Job readyJob = jobs.getFirst();
            if (readyJob.getArrivalTime() == systemTime) {
                if (readyJob.canProcess(memory0, device0)) {
                    if (readyJob.getMemoryRequired() <= memory && readyJob.getDevicesRequired() <= device) {
                        queues.getReadyQueue().add(readyJob);
                        memory -= readyJob.getMemoryRequired();
                        device -= readyJob.getDevicesRequired();
                    }
                    else {
                        if (readyJob.getPriority() == 1) {queues.getHoldQueue1().add(readyJob);}
                        else {queues.getHoldQueue2().add(readyJob);}
                    }
                }
                jobs.removeFirst();
            }
        }
        // Adjust time quantum if only one job is in the ready queue
        if (queues.getReadyQueue().size() == 1) {
            timeQuantum = Objects.requireNonNull(queues.getReadyQueue().peek()).getBurstTime();
        }
    }
    private static void processNewArrivals(ArrayList<Job> jobs, Queues queues, int memory0, int device0) {
        // Process new job arrivals and decide on job placement based on resource availability
        if (!jobs.isEmpty()) {
            Job readyJob = jobs.getFirst();
            if (readyJob.getArrivalTime() == systemTime) {
                if (readyJob.canProcess(memory0, device0)) {
                    if (readyJob.getMemoryRequired() <= memory && readyJob.getDevicesRequired() <= device) {
                        queues.getReadyQueue().add(readyJob);
                        memory -= readyJob.getMemoryRequired();
                        device -= readyJob.getDevicesRequired();
                    }
                    else {
                        if (readyJob.getPriority() == 1) {queues.getHoldQueue1().add(readyJob);}
                        else {queues.getHoldQueue2().add(readyJob);}
                    }
                }
                jobs.removeFirst();
            }
        }
    }

    private static int dynamicProcessReadyQueue(Queues queues, int jobTracker) {
        // Process jobs in the ready queue, managing time quantum and job transitions
        if (!queues.getReadyQueue().isEmpty()) {
            int runTime = Objects.requireNonNull(queues.getReadyQueue().peek()).getBurstTime();

            if (Objects.requireNonNull(queues.getReadyQueue().peek()).getStartTime() == -1) {
                Objects.requireNonNull(queues.getReadyQueue().peek()).setStartTime(systemTime);
                for (Job job: jobsMatrix) {
                    if (job.getJobId() == Objects.requireNonNull(queues.getReadyQueue().peek()).getJobId()) {
                        Objects.requireNonNull(job).setStartTime(systemTime);
                    }
                }
            }

            jobTracker++;
            if (runTime == jobTracker || timeQuantum == jobTracker) {
                for (Job job: jobsMatrix) {
                    if (job.getJobId() == Objects.requireNonNull(queues.getReadyQueue().peek()).getJobId()) {
                        Objects.requireNonNull(job).setCompletionTime(systemTime + 1);
                    }
                }
                Objects.requireNonNull(queues.getReadyQueue().peek()).setBurstTime(runTime - jobTracker);

                if (Objects.requireNonNull(queues.getReadyQueue().peek()).getBurstTime() == 0) {
                    memory += Objects.requireNonNull(queues.getReadyQueue().peek()).getMemoryRequired();
                    device += Objects.requireNonNull(queues.getReadyQueue().peek()).getDevicesRequired();
                    queues.getReadyQueue().poll();
                }
                else {
                    queues.getReadyQueue().add(queues.getReadyQueue().poll());
                }
                jobTracker = 0;
                timeQuantum = updateDynamicTimeQuantum(queues);
            }
        }
        return jobTracker;
    }
    private static int processReadyQueue(Queues queues, int jobTracker) {
        // Process jobs in the ready queue, managing time quantum and job transitions
        if (!queues.getReadyQueue().isEmpty()) {
            int runTime = Objects.requireNonNull(queues.getReadyQueue().peek()).getBurstTime();

            if (Objects.requireNonNull(queues.getReadyQueue().peek()).getStartTime() == -1) {
                Objects.requireNonNull(queues.getReadyQueue().peek()).setStartTime(systemTime);
                for (Job job: jobsMatrix) {
                    if (job.getJobId() == Objects.requireNonNull(queues.getReadyQueue().peek()).getJobId()) {
                        Objects.requireNonNull(job).setStartTime(systemTime);
                    }
                }
            }

            jobTracker++;
            if (runTime == jobTracker || timeQuantum == jobTracker) {
                for (Job job: jobsMatrix) {
                    if (job.getJobId() == Objects.requireNonNull(queues.getReadyQueue().peek()).getJobId()) {
                        Objects.requireNonNull(job).setCompletionTime(systemTime + 1);
                    }
                }
                Objects.requireNonNull(queues.getReadyQueue().peek()).setBurstTime(runTime - jobTracker);

                if (Objects.requireNonNull(queues.getReadyQueue().peek()).getBurstTime() == 0) {
                    memory += Objects.requireNonNull(queues.getReadyQueue().peek()).getMemoryRequired();
                    device += Objects.requireNonNull(queues.getReadyQueue().peek()).getDevicesRequired();

                    queues.getReadyQueue().poll();
                    jobTracker = 0;
                }
                else {
                    for (Job job: jobsMatrix) {
                        if (job == queues.getReadyQueue().peek()) {
                            Objects.requireNonNull(job).setCompletionTime(systemTime + 1);
                        }
                    }
                    queues.getReadyQueue().add(queues.getReadyQueue().poll());
                    jobTracker = 0;
                }
            }
        }
        return jobTracker;
    }

    private static int updateDynamicTimeQuantum(Queues queues) {
        // Dynamically calculate the time quantum based on jobs in ready queue
        if (!queues.getReadyQueue().isEmpty()) {
            int totalRuntime = 0;
            int count = 0;
            for (Job job : queues.getReadyQueue()) {
                totalRuntime += job.getBurstTime();
                count++;
            }
            return totalRuntime / count;
        }
        return 0;
    }

    private static void outputSystemState(ArrayList<Job> jobs, Queues queues, int jobTracker, PrintWriter output)
            throws IOException {
        // Output the current state of the system to a file, detailing job status and system resources
        output.println("Time: " + systemTime);
        output.printf("%-10s %-15s %-15s %-15s %-15s %-15s %-15s %-15s\n",
                "Job ID", "Arrival Time", "State", "Location", "Priority", "Memory Req.", "Devices Req.", "Burst Time");

        for (Job job : jobs) {

            output.printf("%-10s %-15s %-15s %-15s %-15s %-15s %-15s %-15s\n",
                    job.getJobId(),
                    job.getArrivalTime(),
                    "waiting",
                    "input file",
                    job.getPriority(),
                    job.getMemoryRequired(),
                    job.getDevicesRequired(),
                    job.getBurstTime()
            );
        }

        // Display details for jobs in the ready queue, including remaining time calculation
        int remainingTTime = 0;
        if (!queues.getReadyQueue().isEmpty()) {
            remainingTTime = Objects.requireNonNull(queues.getReadyQueue().peek()).getBurstTime() - jobTracker;
        }

        output.println("\nContents of Ready Queue:");
        for (Job j : queues.getReadyQueue()) {
            if (queues.getReadyQueue().peek() == j) {
                output.println("Job ID: " + Objects.requireNonNull(j).getJobId() +
                        ", Arrival Time: " + j.getArrivalTime() +
                        ", Memory: " + j.getMemoryRequired() +
                        ", Devices: " + j.getDevicesRequired() +
                        ", Burst Time: " + j.getBurstTime() +
                        ", Remaining Time: " + remainingTTime
                );
            }
            else {
                output.println("Job ID: " + j.getJobId() +
                        ", Arrival Time: " + j.getArrivalTime() +
                        ", Memory: " + j.getMemoryRequired() +
                        ", Devices: " + j.getDevicesRequired() +
                        ", Burst Time: " + j.getBurstTime()
                );
            }
        }

        // Display details for jobs in holding queues
        output.println("Contents of Hold Queue 1:");
        for (Job j : queues.getHoldQueue1()) {
            output.println("Job ID: " + j.getJobId() +
                    ", Arrival Time " + j.getArrivalTime() +
                    ", Memory: " + j.getMemoryRequired() +
                    ", Devices: " + j.getDevicesRequired() +
                    ", Burst Time: " + j.getBurstTime()
            );
        }

        output.println("Contents of Hold Queue 2:");
        for (Job j : queues.getHoldQueue2()) {
            output.println("Job ID: " + j.getJobId() +
                    ", Arrival Time: " + j.getArrivalTime() +
                    ", Memory: " + j.getMemoryRequired() +
                    ", Devices: " + j.getDevicesRequired() +
                    ", Burst Time: " + j.getBurstTime()
            );
        }

        // Print current system resource status and time quantum
        output.println("\nCurrent Memory: " + memory);
        output.println("Current Devices: " + device);
        output.println("Current Time Quantum: " + timeQuantum);
        output.println("\n----------------------------------------\n");
    }

    private static void timeStampPrint(ArrayList<Job> jobs, Queues queues, int jobTracker)
                throws IOException {
        System.out.println("Time: " + systemTime);
        System.out.printf("%-10s %-15s %-15s %-15s %-15s %-15s %-15s %-15s\n",
                "Job ID", "Arrival Time", "State", "Location", "Priority", "Memory Req.", "Devices Req.", "Burst Time");

        for (Job job : jobs) {

            System.out.printf("%-10s %-15s %-15s %-15s %-15s %-15s %-15s %-15s\n",
                    job.getJobId(),
                    job.getArrivalTime(),
                    "waiting",
                    "input file",
                    job.getPriority(),
                    job.getMemoryRequired(),
                    job.getDevicesRequired(),
                    job.getBurstTime()
            );
        }

        int remainingTTime = 0;
        if (!queues.getReadyQueue().isEmpty()) {
            remainingTTime = Objects.requireNonNull(queues.getReadyQueue().peek()).burstTime - jobTracker;
        }

        System.out.println("\nContents of Ready Queue:");
        for (Job j : queues.getReadyQueue()) {
            if (queues.getReadyQueue().peek() == j) {
                System.out.println("Job ID: " + Objects.requireNonNull(j).getJobId() +
                        ", Arrival Time: " + j.getArrivalTime() +
                        ", Memory: " + j.getMemoryRequired() +
                        ", Devices: " + j.getDevicesRequired() +
                        ", Burst Time: " + j.getBurstTime() +
                        ", Remaining Time: " + remainingTTime
                );
            } else {
                System.out.println("Job ID: " + j.getJobId() +
                        ", Arrival Time: " + j.getArrivalTime() +
                        ", Memory: " + j.getMemoryRequired() +
                        ", Devices: " + j.getDevicesRequired() +
                        ", Burst Time: " + j.getBurstTime()
                );
            }
        }

        System.out.println("Contents of Hold Queue 1:");
        for (Job j : queues.getHoldQueue1()) {
            System.out.println("Job ID: " + j.getJobId() +
                    ", Arrival Time: " + j.getArrivalTime() +
                    ", Memory: " + j.getMemoryRequired() +
                    ", Devices: " + j.getDevicesRequired() +
                    ", Burst Time: " + j.getBurstTime()
            );
        }

        System.out.println("Contents of Hold Queue 2:");
        for (Job j : queues.getHoldQueue2()) {
            System.out.println("Job ID: " + j.getJobId() +
                    ", Arrival Time: " + j.getArrivalTime() +
                    ", Memory: " + j.getMemoryRequired() +
                    ", Devices: " + j.getDevicesRequired() +
                    ", Burst Time: " + j.getBurstTime()
            );
        }

        System.out.println("\nCurrent Memory: " + memory);
        System.out.println("Current Devices: " + device);
        System.out.println("Current Time Quantum: " + timeQuantum);
        System.out.println("\n----------------------------------------\n"
        );
    }

    private static void performanceMetrics(PrintWriter output)
            throws IOException {
        // Output the performance metrics to a file
        double totalTurnaround = 0;
        double totalWaiting = 0;

        output.println("Final Metrics for All Jobs:");

        for (Job job : jobsMatrix) {
            job.calculateMetrics();
            totalTurnaround += job.getTurnaroundTime();
            totalWaiting += job.getWaitingTime();
            output.println("Job ID: " + job.getJobId() +
                    ", Arrival Time: " + job.getArrivalTime() +
                    ", Start Time: " + job.getStartTime() +
                    ", Burst Time: " + job.getBurstTime() +
                    ", Completion Time: " + job.getCompletionTime() +
                    ", Turnaround Time: " + job.getTurnaroundTime() +
                    ", Waiting Time: " + job.getWaitingTime());
        }
        output.println("\nAverage Turnaround Time: " + totalTurnaround);
        output.println("Average Waiting Time: " + totalWaiting);
    }
}