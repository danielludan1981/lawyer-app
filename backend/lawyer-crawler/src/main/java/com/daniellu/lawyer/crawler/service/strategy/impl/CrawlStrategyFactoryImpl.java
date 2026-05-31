package com.daniellu.lawyer.crawler.service.strategy.impl;

import com.daniellu.lawyer.crawler.enums.CrawlStrategy;
import com.daniellu.lawyer.crawler.service.engine.CrawlEngine;
import com.daniellu.lawyer.crawler.service.strategy.CrawlStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 爬取策略工厂实现类
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Component
@Slf4j
public class CrawlStrategyFactoryImpl implements CrawlStrategyFactory, InitializingBean {

    @Autowired
    private Set<CrawlEngine> crawlEngines;

    private final Map<CrawlStrategy, CrawlEngine> engineRegistry = new HashMap<>();

    @Override
    public CrawlEngine getEngine(CrawlStrategy strategy) {
        CrawlEngine engine = engineRegistry.get(strategy);
        if (engine == null) {
            throw new IllegalArgumentException("Unsupported crawl strategy: " + strategy);
        }
        return engine;
    }

    @Override
    public CrawlEngine getEngine(String strategyName) {
        CrawlStrategy strategy;
        try {
            strategy = CrawlStrategy.valueOf(strategyName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported crawl strategy: " + strategyName);
        }
        return getEngine(strategy);
    }

    @Override
    public void registerEngine(CrawlEngine engine) {
        try {
            CrawlStrategy strategy = CrawlStrategy.valueOf(engine.getSupportedStrategy().toUpperCase());
            engineRegistry.put(strategy, engine);
            log.info("Registered crawl engine for strategy: {}", strategy);
        } catch (IllegalArgumentException e) {
            log.error("Failed to register engine with unsupported strategy: {}", engine.getSupportedStrategy(), e);
            throw new IllegalArgumentException("Unsupported strategy: " + engine.getSupportedStrategy());
        }
    }

    @Override
    public Iterable<CrawlStrategy> getAvailableStrategies() {
        return engineRegistry.keySet();
    }

    @Override
    public void afterPropertiesSet() {
        // 自动注册所有CrawlEngine类型的Spring Bean
        for (CrawlEngine engine : crawlEngines) {
            registerEngine(engine);
        }
        log.info("Crawl strategy factory initialized with {} engines", engineRegistry.size());
    }
}
