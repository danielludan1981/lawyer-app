package com.daniellu.lawyer.crawler.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 自定义政府文章爬取请求DTO
 * 用于接收创建政府文章聚合爬取任务的请求参数
 *
 * @author Daniel Lu
 * @since 2026-01-18
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class CrawlCustomGovArticleRequest {

    /**
     * 爬取的发布时间范围（单位：天）
     */
    private int publishedInDays;
}