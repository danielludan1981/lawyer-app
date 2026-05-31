package com.daniellu.lawyer.crawler.service.strategy;

import com.daniellu.lawyer.crawler.enums.CrawlStrategy;
import com.daniellu.lawyer.crawler.service.engine.CrawlEngine;

/**
 * 爬取策略工厂
 * 用于创建和管理不同的爬取引擎实例
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
public interface CrawlStrategyFactory {

    /**
     * 根据策略获取对应的爬取引擎
     *
     * @param strategy 爬取策略
     * @return 爬取引擎实例
     */
    CrawlEngine getEngine(CrawlStrategy strategy);

    /**
     * 根据策略名称获取对应的爬取引擎
     *
     * @param strategyName 策略名称
     * @return 爬取引擎实例
     */
    CrawlEngine getEngine(String strategyName);

    /**
     * 注册新的爬取引擎
     *
     * @param engine 爬取引擎实例
     */
    void registerEngine(CrawlEngine engine);

    /**
     * 获取所有可用的爬取策略
     *
     * @return 可用策略列表
     */
    Iterable<CrawlStrategy> getAvailableStrategies();
}
