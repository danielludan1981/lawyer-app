package com.daniellu.lawyer.crawler.service.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.daniellu.lawyer.crawler.dto.CrawlAggregationRequestDTO;
import com.daniellu.lawyer.crawler.dto.CrawlRequest;
import com.daniellu.lawyer.crawler.service.facade.CrawlAggregationFacade;
import com.daniellu.lawyer.crawler.service.facade.CrawlCustomFacade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义爬虫门面实现类
 * 处理自定义聚合任务的业务逻辑
 *
 * @author Daniel Lu
 * @since 2026-01-18
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CrawlCustomFacadeImpl implements CrawlCustomFacade {

    private final CrawlAggregationFacade crawlAggregationFacade;

    @Override
    public String createGovArticlesAggregationTask(int publishedInDays) {
        // 创建聚合请求
        CrawlAggregationRequestDTO request = new CrawlAggregationRequestDTO();
        List<CrawlRequest> crawlRequests = new ArrayList<>();

        // 添加中国人大网-权威发布请求
        crawlRequests.add(createCrawlRequest(
            "http://www.npc.gov.cn/npc/c2/c12435",
            "中国人大网-权威发布",
            ".s_lw li",
            publishedInDays,
            new String[]{"/index.html", "/index_1.html", "/index_2.html"}
        ));

        // 添加国务院官网-最新政策请求
        crawlRequests.add(createCrawlRequest(
            "https://www.gov.cn/zhengce/zuixin",
            "国务院官网-最新政策",
            ".list li h4",
            publishedInDays,
            new String[]{"/home.htm", "/home_1.htm", "/home_2.htm"}
        ));

        // 添加国务院官网-政策解读请求
        crawlRequests.add(createCrawlRequest(
            "https://www.gov.cn/zhengce/jiedu",
            "国务院官网-政策解读",
            ".list li h4",
            publishedInDays,
            new String[]{"/index.htm"}
        ));

        // 添加最高人民法院-权威发布请求
        crawlRequests.add(createCrawlRequest(
            "https://www.court.gov.cn/fabu.html",
            "最高人民法院-权威发布",
            ".sec_list li",
            publishedInDays,
            null
        ));

        // 添加人民法院报-法答网请求
        crawlRequests.add(createCrawlRequest(
            "https://www.rmfyb.com/search.html?wd=%E6%B3%95%E7%AD%94%E7%BD%91",
            "人民法院报-法答网",
            ".search_content a",
            publishedInDays,
            null
        ));

        // 添加国务院国资委官网-政策发布请求
        crawlRequests.add(createCrawlRequest(
            "http://www.sasac.gov.cn/n2588035/n2588320/n2588335/index.html",
            "国务院国资委官网-政策发布",
            ".zsy_conlist li",
            publishedInDays,
            null
        ));

        // 添加国务院国资委官网-政策解读请求
        crawlRequests.add(createCrawlRequest(
            "http://www.sasac.gov.cn/n2588035/n2588320/n2588340/index.html",
            "国务院国资委官网-政策解读",
            ".zsy_conlist li",
            publishedInDays,
            null
        ));

        // 添加上海市国资委官网-信息公开请求
        crawlRequests.add(createCrawlRequest(
            "https://www.gzw.sh.gov.cn/shgzw_xxgk/",
            "上海市国资委官网-信息公开",
            ".tab-content li",
            publishedInDays,
            null
        ));

        // 添加国务院国资委官网-互动交流
        crawlRequests.add(createCrawlRequest(
            "http://www.sasac.gov.cn/hdjl_index.html",
            "国务院国资委官网-互动交流",
            ".wrz-five-box dl",
            publishedInDays,
            null
        ));

        request.setCrawlRequests(crawlRequests);

        // 创建聚合任务并返回任务ID
        return crawlAggregationFacade.createAggregationTask(request);
    }

    /**
     * 创建爬取请求
     */
    private CrawlRequest createCrawlRequest(String url, String taskName, String listSelector, int publishedInDays, String[] urlSegments) {
        CrawlRequest request = new CrawlRequest();
        request.setUrl(url);
        request.setStrategy(com.daniellu.lawyer.crawler.enums.CrawlStrategy.JSOUP);

        // 创建选择器配置
        com.daniellu.lawyer.crawler.dto.SelectorConfig selectorConfig = new com.daniellu.lawyer.crawler.dto.SelectorConfig();
        selectorConfig.setSelectorType(com.daniellu.lawyer.crawler.enums.SelectorType.CSS);
        selectorConfig.setListSelector(listSelector);

        // 设置字段选择器
        java.util.Map<String, String> fieldSelectors = new java.util.HashMap<>();
        if (url.contains("rmfyb.com")) {
            // 人民法院报-法答网特殊处理
            fieldSelectors.put("title", ".item_title");
            fieldSelectors.put("summary", ".item_content");
            fieldSelectors.put("link", ".item_link");
        } else if (url.contains("www.court.gov.cn")) {
            fieldSelectors.put("href", "a");
            fieldSelectors.put("publishedAt", "i");
        } else if (url.contains("gzw.sh.gov.cn")) {
            // 上海市国资委官网特殊处理
            fieldSelectors.put("href", "a");
            fieldSelectors.put("publishedAt", "span.time");
        } else {
            // 默认字段选择器
            fieldSelectors.put("href", "a");
            fieldSelectors.put("publishedAt", "span");
        }
        selectorConfig.setFieldSelectors(fieldSelectors);

        // 设置元数据，包含publishedInDays
        java.util.Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put(com.daniellu.lawyer.crawler.constant.SelectorMetadataKey.USER_PUBLISHED_IN_DAYS, publishedInDays);
        selectorConfig.setMetadata(metadata);

        request.setSelectorConfig(selectorConfig);

        // 设置任务元数据
        com.daniellu.lawyer.crawler.dto.TaskMetadata taskMetadata = new com.daniellu.lawyer.crawler.dto.TaskMetadata();
        taskMetadata.setTaskName(taskName);
        taskMetadata.setDescription(urlSegments != null ? "最多爬取前" + urlSegments.length + "页内容" : "只爬取第一页内容");
        request.setMetadata(taskMetadata);

        // 设置分页配置
        if (urlSegments != null) {
            com.daniellu.lawyer.crawler.dto.CrawlPagination pagination = new com.daniellu.lawyer.crawler.dto.CrawlPagination();
            pagination.setUrlSegments(java.util.Arrays.asList(urlSegments));
            request.setPagination(pagination);
        }

        return request;
    }
}