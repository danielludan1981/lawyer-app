package com.daniellu.lawyer.crawler.service.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daniellu.lawyer.common.dto.ErrorDTO;
import com.daniellu.lawyer.crawler.constant.SelectorMetadataKey;
import com.daniellu.lawyer.crawler.dto.CrawlPageResult;
import com.daniellu.lawyer.crawler.dto.CrawlPagination;
import com.daniellu.lawyer.crawler.dto.CrawlRequest;
import com.daniellu.lawyer.crawler.dto.CrawlResponse;
import com.daniellu.lawyer.crawler.dto.DataExtractorResult;
import com.daniellu.lawyer.crawler.dto.SelectorConfig;
import com.daniellu.lawyer.crawler.enums.CrawlStrategy;
import com.daniellu.lawyer.crawler.enums.TaskStatus;
import com.daniellu.lawyer.crawler.service.data.DataExtractor;
import com.daniellu.lawyer.crawler.service.engine.CrawlEngine;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;

import lombok.extern.slf4j.Slf4j;

/**
 * Playwright引擎实现类
 * 用于动态页面爬取
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Component
@Slf4j
public class PlaywrightEngine implements CrawlEngine {

    private final DataExtractor dataExtractor;

    // 信号量，控制最大并发数
    private static final Semaphore CONCURRENCY_SEMAPHORE = new Semaphore(5); // 限制最大5个并发Playwright任务

    /**
     * 注入数据提取器
     *
     * @param dataExtractor 数据提取器
     */
    @Autowired
    public PlaywrightEngine(DataExtractor dataExtractor) {
        this.dataExtractor = dataExtractor;
    }

    @Override
    public CrawlResponse execute(CrawlRequest request) {
        long startTime = System.currentTimeMillis();
        CrawlResponse result = initializeResult(request);
        // 初始化pageResultMap
        Map<String, CrawlPageResult> pageResultMap = new LinkedHashMap<>();
        result.setPageResultMap(pageResultMap);

        Playwright playwright = null;
        Browser browser = null;
        BrowserContext context = null;
        Page page = null;

        try {
            // 尝试获取信号量许可
            if (!CONCURRENCY_SEMAPHORE.tryAcquire(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Failed to acquire Playwright instance, timeout");
            }

            // 初始化Playwright
            playwright = Playwright.create();
            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true) // 无头模式
                            .setSlowMo(100) // 减缓操作，避免被反爬
            );

            // 创建新的上下文（隔离的浏览器环境）
            context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setUserAgent(getUserAgent(request))
            );

            // 创建新页面
            page = context.newPage();

            // 设置超时
            int timeout = request.getOptions() != null ? request.getOptions().getTimeout() : 30000;
            page.setDefaultTimeout(timeout);
            page.setDefaultNavigationTimeout(timeout);

            // 检查是否需要分页爬取
            if (request.getPagination() != null) {
                // 生成所有需要爬取的分页URL
                List<String> paginationUrls = generatePaginationUrls(request.getUrl(), request.getPagination());
                log.debug("Generated pagination URLs: {}", paginationUrls);

                int successfulPages = 0;
                boolean stopCrawl = false;

                // 遍历所有分页URL进行爬取
                for (String url : paginationUrls) {
                    // 处理单个页面爬取，如果返回true则停止后续爬取
                    stopCrawl = processSinglePage(request, url, page, pageResultMap);
                    if (stopCrawl) {
                        log.info("Stopping pagination crawl due to stopCrawl flag from extractor");
                        break;
                    }
                }

                // 更新成功页面计数
                for (CrawlPageResult pageResult : pageResultMap.values()) {
                    if (pageResult.isSuccess()) {
                        successfulPages++;
                    }
                }

                // 设置爬取结果
                result.setStatus(TaskStatus.SUCCESS);
                result.setPageCount(successfulPages);

            } else {
                // 单页爬取逻辑
                processSinglePage(request, request.getUrl(), page, pageResultMap);
                // 检查单页爬取是否成功
                CrawlPageResult pageResult = pageResultMap.get(request.getUrl());
                if (pageResult != null && pageResult.isSuccess()) {
                    result.setStatus(TaskStatus.SUCCESS);
                    result.setPageCount(1); // 单页爬取
                } else {
                    result.setStatus(TaskStatus.FAILED);
                    result.setPageCount(0);
                }
            }

        } catch (Exception e) {
            log.error("Playwright crawl failed for URL: {}", request.getUrl(), e);
            result.setStatus(TaskStatus.FAILED);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage(e.getMessage());
            errorDTO.setDetails(getStackTraceAsString(e));
            result.setError(errorDTO);
        } finally {
            // 清理资源
            try {
                if (page != null) {
                    page.close();
                }
                if (context != null) {
                    context.close();
                }
                if (browser != null) {
                    browser.close();
                }
                if (playwright != null) {
                    playwright.close();
                }
            } catch (Exception e) {
                log.error("Error closing Playwright resources", e);
            }
            // 释放信号量许可
            CONCURRENCY_SEMAPHORE.release();
        }

        // 设置总耗时
        result.setElapsedTime(System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 处理单个页面的爬取
     * @return 是否需要停止后续爬取
     */
    private boolean processSinglePage(CrawlRequest originalRequest, String url, Page page, Map<String, CrawlPageResult> pageResultMap) {
        long pageStartTime = System.currentTimeMillis();
        CrawlPageResult pageResult = new CrawlPageResult();
        pageResult.setUrl(url); // 设置页面URL
        pageResult.setSuccess(false);
        boolean stopCrawl = false;

        try {
            // 导航到目标URL
            log.debug("Playwright navigating to URL: {}", url);
            page.navigate(url, new Page.NavigateOptions()
                    .setWaitUntil(WaitUntilState.NETWORKIDLE)); // 等待网络空闲

            // 等待额外时间确保JavaScript执行完成
            if (originalRequest.getOptions() != null && originalRequest.getOptions().isEnableJavaScript()) {
                page.waitForTimeout(2000);
            }

            // 获取HTML内容
            String htmlContent = page.content();

            // 解析HTML
            Document document = Jsoup.parse(htmlContent);

            // 复制并更新selectorConfig，添加当前URL到metadata
            SelectorConfig originalSelectorConfig = originalRequest.getSelectorConfig();
            SelectorConfig pageSelectorConfig = originalSelectorConfig;

            if (originalSelectorConfig != null) {
                // 创建新的SelectorConfig对象，避免修改原始对象
                pageSelectorConfig = SelectorConfig.builder()
                        .fieldSelectors(originalSelectorConfig.getFieldSelectors())
                        .listSelector(originalSelectorConfig.getListSelector())
                        .paginationSelector(originalSelectorConfig.getPaginationSelector())
                        .selectorType(originalSelectorConfig.getSelectorType())
                        .build();

                // 获取或初始化metadata
                Map<String, Object> metadata = originalSelectorConfig.getMetadata();
                if (metadata == null) {
                    metadata = new HashMap<>();
                } else {
                    // 创建新的metadata对象，避免修改原始对象
                    metadata = new HashMap<>(metadata);
                }

                // 添加当前URL到metadata，使用SYS_URL作为key
                metadata.put(SelectorMetadataKey.SYS_URL, url);
                pageSelectorConfig.setMetadata(metadata);
            }

            // 使用新的DataExtractor接口提取数据
            DataExtractorResult extractorResult = dataExtractor.extractData(document, pageSelectorConfig);

            // 设置爬取结果数据
            pageResult.setData(extractorResult.getData());
            // 从DataExtractorResult中获取rawHtml和md5
            pageResult.setRawHtml(extractorResult.getRawHtml());
            pageResult.setMd5(extractorResult.getMd5());
            // 获取是否需要停止后续爬取的标志
            stopCrawl = extractorResult.isStopCrawl();

            // 更新页面结果状态
            pageResult.setSuccess(true);

            // 添加随机延迟，避免被反爬
            addRandomDelay(originalRequest);

        } catch (Exception e) {
            log.error("Playwright crawl failed for URL: {}", url, e);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage(e.getMessage());
            errorDTO.setDetails(getStackTraceAsString(e));
            pageResult.setError(errorDTO);
        } finally {
            // 设置页面爬取耗时
            pageResult.setElapsedTime(System.currentTimeMillis() - pageStartTime);
            // 将页面结果添加到map中
            pageResultMap.put(url, pageResult);
        }

        return stopCrawl;
    }

    @Override
    public String getSupportedStrategy() {
        return CrawlStrategy.PLAYWRIGHT.name();
    }

    @Override
    public boolean isAvailable() {
        // 简单检查Playwright是否可用
        try (Playwright playwright = Playwright.create()) {
            playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setTimeout(10000)).close();
            return true;
        } catch (Exception e) {
            log.error("Playwright is not available", e);
            return false;
        }
    }

    /**
     * 初始化爬取结果
     */
    private CrawlResponse initializeResult(CrawlRequest request) {
        return CrawlResponse.builder()
                .metadata(request.getMetadata())
                .status(TaskStatus.RUNNING)
                .usedStrategy(CrawlStrategy.PLAYWRIGHT)
                .build();
    }

    /**
     * 获取User-Agent
     */
    private String getUserAgent(CrawlRequest request) {
        if (request.getOptions() != null && request.getOptions().getUserAgent() != null) {
            return request.getOptions().getUserAgent();
        }
        // 默认User-Agent
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    }

    /**
     * 将异常堆栈转换为字符串
     */
    private String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * 生成所有需要爬取的分页URL
     */
    private List<String> generatePaginationUrls(String baseUrl, CrawlPagination pagination) {
        List<String> urls = new ArrayList<>();
        List<String> segments = new ArrayList<>();

        // 如果有urlSegments直接使用
        if (pagination.getUrlSegments() != null && !pagination.getUrlSegments().isEmpty()) {
            segments.addAll(pagination.getUrlSegments());
        }
        // 如果有urlTemplate，根据fromPage和endPage生成segments
        else if (pagination.getUrlTemplate() != null && pagination.getFromPage() != null && pagination.getEndPage() != null) {
            for (int i = pagination.getFromPage(); i <= pagination.getEndPage(); i++) {
                String segment = pagination.getUrlTemplate().replace("{}", String.valueOf(i));
                segments.add(segment);
            }
        }

        // 如果没有分页配置，只爬取基础URL
        if (segments.isEmpty()) {
            urls.add(baseUrl);
            return urls;
        }

        // 生成完整的URL
        for (String segment : segments) {
            String url;
            if (segment.startsWith("?") || segment.startsWith("&")) {
                // 如果是查询参数，检查baseUrl是否已有参数
                if (baseUrl.contains("?")) {
                    url = baseUrl + segment;
                } else {
                    url = baseUrl + segment;
                }
            } else {
                // 如果是路径，直接拼接
                url = baseUrl + segment;
            }
            urls.add(url);
        }

        return urls;
    }

    /**
     * 添加随机延迟，避免被反爬
     */
    private void addRandomDelay(CrawlRequest request) {
        if (request.getOptions() != null && request.getOptions().isEnableRandomDelay()) {
            try {
                int minDelay = request.getOptions().getMinDelay();
                int maxDelay = request.getOptions().getMaxDelay();
                int delay = minDelay + new Random().nextInt(maxDelay - minDelay + 1);
                log.debug("Adding random delay: {}ms", delay);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Random delay interrupted", e);
            }
        }
    }
}
