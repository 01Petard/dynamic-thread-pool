package cn.bugstack.task;

import lombok.Data;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 模拟线程池任务
 * @return Runnable 模拟任务
 */
public class ThreadPoolSimulation implements Runnable {

    private final SimulatedTask task;

    public ThreadPoolSimulation(int taskId) {
        Random random = new Random();
        this.task = new SimulatedTask(taskId, random.nextInt(10), random.nextInt(10));
    }

    @Override
    public void run() {
        try {
            System.out.println("启动任务" + task.getTaskId() + "中，" + task.getInitialDelay() + " 秒后执行...");
            TimeUnit.SECONDS.sleep(task.getInitialDelay());
            System.out.println("任务" + task.getTaskId() + "执行中...");
            TimeUnit.SECONDS.sleep(task.getSleepTime());
            System.out.println("任务" + task.getTaskId() + "执行完毕，耗时 " + task.getSleepTime() + " 秒.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("任务" + task.getTaskId() + "中断！");
        }
    }
}

@Data
class SimulatedTask {
    private int taskId;
    private int initialDelay;
    private int sleepTime;

    SimulatedTask(int taskId, int initialDelay, int sleepTime) {
        this.taskId = taskId;
        this.initialDelay = initialDelay;
        this.sleepTime = sleepTime;
    }
}

