package com.daniellu.lawyer.crawler.service.strategy;

import com.daniellu.lawyer.crawler.enums.CrawlStrategy;

/**
 * 策略解析器
 * 基于URL特征、SEO标记或配置中心决定采用哪种爬取引擎
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
public interface CrawlStrategyResolver {

    /**
     * 解析并返回适合的爬取策略
     *
     * @param url 目标URL
     * @return 适合的爬取策略
     */
    CrawlStrategy resolveStrategy(String url);

    /**
     * 预爬取嗅探，检查页面内容以确定合适的策略
     *
     * @param url 目标URL
     * @return 适合的爬取策略
     */
    CrawlStrategy sniffPageContent(String url);
}
