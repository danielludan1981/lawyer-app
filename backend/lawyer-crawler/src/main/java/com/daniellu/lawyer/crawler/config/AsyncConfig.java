package com.daniellu.lawyer.crawler.config;

import java.util.concurrent.ThreadFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步配置类
 * 配置Spring异步执行器，使用虚拟线程
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "asyncTaskExecutor")
    public ThreadPoolTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Async-Crawler-");

        // 使用虚拟线程工厂
        executor.setThreadFactory(new VirtualThreadFactory());

        executor.initialize();
        return executor;
    }

    /**
     * 虚拟线程工厂
     */
    private static class VirtualThreadFactory implements ThreadFactory {
        private final ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();

        @Override
        public Thread newThread(Runnable r) {
            return virtualThreadFactory.newThread(r);
        }
    }
}
