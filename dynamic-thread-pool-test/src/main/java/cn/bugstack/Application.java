package cn.bugstack;

import cn.bugstack.task.SystemMonitor;
import cn.bugstack.task.ThreadPoolSimulation;
import org.apache.catalina.Executor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class Application {

    // 任务id计数器
    private static final AtomicInteger taskId = new AtomicInteger(1);

    @Resource
    ExecutorService tpe_01;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            // 启动系统监控线程，只需要启动一次
            Thread monitorThread = new Thread(new SystemMonitor(25,10));
            monitorThread.setDaemon(true); // 设置为守护线程，主程序退出时监控线程自动结束
            monitorThread.start();

            // 创建并运行线程池任务，不需要每次循环重新启动监控线程
            while (true) {
                ThreadPoolSimulation threadPoolSimulation = new ThreadPoolSimulation(taskId.getAndIncrement());
                tpe_01.submit(threadPoolSimulation);
                try {
                    Thread.sleep(new Random().nextInt(500) + 1); // 模拟提交任务的时间间隔
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("主线程中断！");
                }
            }
        };
    }

}
