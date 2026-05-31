package com.daniellu.lawyer.crawler.service.facade;

import com.daniellu.lawyer.crawler.dto.CrawlRequest;
import com.daniellu.lawyer.crawler.dto.CrawlResponse;

/**
 * 爬虫门面接口
 * 作为系统入口屏蔽底层细节，协调异步任务的生命周期与通知触发
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
public interface CrawlFacade {

    /**
     * 启动爬取任务
     *
     * @param request 爬取请求参数
     * @return 任务ID
     */
    String startCrawlTask(CrawlRequest request);

    /**
     * 获取任务结果
     *
     * @param taskId 任务ID
     * @return 爬取结果
     */
    CrawlResponse getTaskResult(String taskId);

    /**
     * 取消任务
     *
     * @param taskId 任务ID
     * @return 是否取消成功
     */
    boolean cancelTask(String taskId);
}

