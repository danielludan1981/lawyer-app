package com.daniellu.lawyer.crawler.service.engine;

import com.daniellu.lawyer.crawler.dto.CrawlRequest;
import com.daniellu.lawyer.crawler.dto.CrawlResponse;

/**
 * 爬取引擎接口
 * 定义爬取引擎的通用方法
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
public interface CrawlEngine {

    /**
     * 执行爬取任务
     *
     * @param request 爬取请求参数
     * @return 爬取结果
     */
    CrawlResponse execute(CrawlRequest request);

    /**
     * 获取支持的策略
     *
     * @return 支持的爬取策略
     */
    String getSupportedStrategy();

    /**
     * 检查引擎是否可用
     *
     * @return 引擎是否可用
     */
    boolean isAvailable();
}
