package com.daniellu.lawyer.crawler.enums;

/**
 * 爬取策略枚举
 * 定义可用的爬取引擎策略
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
public enum CrawlStrategy {
    
    /**
     * 自动策略
     * 根据URL特征和内容自动选择合适的引擎
     */
    AUTO,
    
    /**
     * Jsoup引擎
     * 适用于静态页面爬取
     */
    JSOUP,
    
    /**
     * Playwright引擎
     * 适用于动态页面爬取
     */
    PLAYWRIGHT
}
