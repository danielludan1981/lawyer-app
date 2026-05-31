package com.daniellu.lawyer.crawler.dto;

import com.daniellu.lawyer.crawler.enums.CrawlStrategy;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 爬取请求模型
 * 包含目标URL、字段选择器、任务元数据等信息
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class CrawlRequest {

    /**
     * 目标URL
     */
    private String url;

    /**
     * 爬取策略（AUTO, JSOUP, PLAYWRIGHT）
     */
    private CrawlStrategy strategy;

    /**
     * 字段选择器配置
     */
    private SelectorConfig selectorConfig;

    /**
     * 任务元数据（用于识别和跟踪任务）
     */
    private TaskMetadata metadata;

    /**
     * 爬取配置选项
     */
    private CrawlOptions options;

    /**
     * 分页配置, 当提供分页配置时，会根据URL拼接分页部分爬取多页数据
     */
    private CrawlPagination pagination;

}