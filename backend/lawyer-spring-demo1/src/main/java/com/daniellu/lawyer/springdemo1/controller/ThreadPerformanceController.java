package com.daniellu.lawyer.springdemo1.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/performance")
public class ThreadPerformanceController {

    // 线程池用于传统线程测试
    private final ExecutorService threadPool = Executors.newFixedThreadPool(100);

    // 虚拟线程执行器
    private final ExecutorService virtualThreadPool = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * 使用传统线程执行并发任务
     */
    @GetMapping("/traditional/{count}")
    public String testTraditionalThreads(@PathVariable int count) {
        if (count <= 0 || count > 10000) {
            return "请指定1-10000之间的任务数量";
        }

        LocalDateTime start = LocalDateTime.now();
        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < count; i++) {
                final int taskId = i;
                futures.add(threadPool.submit(() -> {
                    // 模拟IO等待
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return taskId;
                }));
            }

            // 等待所有任务完成
            for (Future<?> future : futures) {
                future.get();
            }

            LocalDateTime end = LocalDateTime.now();
            Duration duration = Duration.between(start, end);
            return String.format("传统线程执行%d个任务耗时: %d毫秒", count, duration.toMillis());
        } catch (Exception e) {
            return "执行过程中发生错误: " + e.getMessage();
        }
    }

    /**
     * 使用虚拟线程执行并发任务
     */
    @GetMapping("/virtual/{count}")
    public String testVirtualThreads(@PathVariable int count) {
        if (count <= 0 || count > 10000) {
            return "请指定1-10000之间的任务数量";
        }

        LocalDateTime start = LocalDateTime.now();
        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < count; i++) {
                final int taskId = i;
                futures.add(virtualThreadPool.submit(() -> {
                    // 模拟IO等待
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return taskId;
                }));
            }

            // 等待所有任务完成
            for (Future<?> future : futures) {
                future.get();
            }

            LocalDateTime end = LocalDateTime.now();
            Duration duration = Duration.between(start, end);
            return String.format("虚拟线程执行%d个任务耗时: %d毫秒", count, duration.toMillis());
        } catch (Exception e) {
            return "执行过程中发生错误: " + e.getMessage();
        }
    }

    /**
     * 模拟长时间运行的任务（使用虚拟线程）
     */
    @GetMapping("/long-task")
    public String longRunningTask() {
        return "任务已开始，将在后台运行...";
    }

    /**
     * 获取当前线程信息
     */
    @GetMapping("/thread-info")
    public String getThreadInfo() {
        Thread currentThread = Thread.currentThread();
        return String.format(
                "线程名称: %s\n是否为虚拟线程: %s\n线程状态: %s\n线程优先级: %d",
                currentThread.getName(),
                currentThread.isVirtual(),
                currentThread.getState(),
                currentThread.getPriority()
        );
    }

}