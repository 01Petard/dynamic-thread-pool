package cn.bugstack.task;

import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.*;
import java.util.List;


/**
 * 监控系统资源，输出更多JMX信息
 * @return Runnable 监控任务
 */
public class SystemMonitor implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(SystemMonitor.class);

    private double memoryUsage;
    private double cpuUsage;

    public SystemMonitor() {
        this.memoryUsage = 80;
        this.cpuUsage = 80;
    }

    public SystemMonitor(double memoryUsage, double cpuUsage) {
        this.memoryUsage = memoryUsage;
        this.cpuUsage = cpuUsage;
    }

    // 操作系统信息 (OperatingSystemMXBean)：除了CPU负载，还包括操作系统名称、版本、架构、处理器数量和系统平均负载。
    OperatingSystemMXBean osMxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    // 内存信息 (MemoryMXBean)：堆内存和非堆内存的使用和最大值。
    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    // 线程信息 (ThreadMXBean)：线程总数、峰值线程数、守护线程数。
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    // 类加载信息 (ClassLoadingMXBean)：当前加载的类数量、卸载的类数量。
    ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();

    // 垃圾回收信息 (GarbageCollectorMXBean)：各个垃圾回收器的GC次数和时间。
    List<GarbageCollectorMXBean> gcMxBeans = ManagementFactory.getGarbageCollectorMXBeans();

    // 内存池信息 (MemoryPoolMXBean)：各个内存池（如年轻代、老年代等）的使用情况。
    List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();

    @Override
    public void run() {
        while (true) {
            try {
                StringBuilder systemInfo = loadSystemInfo();

                StringBuilder logMessage = new StringBuilder();

                // 获取 JVM 堆的使用情况
                long maxMemory = Runtime.getRuntime().maxMemory();
                long totalMemory = Runtime.getRuntime().totalMemory();
                long freeMemory = Runtime.getRuntime().freeMemory();
                long usedMemory = totalMemory - freeMemory;
                double currentMemoryUsage = (double) usedMemory / (double) totalMemory;

                // 获取系统 CPU 使用率
                double systemCpuLoad = osMxBean.getSystemCpuLoad();

                // 当 CPU 或内存使用率超过阈值时，合并日志输出
                if (systemCpuLoad * 100 > this.cpuUsage || currentMemoryUsage > this.memoryUsage) {
                    logMessage.append("\n----------------------------------\n");

                    if (systemCpuLoad * 100 > this.cpuUsage) {
                        logMessage.append("CPU usage exceeds ")
                                .append(this.cpuUsage)
                                .append(" %\n");
                    }

                    if (currentMemoryUsage * 100 > this.memoryUsage) {
                        logMessage.append("JVM Heap usage exceeds ")
                                .append(this.memoryUsage)
                                .append(" %\n");
                    }

                    logMessage.append("\n ------ Details are below:  ------\n")
                            .append("Max Memory: ").append(maxMemory / (1024 * 1024)).append(" MB\n")
                            .append("Total Memory: ").append(totalMemory / (1024 * 1024)).append(" MB\n")
                            .append("Used Memory: ").append(usedMemory / (1024 * 1024)).append(" MB\n")
                            .append("Free Memory: ").append(freeMemory / (1024 * 1024)).append(" MB\n")
                            .append("Memory Usage: ").append(String.format("%.2f", currentMemoryUsage * 100)).append(" %\n")
                            .append("CPU Usage: ").append(String.format("%.2f", systemCpuLoad * 100)).append(" %\n")
                            .append("----------------------------------");
                }
                logger.warn(logMessage.toString());
                logger.info(systemInfo.toString());
                // 休眠 3 秒后重新检测
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("系统监控线程中断！", e);
            }
        }
    }

    private StringBuilder loadSystemInfo() {
        StringBuilder systemInfo = new StringBuilder();
        // 1. 操作系统信息
        systemInfo.append("\n------ Operating System Info ------\n");
        systemInfo.append("OS Name: ").append(osMxBean.getName()).append("\n");
        systemInfo.append("OS Version: ").append(osMxBean.getVersion()).append("\n");
        systemInfo.append("Architecture: ").append(osMxBean.getArch()).append("\n");
        systemInfo.append("Available Processors: ").append(osMxBean.getAvailableProcessors()).append("\n");
        systemInfo.append("System Load Average: ").append(osMxBean.getSystemLoadAverage()).append("\n");
        systemInfo.append("CPU Usage: ").append(String.format("%.2f", osMxBean.getSystemCpuLoad() * 100)).append(" %\n");

        // 2. 内存信息
        systemInfo.append("\n------ Memory Info ------\n");
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        systemInfo.append("Heap Memory: Used/Max: ").append(heapMemoryUsage.getUsed() / (1024 * 1024)).append(" MB / ")
                .append(heapMemoryUsage.getMax() / (1024 * 1024)).append(" MB\n");

        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        systemInfo.append("Non-Heap Memory: Used/Max: ").append(nonHeapMemoryUsage.getUsed() / (1024 * 1024)).append(" MB / ")
                .append(nonHeapMemoryUsage.getMax() / (1024 * 1024)).append(" MB\n");

        // 3. 线程信息
        systemInfo.append("\n------ Thread Info ------\n");
        systemInfo.append("Thread Count: ").append(threadMXBean.getThreadCount()).append("\n");
        systemInfo.append("Peak Thread Count: ").append(threadMXBean.getPeakThreadCount()).append("\n");
        systemInfo.append("Daemon Thread Count: ").append(threadMXBean.getDaemonThreadCount()).append("\n");

        // 4. 类加载信息
        systemInfo.append("\n------ Class Loading Info ------\n");
        systemInfo.append("Loaded Class Count: ").append(classLoadingMXBean.getLoadedClassCount()).append("\n");
        systemInfo.append("Total Loaded Class Count: ").append(classLoadingMXBean.getTotalLoadedClassCount()).append("\n");
        systemInfo.append("Unloaded Class Count: ").append(classLoadingMXBean.getUnloadedClassCount()).append("\n");

        // 5. 垃圾回收信息
        systemInfo.append("\n------ Garbage Collection Info ------\n");
        for (GarbageCollectorMXBean gcMXBean : gcMxBeans) {
            systemInfo.append(gcMXBean.getName()).append(" GC Count: ").append(gcMXBean.getCollectionCount()).append(", GC Time: ")
                    .append(gcMXBean.getCollectionTime()).append(" ms\n");
        }

        // 6. 内存池信息
        systemInfo.append("\n------ Memory Pools Info ------\n");
        for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
            MemoryUsage usage = memoryPoolMXBean.getUsage();
            systemInfo.append(memoryPoolMXBean.getName()).append(" - Used/Max: ").append(usage.getUsed() / (1024 * 1024)).append(" MB / ")
                    .append(usage.getMax() / (1024 * 1024)).append(" MB\n");
        }

        return systemInfo;
    }
}
