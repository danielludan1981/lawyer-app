package com.daniellu.lawyer.springdemo1.benchmark;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ThreadPerformanceBenchmark {

    @Param({"10", "100", "1000", "5000"})
    private int taskCount;

    private ExecutorService threadPool;
    private ExecutorService virtualThreadPool;

    @Setup
    public void setup() {
        threadPool = Executors.newFixedThreadPool(200);
        virtualThreadPool = Executors.newVirtualThreadPerTaskExecutor();
    }

    @TearDown
    public void tearDown() {
        threadPool.shutdown();
        virtualThreadPool.shutdown();
    }

    /**
     * 传统线程性能测试
     */
    @Benchmark
    public void traditionalThreadBenchmark() throws ExecutionException, InterruptedException {
        Future<?>[] futures = new Future<?>[taskCount];
        for (int i = 0; i < taskCount; i++) {
            futures[i] = threadPool.submit(this::ioIntensiveTask);
        }
        for (Future<?> future : futures) {
            future.get();
        }
    }

    /**
     * 虚拟线程性能测试
     */
    @Benchmark
    public void virtualThreadBenchmark() throws ExecutionException, InterruptedException {
        Future<?>[] futures = new Future<?>[taskCount];
        for (int i = 0; i < taskCount; i++) {
            futures[i] = virtualThreadPool.submit(this::ioIntensiveTask);
        }
        for (Future<?> future : futures) {
            future.get();
        }
    }

    /**
     * 模拟IO密集型任务
     */
    private void ioIntensiveTask() {
        try {
            // 模拟IO等待10毫秒
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 运行基准测试
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(ThreadPerformanceBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }

}