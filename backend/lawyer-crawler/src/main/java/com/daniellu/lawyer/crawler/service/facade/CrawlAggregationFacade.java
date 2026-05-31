package com.daniellu.lawyer.crawler.service.facade;

import com.daniellu.lawyer.crawler.dto.CrawlAggregationRequestDTO;
import com.daniellu.lawyer.crawler.dto.DataResponseAggregationDTO;

/**
 * 爬虫聚合服务接口
 * 提供聚合爬虫任务的创建、查询和取消功能
 *
 * @author Daniel Lu
 * @since 2026-01-18
 */
public interface CrawlAggregationFacade {

    /**
     * 创建聚合爬取任务
     *
     * @param request 聚合爬取请求
     * @return 聚合任务ID
     */
    String createAggregationTask(CrawlAggregationRequestDTO request);

    /**
     * 查询聚合任务状态
     *
     * @param aggregationTaskId 聚合任务ID
     * @return 聚合任务结果
     */
    DataResponseAggregationDTO getAggregationTaskResult(String aggregationTaskId);

    /**
     * 取消聚合任务
     *
     * @param aggregationTaskId 聚合任务ID
     * @return 是否取消成功
     */
    boolean cancelAggregationTask(String aggregationTaskId);
}