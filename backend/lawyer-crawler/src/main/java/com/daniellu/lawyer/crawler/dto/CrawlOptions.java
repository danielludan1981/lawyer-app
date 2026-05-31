package com.daniellu.lawyer.crawler.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 爬取选项配置
 * 定义爬取任务的各种配置参数
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class CrawlOptions {

    /**
     * 请求超时时间（毫秒）
     */
    @Builder.Default
    private int timeout = 30000;

    /**
     * 最大重试次数
     */
    @Builder.Default
    private int maxRetries = 3;

    /**
     * 重试间隔时间（毫秒）
     */
    @Builder.Default
    private int retryInterval = 1000;

    /**
     * 是否启用JavaScript渲染
     */
    @Builder.Default
    private boolean enableJavaScript = false;

    /**
     * 是否启用分页爬取
     */
    @Builder.Default
    private boolean enablePagination = false;

    /**
     * 最大爬取页数
     */
    @Builder.Default
    private int maxPages = 1;

    /**
     * User-Agent头
     */
    private String userAgent;

    /**
     * 是否启用随机User-Agent
     */
    @Builder.Default
    private boolean randomUserAgent = true;

    /**
     * 是否启用代理
     */
    @Builder.Default
    private boolean enableProxy = false;

    /**
     * 代理服务器地址
     */
    private String proxyHost;

    /**
     * 代理服务器端口
     */
    private int proxyPort;

    /**
     * 是否启用头部信息模拟
     */
    @Builder.Default
    private boolean enableHeadersSimulation = true;
    
    /**
     * 是否启用随机延迟
     */
    @Builder.Default
    private boolean enableRandomDelay = true;
    
    /**
     * 最小延迟时间（毫秒）
     */
    @Builder.Default
    private int minDelay = 1000;
    
    /**
     * 最大延迟时间（毫秒）
     */
    @Builder.Default
    private int maxDelay = 3000;
}
