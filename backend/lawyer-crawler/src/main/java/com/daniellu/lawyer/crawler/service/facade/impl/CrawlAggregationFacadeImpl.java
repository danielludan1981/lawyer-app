package com.daniellu.lawyer.crawler.service.facade.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.daniellu.lawyer.crawler.dto.CrawlAggregationRequestDTO;
import com.daniellu.lawyer.crawler.dto.CrawlResponse;
import com.daniellu.lawyer.crawler.dto.DataResponseAggregationDTO;
import com.daniellu.lawyer.crawler.dto.TaskMetadata;
import com.daniellu.lawyer.crawler.enums.TaskStatus;
import com.daniellu.lawyer.crawler.service.facade.CrawlAggregationFacade;
import com.daniellu.lawyer.crawler.service.facade.CrawlFacade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 爬虫聚合服务实现类
 * 处理聚合爬虫任务的创建、查询和取消功能
 *
 * @author Daniel Lu
 * @since 2026-01-18
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CrawlAggregationFacadeImpl implements CrawlAggregationFacade {

    private final CrawlFacade crawlFacade;

    // 存储聚合任务结果，key为聚合任务ID，value为子任务ID列表和创建时间
    private final Map<String, AggregationTaskInfo> aggregationTaskMap = new ConcurrentHashMap<>();

    // 聚合任务信息内部类
    private static class AggregationTaskInfo {
        private final List<String> subtaskIds = new ArrayList<>();
        private final long createdAt = System.currentTimeMillis();
        private TaskMetadata metadata;
        private final Map<String, CrawlResponse> subtaskResults = new ConcurrentHashMap<>();
    }

    @Override
    public String createAggregationTask(CrawlAggregationRequestDTO request) {
        // 生成唯一聚合任务ID
        String aggregationTaskId = UUID.randomUUID().toString();

        // 初始化聚合任务元数据
        TaskMetadata metadata = TaskMetadata.builder()
                .taskId(aggregationTaskId)
                .createdAt(System.currentTimeMillis())
                .createdByUserName(request.getMetadata() != null ? request.getMetadata().getCreatedByUserName() : "system")
                .taskName("聚合爬虫任务")
                .description(request.getMetadata() != null ? request.getMetadata().getDescription() : null)
                .build();

        // 创建聚合任务信息
        AggregationTaskInfo taskInfo = new AggregationTaskInfo();
        taskInfo.metadata = metadata;
        aggregationTaskMap.put(aggregationTaskId, taskInfo);

        // 并行执行所有子任务
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (com.daniellu.lawyer.crawler.dto.CrawlRequest crawlRequest : request.getCrawlRequests()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // 执行子任务
                    String subtaskId = crawlFacade.startCrawlTask(crawlRequest);
                    synchronized (taskInfo) {
                        taskInfo.subtaskIds.add(subtaskId);
                    }
                } catch (Exception e) {
                    log.error("Failed to start subtask: {}", e.getMessage(), e);
                }
            });
            futures.add(future);
        }

        // 等待所有子任务启动完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            log.info("Aggregation task {} started with {} subtasks", aggregationTaskId, taskInfo.subtaskIds.size());
        });

        return aggregationTaskId;
    }

    @Override
    public DataResponseAggregationDTO getAggregationTaskResult(String aggregationTaskId) {
        // 获取聚合任务信息
        AggregationTaskInfo taskInfo = aggregationTaskMap.get(aggregationTaskId);
        if (taskInfo == null) {
            return null;
        }

        // 更新子任务结果
        updateSubtaskResults(taskInfo);

        // 计算聚合任务状态
        TaskStatus status = calculateAggregationStatus(taskInfo);

        // 计算总耗时
        long elapsedTime = System.currentTimeMillis() - taskInfo.createdAt;

        // 创建并返回聚合任务结果
        return DataResponseAggregationDTO.builder()
                .crawlResults(new ArrayList<>(taskInfo.subtaskResults.values()))
                .metadata(taskInfo.metadata)
                .status(status)
                .elapsedTime(elapsedTime)
                .build();
    }

    @Override
    public boolean cancelAggregationTask(String aggregationTaskId) {
        // 获取聚合任务信息
        AggregationTaskInfo taskInfo = aggregationTaskMap.get(aggregationTaskId);
        if (taskInfo == null) {
            return false;
        }

        // 取消所有子任务
        boolean allCancelled = true;
        for (String subtaskId : taskInfo.subtaskIds) {
            boolean cancelled = crawlFacade.cancelTask(subtaskId);
            if (!cancelled) {
                allCancelled = false;
            }
        }

        // 聚合任务状态由calculateAggregationStatus方法动态计算，不需要手动更新

        return allCancelled;
    }

    /**
     * 更新子任务结果
     */
    private void updateSubtaskResults(AggregationTaskInfo taskInfo) {
        for (String subtaskId : taskInfo.subtaskIds) {
            if (!taskInfo.subtaskResults.containsKey(subtaskId)) {
                CrawlResponse subtaskResult = crawlFacade.getTaskResult(subtaskId);
                if (subtaskResult != null && subtaskResult.getStatus() != TaskStatus.RUNNING) {
                    taskInfo.subtaskResults.put(subtaskId, subtaskResult);
                }
            }
        }
    }

    /**
     * 计算聚合任务状态
     */
    private TaskStatus calculateAggregationStatus(AggregationTaskInfo taskInfo) {
        // 检查是否有子任务正在运行
        boolean hasRunning = false;
        boolean hasFailed = false;

        for (String subtaskId : taskInfo.subtaskIds) {
            CrawlResponse subtaskResult = taskInfo.subtaskResults.get(subtaskId);
            if (subtaskResult == null) {
                // 子任务结果尚未获取，认为正在运行
                hasRunning = true;
            } else {
                switch (subtaskResult.getStatus()) {
                    case RUNNING:
                        hasRunning = true;
                        break;
                    case FAILED:
                        hasFailed = true;
                        break;
                    case CANCELLED:
                        return TaskStatus.CANCELLED;
                    default:
                        break;
                }
            }
        }

        if (hasRunning) {
            return TaskStatus.RUNNING;
        }

        if (hasFailed) {
            return TaskStatus.FAILED;
        }

        return TaskStatus.SUCCESS;
    }
}