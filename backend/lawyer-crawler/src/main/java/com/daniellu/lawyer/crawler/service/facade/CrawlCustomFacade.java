package com.daniellu.lawyer.crawler.service.facade;

/**
 * 自定义爬虫门面接口
 * 负责处理自定义聚合任务的业务逻辑
 *
 * @author Daniel Lu
 * @since 2026-01-18
 */
public interface CrawlCustomFacade {

    /**
     * 创建一个聚合任务，用于爬取国家法律网站的法规文章
     *
     * @param publishedInDays - 爬取的发布时间范围（单位：天）
     * @return 聚合任务ID
     */
    String createGovArticlesAggregationTask(int publishedInDays);
}