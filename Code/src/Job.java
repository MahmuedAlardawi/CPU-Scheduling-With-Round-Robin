/*
          --------------------------------------------
           CPU Scheduling Using Round Robin Algorithm
                 CPCS-361 - Group project - CS1
          --------------------------------------------

         Students: -
         - Mahmued Alardawi - 2135209 - mmalardawi@stu.kau.edu.sa
         - Yazeed Alsahafi - 2036556
 */

public class Job {
    int arrivalTime;
    int jobId;
    int memoryRequired;
    int devicesRequired;
    int burstTime;
    int priority;

    int startTime = -1;
    int completionTime = -1;
    int turnaroundTime;
    int waitingTime;

    public Job() {}

    public int getArrivalTime() {return arrivalTime;}
    public void setArrivalTime(int arrivalTime) {this.arrivalTime = arrivalTime;}
    public int getJobId() {return jobId;}
    public void setJobId(int jobId) {this.jobId = jobId;}
    public int getMemoryRequired() {return memoryRequired;}
    public void setMemoryRequired(int memoryRequired) {this.memoryRequired = memoryRequired;}
    public int getDevicesRequired() {return devicesRequired;}
    public void setDevicesRequired(int devicesRequired) {this.devicesRequired = devicesRequired;}
    public int getBurstTime() {return burstTime;}
    public void setBurstTime(int burstTime) {this.burstTime = burstTime;}
    public int getPriority() {return priority;}
    public void setPriority(int priority) {this.priority = priority;}
    public int getStartTime() {return startTime;}
    public void setStartTime(int startTime) {this.startTime = startTime;}
    public int getCompletionTime() {return completionTime;}
    public void setCompletionTime(int completionTime) {this.completionTime = completionTime;}
    public int getTurnaroundTime() {return turnaroundTime;}
    public void setTurnaroundTime(int turnaroundTime) {this.turnaroundTime = turnaroundTime;}
    public int getWaitingTime() {return waitingTime;}
    public void setWaitingTime(int waitingTime) {this.waitingTime = waitingTime;}

    @Override
    public String toString() {
        return "Job{" +
                "arrivalTime=" + arrivalTime +
                ", jobId=" + jobId +
                ", memoryRequired=" + memoryRequired +
                ", devicesRequired=" + devicesRequired +
                ", burstTime=" + burstTime +
                ", priority=" + priority +
                ", startTime=" + startTime +
                ", completionTime=" + completionTime +
                ", turnaroundTime=" + turnaroundTime +
                ", waitingTime=" + waitingTime +
                '}';
    }

    public Boolean isJob () {
        return !(arrivalTime == 0 && jobId == 0 && memoryRequired == 0 && devicesRequired == 0 &&
                burstTime == 0 && priority == 0);
    }

    public boolean canProcess(int memory, int device) {
        return !(memoryRequired > memory | devicesRequired > device);
    }

    public void calculateMetrics() {
        if (completionTime >= 0 && arrivalTime >= 0) {
            turnaroundTime = completionTime - arrivalTime;
            waitingTime = turnaroundTime - burstTime;
        }
    }

    public Job copy(Job source) {
        Job copyJob = new Job();
        copyJob.arrivalTime = source.arrivalTime;
        copyJob.jobId = source.jobId;
        copyJob.memoryRequired = source.memoryRequired;
        copyJob.devicesRequired = source.devicesRequired;
        copyJob.burstTime = source.burstTime;
        copyJob.priority = source.priority;
        copyJob.startTime = source.startTime;
        copyJob.completionTime = source.completionTime;
        copyJob.turnaroundTime = source.turnaroundTime;
        copyJob.waitingTime = source.waitingTime;
        return copyJob;
    }
}