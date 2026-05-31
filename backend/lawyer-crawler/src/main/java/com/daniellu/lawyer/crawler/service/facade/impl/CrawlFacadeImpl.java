package com.daniellu.lawyer.crawler.service.facade.impl;

import com.daniellu.lawyer.common.dto.ErrorDTO;
import com.daniellu.lawyer.crawler.dto.*;
import com.daniellu.lawyer.crawler.enums.*;
import com.daniellu.lawyer.crawler.service.async.AsyncManager;
import com.daniellu.lawyer.crawler.service.engine.CrawlEngine;
import com.daniellu.lawyer.crawler.service.facade.CrawlFacade;
import com.daniellu.lawyer.crawler.service.strategy.CrawlStrategyFactory;
import com.daniellu.lawyer.crawler.service.strategy.CrawlStrategyResolver;
import com.daniellu.lawyer.crawler.websocket.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * 爬虫门面实现类
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Service
@RequiredArgsConstructor
public class CrawlFacadeImpl implements CrawlFacade {

    private final AsyncManager asyncManager;
    private final CrawlStrategyFactory strategyFactory;
    private final CrawlStrategyResolver strategyResolver;
    private final WebSocketHandler webSocketHandler;

    // 存储任务结果
    private final Map<String, CrawlResponse> taskResults = new ConcurrentHashMap<>();
    // 存储任务Future，用于取消任务
    private final Map<String, Future<?>> taskFutures = new ConcurrentHashMap<>();

    @Override
    public String startCrawlTask(CrawlRequest request) {
        // 生成唯一任务ID
        String taskId = UUID.randomUUID().toString();

        // 初始化任务元数据
        TaskMetadata metadata = TaskMetadata.builder()
                .taskId(taskId)
                .createdAt(System.currentTimeMillis())
                .createdByUserName("system")
                .taskName(request.getMetadata() != null ? request.getMetadata().getTaskName() : null)
                .description(request.getMetadata() != null ? request.getMetadata().getDescription() : null)
                .build();
        request.setMetadata(metadata);

        // 解析并确定爬取策略
        CrawlStrategy strategy = resolveStrategy(request);
        request.setStrategy(strategy);

        // 初始化任务结果
        CrawlResponse initialResult = CrawlResponse.builder()
                .metadata(metadata)
                .status(TaskStatus.CREATED)
                .usedStrategy(strategy)
                .build();
        taskResults.put(taskId, initialResult);

        // 启动异步爬取任务
        Future<?> future = asyncManager.executeAsync(() -> {
            executeCrawlTask(request);
        });
        taskFutures.put(taskId, future);

        return taskId;
    }

    @Override
    public CrawlResponse getTaskResult(String taskId) {
        return taskResults.get(taskId);
    }

    @Override
    public boolean cancelTask(String taskId) {
        Future<?> future = taskFutures.remove(taskId);
        if (future != null && !future.isDone()) {
            boolean cancelled = future.cancel(true);
            if (cancelled) {
                CrawlResponse result = taskResults.get(taskId);
                if (result != null) {
                    result.setStatus(TaskStatus.CANCELLED);
                    ErrorDTO errorDTO = new ErrorDTO();
                    errorDTO.setMessage("Task cancelled by user");
                    result.setError(errorDTO);
                }
                webSocketHandler.sendFailureNotification(taskId, "任务已取消");
            }
            return cancelled;
        }
        return false;
    }

    /**
     * 解析并确定爬取策略
     */
    private CrawlStrategy resolveStrategy(CrawlRequest request) {
        if (request.getStrategy() != null && request.getStrategy() != CrawlStrategy.AUTO) {
            return request.getStrategy();
        }
        return strategyResolver.resolveStrategy(request.getUrl());
    }

    /**
     * 执行爬取任务
     */
    private void executeCrawlTask(CrawlRequest request) {
        String taskId = request.getMetadata().getTaskId();
        long startTime = System.currentTimeMillis();

        try {
            // 更新任务状态为运行中
            CrawlResponse result = taskResults.get(taskId);
            result.setStatus(TaskStatus.RUNNING);

            // 获取对应的爬取引擎
            CrawlEngine engine = strategyFactory.getEngine(request.getStrategy());

            // 执行爬取
            CrawlResponse crawlResult = engine.execute(request);

            // 更新任务结果
            crawlResult.setElapsedTime(System.currentTimeMillis() - startTime);
            taskResults.put(taskId, crawlResult);

            // 发送完成通知
            webSocketHandler.sendCompletionNotification(crawlResult);

        } catch (Exception e) {
            // 处理异常情况
            CrawlResponse result = taskResults.get(taskId);
            result.setStatus(TaskStatus.FAILED);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage(e.getMessage());
            errorDTO.setDetails(getStackTraceAsString(e));
            result.setError(errorDTO);
            result.setElapsedTime(System.currentTimeMillis() - startTime);

            // 发送失败通知
            webSocketHandler.sendFailureNotification(taskId, e.getMessage());
        } finally {
            // 清理任务Future
            taskFutures.remove(taskId);
        }
    }

    /**
     * 将异常堆栈转换为字符串
     */
    private String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
