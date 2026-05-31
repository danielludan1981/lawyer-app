package com.daniellu.lawyer.crawler.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 通用数据请求聚合DTO
 * 包含多个数据请求DTO的列表，每个DTO代表一个具体的爬取请求
 *
 * @author Daniel Lu
 * @since 2026-01-18
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class CrawlAggregationRequestDTO {

    /**
     * 爬取请求列表
     */
    private List<CrawlRequest> crawlRequests;

    /**
     * 任务元数据
     */
    private TaskMetadata metadata;
}