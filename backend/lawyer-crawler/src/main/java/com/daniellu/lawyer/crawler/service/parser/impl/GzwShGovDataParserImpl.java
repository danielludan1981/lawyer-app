package com.daniellu.lawyer.crawler.service.parser.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import com.daniellu.lawyer.common.util.URIUtil;
import com.daniellu.lawyer.crawler.dto.parser.ArticleDTO;
import com.daniellu.lawyer.crawler.service.parser.AbstractDataParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 上海市国资委官网页面解析器
 * 支持的URL前缀为https://www.gzw.sh.gov.cn
 *
 * @author Daniel Lu
 * @since 2026-01-17
 */
@Component

@Slf4j
public class GzwShGovDataParserImpl extends AbstractDataParser {

    /**
     * 返回解析器支持的URL前缀
     */
    @Override
    protected String getUrlPrefix() {
        return "https://www.gzw.sh.gov.cn";
    }

    /**
     * 解析单个Map数据
     * 直接抛出UnsupportedOperationException异常
     */
    @Override
    protected Object parseMap(Map<String, Object> data, Map<String, Object> metadata) {
        throw new UnsupportedOperationException("parseMap is not supported for GzwShGovDataParserImpl");
    }

    /**
     * 解析List数据
     * 将每个元素映射到ArticleDTO对象
     */
    @Override
    protected List<Map<String, Object>> parseList(List<Map<String, Object>> data, Map<String, Object> metadata) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (data == null || data.isEmpty()) {
            return result;
        }

        // 获取metadata中的url字段，作为baseUrl
        String baseUrl = (String) metadata.get("url");

        // 遍历List中的每个元素
        for (Map<String, Object> item : data) {
            try {
                // 解析并映射到ArticleDTO对象
                ArticleDTO article = parseArticle(item, baseUrl);
                if (article != null) {
                    // 将ArticleDTO转换为Map并添加到结果列表
                    result.add(toMap(article));
                }
            } catch (Exception e) {
                logger.warn("Failed to parse article: {}", e.getMessage());
            }
        }

        return result;
    }

    /**
     * 解析单个文章元素
     */
    private ArticleDTO parseArticle(Map<String, Object> item, String baseUrl) {
        // 获取href字段的值，解析为Element对象
        Object hrefObj = item.get("href");
        if (hrefObj == null) {
            logger.warn("Item has no href field");
            return null;
        }

        String hrefHtml = hrefObj.toString();
        Element hrefElement = Jsoup.parse(hrefHtml).selectFirst("a");
        if (hrefElement == null) {
            logger.warn("Invalid href element: {}", hrefHtml);
            return null;
        }

        // 从Element对象中提取text值，作为ArticleDTO的title字段
        String title = hrefElement.text().trim();

        // 从Element对象中提取href字段的值，根据baseUrl和href字段的值（该值为相对url路径），拼接成完整的url
        String href = hrefElement.attr("href");
        String url = URIUtil.resolveUrl(baseUrl, href);

        // 获取publishedAt字段的值，解析为Element对象
        Object publishedAtObj = item.get("publishedAt");
        if (publishedAtObj == null) {
            logger.warn("Item has no publishedAt field");
            return null;
        }

        String publishedAtHtml = publishedAtObj.toString();
        Element publishedAtElement = Jsoup.parse(publishedAtHtml).selectFirst("span.time");
        if (publishedAtElement == null) {
            logger.warn("Invalid publishedAt element: {}", publishedAtHtml);
            return null;
        }

        // 从Element对象中提取text值，作为ArticleDTO的publishedAt字段
        String publishedAtStr = publishedAtElement.text().trim();
        LocalDateTime publishedAt = parseDateTime(publishedAtStr);
        if (publishedAt == null) {
            logger.warn("Invalid publishedAt date: {}", publishedAtStr);
            return null;
        }

        // 创建并返回ArticleDTO对象
        return ArticleDTO.builder()
                .title(title)
                .url(url)
                .publishedAt(publishedAt)
                .build();
    }

    /**
     * 解析日期时间字符串
     * 支持的格式：yyyy-MM-dd
     *
     * @param dateTimeStr 日期时间字符串
     * @return 解析后的LocalDateTime对象，解析失败返回null
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }

        try {
            // 尝试解析日期格式：yyyy-MM-dd
            return LocalDateTime.parse(dateTimeStr + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException _) {
            logger.warn("Failed to parse date time: {}", dateTimeStr);
            return null;
        }
    }

    /**
     * 将ArticleDTO转换为Map
     */
    private Map<String, Object> toMap(ArticleDTO article) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", article.getTitle());
        map.put("url", article.getUrl());
        map.put("publishedAt", article.getPublishedAt());
        map.put("summary", article.getSummary());
        map.put("content", article.getContent());
        map.put("source", article.getSource());
        return map;
    }
}