package com.daniellu.lawyer.crawler.service.strategy.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.daniellu.lawyer.crawler.enums.CrawlStrategy;
import com.daniellu.lawyer.crawler.service.strategy.CrawlStrategyResolver;

import lombok.extern.slf4j.Slf4j;

/**
 * 策略解析器实现类
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Component
@Slf4j
public class CrawlStrategyResolverImpl implements CrawlStrategyResolver {

    // 配置的动态站点域名列表
    private final Set<String> dynamicSites = new HashSet<>();

    // SPA常见的挂载点模式
    private static final Pattern SPA_MOUNT_PATTERN = Pattern.compile("<div[^>]*id=[\"'](app|root|main)[\"'][^>]*>");

    public CrawlStrategyResolverImpl() {
        // 初始化常见的动态站点域名
        dynamicSites.add("twitter.com");
        dynamicSites.add("facebook.com");
        dynamicSites.add("instagram.com");
        dynamicSites.add("linkedin.com");
        dynamicSites.add("youtube.com");
        dynamicSites.add("github.com");
        // 可以从配置文件或数据库加载更多动态站点
    }

    @Override
    public CrawlStrategy resolveStrategy(String url) {
        try {
            // 使用URI替代URL解析主机名
            URI uri = URI.create(url);
            String host = uri.getHost();

            // 1. 检查是否为已知的动态站点
            for (String dynamicSite : dynamicSites) {
                if (host.endsWith(dynamicSite)) {
                    log.debug("URL {} is on known dynamic site {}, using PLAYWRIGHT strategy", url, dynamicSite);
                    return CrawlStrategy.PLAYWRIGHT;
                }
            }

            // 2. 预爬取嗅探页面内容
            return sniffPageContent(url);
        } catch (Exception e) {
            log.error("Error resolving strategy for URL {}", url, e);
            // 默认使用Jsoup策略
            return CrawlStrategy.JSOUP;
        }
    }

    @Override
    public CrawlStrategy sniffPageContent(String url) {
        try {
            // 使用URI替代URL，然后转换为URL以打开连接
            URI uri = URI.create(url);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                log.warn("Sniffing URL {} returned response code {}", url, responseCode);
                return CrawlStrategy.JSOUP;
            }

            // 读取部分HTML内容进行检查
            StringBuilder content = new StringBuilder();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                String line;
                int linesRead = 0;
                while ((line = reader.readLine()) != null && linesRead < 100) {
                    content.append(line).append("\n");
                    linesRead++;
                }
            }

            String htmlContent = content.toString();

            // 检查是否包含SPA常见的挂载点
            Matcher matcher = SPA_MOUNT_PATTERN.matcher(htmlContent);
            if (matcher.find()) {
                log.debug("URL {} contains SPA mount point {}, using PLAYWRIGHT strategy", url, matcher.group(1));
                return CrawlStrategy.PLAYWRIGHT;
            }

            // 检查是否包含大量JavaScript引用
            int scriptTagCount = countOccurrences(htmlContent, "<script");
            if (scriptTagCount > 10) {
                log.debug("URL {} contains {} script tags, using PLAYWRIGHT strategy", url, scriptTagCount);
                return CrawlStrategy.PLAYWRIGHT;
            }

            // 默认使用Jsoup策略
            log.debug("URL {} appears to be static, using JSOUP strategy", url);
            return CrawlStrategy.JSOUP;

        } catch (IOException e) {
            log.error("Error sniffing page content for URL {}", url, e);
            // 默认使用Jsoup策略
            return CrawlStrategy.JSOUP;
        }
    }

    /**
     * 统计字符串出现次数
     */
    private int countOccurrences(String str, String subStr) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(subStr, index)) != -1) {
            count++;
            index += subStr.length();
        }
        return count;
    }
}
