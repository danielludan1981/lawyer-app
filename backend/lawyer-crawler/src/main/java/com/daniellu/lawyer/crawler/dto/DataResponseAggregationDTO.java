package com.daniellu.lawyer.crawler.dto;

import java.util.List;

import com.daniellu.lawyer.crawler.enums.TaskStatus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 通用数据响应聚合DTO
 * 包含多个数据请求DTO的列表，每个DTO代表一个具体的爬取请求
 *
 * @author Daniel Lu
 * @since 2026-01-18
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class DataResponseAggregationDTO {

    /**
     * 爬取结果列表
     */
    private List<CrawlResponse> crawlResults;

    /**
     * 聚合任务元数据
     */
    private TaskMetadata metadata;

    /**
     * 聚合任务状态
     */
    private TaskStatus status;

    /**
     * 聚合任务总耗时（毫秒）
     */
    private long elapsedTime;
}