package com.daniellu.lawyer.crawler.controller;

import com.daniellu.lawyer.crawler.dto.CrawlAggregationRequestDTO;
import com.daniellu.lawyer.crawler.dto.CrawlRequest;
import com.daniellu.lawyer.crawler.dto.CrawlResponse;
import com.daniellu.lawyer.crawler.dto.DataResponseAggregationDTO;
import com.daniellu.lawyer.crawler.service.facade.CrawlAggregationFacade;
import com.daniellu.lawyer.crawler.service.facade.CrawlFacade;
import com.daniellu.lawyer.common.dto.ResponseDTO;
import com.daniellu.lawyer.common.exception.BusinessException;
import com.daniellu.lawyer.common.constant.CommonErrCode;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 爬虫控制器
 * 处理爬取任务的HTTP请求
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@RestController
@RequestMapping("/api/crawler")
@RequiredArgsConstructor
public class CrawlController {

    private final CrawlFacade crawlFacade;
    private final CrawlAggregationFacade crawlAggregationService;

    /**
     * 创建爬取任务
     *
     * @param request 爬取请求参数
     * @return 包含任务ID的响应
     */
    @PostMapping("/tasks")
    public ResponseDTO<String> createCrawlTask(@RequestBody CrawlRequest request) {
        String taskId = crawlFacade.startCrawlTask(request);
        return ResponseDTO.success(taskId);
    }

    /**
     * 查询任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态响应
     */
    @GetMapping("/tasks/{taskId}")
    public ResponseDTO<CrawlResponse> getTaskStatus(@PathVariable String taskId) {
        CrawlResponse result = crawlFacade.getTaskResult(taskId);
        if (result == null) {
            throw new BusinessException(CommonErrCode.BUS_DATA_NOT_FOUND_ERROR, "爬虫任务不存在");
        }
        return ResponseDTO.success(result);
    }

    /**
     * 取消爬取任务
     *
     * @param taskId 任务ID
     * @return 取消结果响应
     */
    @DeleteMapping("/tasks/{taskId}")
    public ResponseDTO<Boolean> cancelTask(@PathVariable String taskId) {
        // 先检查任务是否存在
        CrawlResponse result = crawlFacade.getTaskResult(taskId);
        if (result == null) {
            throw new BusinessException(CommonErrCode.BUS_DATA_NOT_FOUND_ERROR, "爬虫任务不存在");
        }
        boolean cancelled = crawlFacade.cancelTask(taskId);
        return ResponseDTO.success(cancelled);
    }

    /**
     * 创建聚合爬取任务
     *
     * @param request 聚合爬取请求
     * @return 包含聚合任务ID的响应
     */
    @PostMapping("/aggregationTasks")
    public ResponseDTO<String> createAggregationTask(@RequestBody CrawlAggregationRequestDTO request) {
        String taskId = crawlAggregationService.createAggregationTask(request);
        return ResponseDTO.success(taskId);
    }

    /**
     * 查询聚合任务状态
     *
     * @param aggregationTaskId 聚合任务ID
     * @return 聚合任务结果响应
     */
    @GetMapping("/aggregationTasks/{aggregationTaskId}")
    public ResponseDTO<DataResponseAggregationDTO> getAggregationTaskStatus(@PathVariable String aggregationTaskId) {
        DataResponseAggregationDTO result = crawlAggregationService.getAggregationTaskResult(aggregationTaskId);
        if (result == null) {
            throw new BusinessException(CommonErrCode.BUS_DATA_NOT_FOUND_ERROR, "聚合爬虫任务不存在");
        }
        return ResponseDTO.success(result);
    }

    /**
     * 取消聚合任务（取消所有关联子任务）
     *
     * @param aggregationTaskId 聚合任务ID
     * @return 取消结果响应
     */
    @DeleteMapping("/aggregationTasks/{aggregationTaskId}")
    public ResponseDTO<Boolean> cancelAggregationTask(@PathVariable String aggregationTaskId) {
        // 先检查任务是否存在
        DataResponseAggregationDTO result = crawlAggregationService.getAggregationTaskResult(aggregationTaskId);
        if (result == null) {
            throw new BusinessException(CommonErrCode.BUS_DATA_NOT_FOUND_ERROR, "聚合爬虫任务不存在");
        }
        boolean cancelled = crawlAggregationService.cancelAggregationTask(aggregationTaskId);
        return ResponseDTO.success(cancelled);
    }
}
