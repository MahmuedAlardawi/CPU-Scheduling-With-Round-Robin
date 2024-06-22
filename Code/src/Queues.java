/*
          --------------------------------------------
           CPU Scheduling Using Round Robin Algorithm
                 CPCS-361 - Group project - CS1
          --------------------------------------------

         Students: -
         - Mahmued Alardawi - 2135209 - mmalardawi@stu.kau.edu.sa
         - Yazeed Alsahafi - 2036556
 */

import java.util.*;

public class Queues {
    static Queue<Job> readyQueue = new LinkedList<>();
    static Queue<Job> holdQueue1 = new PriorityQueue<>(Comparator.comparingInt(j -> j.memoryRequired));
    static Queue<Job> holdQueue2 = new LinkedList<>();

    public Queues() {}

    public Queue<Job> getReadyQueue() {return readyQueue;}
    public Queue<Job> getHoldQueue1() {return holdQueue1;}
    public Queue<Job> getHoldQueue2() {return holdQueue2;}

    @Override
    public String toString() {
        return "Queues{" +
                "readyQueue=" + readyQueue +
                ", holdQueue1=" + holdQueue1 +
                ", holdQueue2=" + holdQueue2 +
                '}';
    }

    public  boolean areEmpty () {
        return (readyQueue.isEmpty() && holdQueue1.isEmpty() && holdQueue2.isEmpty());
    }

    public  boolean areHoldingQueuesEmpty () {
        return (holdQueue1.isEmpty() && holdQueue2.isEmpty());
    }
}