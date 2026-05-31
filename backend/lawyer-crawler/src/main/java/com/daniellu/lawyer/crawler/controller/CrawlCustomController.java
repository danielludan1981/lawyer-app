package com.daniellu.lawyer.crawler.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daniellu.lawyer.crawler.dto.CrawlCustomGovArticleRequest;
import com.daniellu.lawyer.crawler.service.facade.CrawlCustomFacade;
import com.daniellu.lawyer.common.dto.ResponseDTO;

import lombok.RequiredArgsConstructor;

/**
 * 自定义爬虫控制器
 * 处理自定义聚合任务的HTTP请求
 *
 * @author Daniel Lu
 * @since 2026-01-18
 */
@RestController
@RequestMapping("/api/crawler/custom")
@RequiredArgsConstructor
public class CrawlCustomController {

    private final CrawlCustomFacade crawlCustomFacade;

    /**
     * 创建聚合任务，用于爬取国家法律网站的法规文章
     *
     * @param request - 爬取请求参数
     * @return 聚合任务ID
     */
    @PostMapping("/aggregationTasks/govArticles")
    public ResponseDTO<String> createGovArticlesAggregationTask(@RequestBody CrawlCustomGovArticleRequest request) {
        String taskId = crawlCustomFacade.createGovArticlesAggregationTask(request.getPublishedInDays());
        return ResponseDTO.success(taskId);
    }
}