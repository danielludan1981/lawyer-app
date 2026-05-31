package com.daniellu.lawyer.crawler.service.async;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 异步任务管理器
 * 提供异步执行爬取任务的能力
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
public interface AsyncManager {

    /**
     * 异步执行任务
     *
     * @param task 要执行的任务
     * @return Future对象，用于跟踪任务执行状态
     */
    Future<?> executeAsync(Runnable task);

    /**
     * 异步执行带返回值的任务
     *
     * @param task 要执行的任务
     * @param <T>  返回值类型
     * @return Future对象，用于获取任务执行结果
     */
    <T> Future<T> executeAsync(Callable<T> task);

}
