package com.daniellu.lawyer.crawler.service.engine.impl;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.daniellu.lawyer.common.dto.ErrorDTO;
import com.daniellu.lawyer.crawler.constant.SelectorMetadataKey;
import com.daniellu.lawyer.crawler.dto.CrawlOptions;
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

import lombok.extern.slf4j.Slf4j;

/**
 * Jsoup引擎实现类
 * 用于静态页面爬取
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Component
@Slf4j
public class JsoupEngine implements CrawlEngine {

    private final DataExtractor dataExtractor;

    // User-Agent池，用于随机选择
    private static final List<String> USER_AGENTS = Arrays.asList(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_2) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    );

    private static final Random RANDOM = new Random();

    /**
     * 注入数据提取器
     *
     * @param dataExtractor 数据提取器
     */
    @Autowired
    public JsoupEngine(DataExtractor dataExtractor) {
        this.dataExtractor = dataExtractor;
    }

    @Override
    public CrawlResponse execute(CrawlRequest request) {
        long startTime = System.currentTimeMillis();
        CrawlResponse result = initializeResult(request);
        // 初始化pageResultMap
        Map<String, CrawlPageResult> pageResultMap = new LinkedHashMap<>();
        result.setPageResultMap(pageResultMap);

        try {
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
                    stopCrawl = processSinglePage(request, url, pageResultMap);
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
                processSinglePage(request, request.getUrl(), pageResultMap);
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
            log.error("Jsoup crawl failed for URL: {}", request.getUrl(), e);
            result.setStatus(TaskStatus.FAILED);
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setMessage(e.getMessage());
            errorDTO.setDetails(getStackTraceAsString(e));
            result.setError(errorDTO);
        }

        // 设置总耗时
        result.setElapsedTime(System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 处理单个页面的爬取
     * @return 是否需要停止后续爬取
     */
    private boolean processSinglePage(CrawlRequest originalRequest, String url, Map<String, CrawlPageResult> pageResultMap) {
        long pageStartTime = System.currentTimeMillis();
        CrawlPageResult pageResult = new CrawlPageResult();
        pageResult.setUrl(url); // 设置页面URL
        pageResult.setSuccess(false);
        boolean stopCrawl = false;

        try {
            // 创建分页请求
            CrawlRequest pageRequest = createPageRequest(originalRequest, url);

            // 获取HTML内容
            String htmlContent = fetchHtmlContent(pageRequest);

            // 解析HTML
            Document document = Jsoup.parse(htmlContent);

            // 使用新的DataExtractor接口提取数据
            DataExtractorResult extractorResult = dataExtractor.extractData(document, pageRequest.getSelectorConfig());

            // 设置爬取结果数据
            pageResult.setData(extractorResult.getData());
            // 从DataExtractorResult中获取rawHtml和md5
            // pageResult.setRawHtml(extractorResult.getRawHtml()); // 暂时不设置原始HTML
            pageResult.setMd5(extractorResult.getMd5());
            // 获取是否需要停止后续爬取的标志
            stopCrawl = extractorResult.isStopCrawl();

            // 更新页面结果状态
            pageResult.setSuccess(true);

            // 添加随机延迟，避免被反爬
            addRandomDelay(originalRequest);

        } catch (Exception e) {
            log.error("Jsoup crawl failed for URL: {}", url, e);
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
        return CrawlStrategy.JSOUP.name();
    }

    @Override
    public boolean isAvailable() {
        return true; // Jsoup是纯Java库，始终可用
    }

    /**
     * 初始化爬取结果
     */
    private CrawlResponse initializeResult(CrawlRequest request) {
        return CrawlResponse.builder()
                .metadata(request.getMetadata())
                .status(TaskStatus.RUNNING)
                .usedStrategy(CrawlStrategy.JSOUP)
                .build();
    }

    /**
     * 获取HTML内容
     */
    @Retryable(
            maxAttemptsExpression = "#root.args[0].options?.maxRetries ?: environment.getProperty('crawler.jsoup.max-retries', T(java.lang.Integer), 3)",
            backoff = @Backoff(
                    delayExpression = "#root.args[0].options?.retryInterval ?: environment.getProperty('crawler.jsoup.retry-interval', T(java.lang.Integer), 1000)",
                    multiplier = 2,
                    random = true
            ),
            retryFor = IOException.class,
            notRecoverable = {InterruptedException.class, RuntimeException.class}
    )
    public String fetchHtmlContent(CrawlRequest request) throws IOException {
        String url = request.getUrl();
        CrawlOptions options = request.getOptions();
        int timeout = options != null ? options.getTimeout() : 30000;

        Connection connection = createConnection(url, request);
        connection.timeout(timeout);

        log.debug("Jsoup fetch attempt for URL: {}", url);
        return connection.get().html();
    }

    /**
     * 重试失败时的恢复方法
     */
    @Recover
    public String recoverFromFetchFailure(IOException e, CrawlRequest request) {
        String url = request.getUrl();
        log.error("Jsoup fetch failed after multiple attempts for URL: {}", url, e);
        throw new RuntimeException("Failed to fetch HTML content after multiple attempts", e);
    }

    /**
     * 创建Jsoup连接
     */
    private Connection createConnection(String url, CrawlRequest request) {
        Connection connection = Jsoup.connect(url);
        CrawlOptions options = request.getOptions();

        // 设置User-Agent
        if (options != null && options.getUserAgent() != null) {
            connection.userAgent(options.getUserAgent());
        } else if (options == null || options.isRandomUserAgent()) {
            // 随机选择User-Agent
            String randomUserAgent = USER_AGENTS.get(RANDOM.nextInt(USER_AGENTS.size()));
            connection.userAgent(randomUserAgent);
        }

        // 设置请求头
        if (options != null && options.isEnableHeadersSimulation()) {
            connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            connection.header("Accept-Language", "en-US,en;q=0.5");
            connection.header("Accept-Encoding", "gzip, deflate");

            connection.header("Upgrade-Insecure-Requests", "1");
        }

        // 设置自定义SSL上下文，信任所有证书
        connection.sslSocketFactory(createTrustAllSslSocketFactory());

        return connection;
    }

    /**
     * 创建信任所有证书的SSL上下文
     */
    private javax.net.ssl.SSLSocketFactory createTrustAllSslSocketFactory() {
        try {
            // 创建信任所有证书的TrustManager
            javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };

            // 创建SSL上下文
            javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            log.error("Failed to create trust all SSL socket factory", e);
            throw new RuntimeException(e);
        }
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
            // 直接拼接URL，无论segment是查询参数还是路径
            String url = baseUrl + segment;
            urls.add(url);
        }

        return urls;
    }

    /**
     * 为分页URL创建临时请求对象
     */
    private CrawlRequest createPageRequest(CrawlRequest originalRequest, String url) {
        // 复制并更新selectorConfig，添加当前URL到metadata
        SelectorConfig originalSelectorConfig = originalRequest.getSelectorConfig();
        SelectorConfig pageSelectorConfig = null;

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

        return CrawlRequest.builder()
                .url(url)
                .strategy(originalRequest.getStrategy())
                .selectorConfig(pageSelectorConfig)
                .options(originalRequest.getOptions())
                .metadata(originalRequest.getMetadata())
                .pagination(null) // 分页请求不需要再设置pagination
                .build();
    }

    /**
     * 添加随机延迟，避免被反爬
     */
    private void addRandomDelay(CrawlRequest request) {
        if (request.getOptions() != null && request.getOptions().isEnableRandomDelay()) {
            try {
                int minDelay = request.getOptions().getMinDelay();
                int maxDelay = request.getOptions().getMaxDelay();
                int delay = minDelay + RANDOM.nextInt(maxDelay - minDelay + 1);
                log.debug("Adding random delay: {}ms", delay);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Random delay interrupted", e);
            }
        }
    }


}
