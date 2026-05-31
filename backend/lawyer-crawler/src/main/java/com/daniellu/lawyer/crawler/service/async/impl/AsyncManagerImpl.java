package com.daniellu.lawyer.crawler.service.async.impl;

import com.daniellu.lawyer.crawler.service.async.AsyncManager;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 异步任务管理器实现类
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Component
@RequiredArgsConstructor
public class AsyncManagerImpl implements AsyncManager {

    private final ThreadPoolTaskExecutor asyncTaskExecutor;

    @Override
    public Future<?> executeAsync(Runnable task) {
        return asyncTaskExecutor.submit(task);
    }

    @Override
    public <T> Future<T> executeAsync(Callable<T> task) {
        return asyncTaskExecutor.submit(task);
    }
}
