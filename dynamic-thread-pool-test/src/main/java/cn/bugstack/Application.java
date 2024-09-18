package cn.bugstack;

import com.sun.management.OperatingSystemMXBean;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class Application {

    // 任务id计数器
    private static final AtomicInteger taskId = new AtomicInteger(0);

    @Resource
    ExecutorService tpe_01;

    @Resource
    ExecutorService tpe_02;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        OperatingSystemMXBean osMxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return args -> {
            while (true) {
                // 创建一个随机时间生成器
                Random random = new Random();
                // 随机时间，用于模拟任务启动延迟
                int initialDelay = random.nextInt(10) + 1; // 1到10秒之间
                // 随机休眠时间，用于模拟任务执行时间
                int sleepTime = random.nextInt(10) + 1; // 1到10秒之间

                // 提交任务到线程池
                tpe_01.submit(() -> {
                    int currentTaskId = taskId.getAndIncrement();
                    try {
                        // 模拟任务启动延迟
                        System.out.println("启动任务" + currentTaskId + "中，" + initialDelay + " 秒后执行...");
                        TimeUnit.SECONDS.sleep(initialDelay);
                        // 模拟任务执行
                        System.out.println("任务" + currentTaskId + "执行中... ");
                        TimeUnit.SECONDS.sleep(sleepTime);
                        System.out.println("任务" + currentTaskId + "执行完毕，耗时 " + sleepTime + " 秒.");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("任务" + currentTaskId + "中断！");
                    }
                });
                try {
                    Thread.sleep(random.nextInt(50) + 1); // 模拟提交任务的时间间隔
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("主线程中断！");
                }

                new Thread(() -> {
                    OperatingSystemMXBean osMxBean1 = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                    try {
                        // 获取系统内存信息
                        long totalMemory = Runtime.getRuntime().totalMemory();
                        long freeMemory = Runtime.getRuntime().freeMemory();
                        long maxMemory = Runtime.getRuntime().maxMemory();
                        long usedMemory = totalMemory - freeMemory;

                        // 获取 CPU 使用率
                        double systemCpuLoad = osMxBean1.getSystemCpuLoad();

                        // 打印资源使用情况
                        System.out.println("------------------");
                        System.out.println("Total Memory: " + totalMemory / (1024 * 1024) + " MB");
                        System.out.println("Free Memory: " + freeMemory / (1024 * 1024) + " MB");
                        System.out.println("Used Memory: " + usedMemory / (1024 * 1024) + " MB");
                        System.out.println("Max Memory: " + maxMemory / (1024 * 1024) + " MB");
                        System.out.println("System CPU Load: " + String.format("%.2f", systemCpuLoad * 100) + " %");

                        Thread.sleep(10000); // 每隔5秒检查一次
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("资源监控线程中断！");
                    }

                }).start();
            }
        };
    }

    // 定期监控系统资源使用情况


    // 用于测试的 ExecutorService Bean
    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10); // 创建固定大小的线程池
    }
}
