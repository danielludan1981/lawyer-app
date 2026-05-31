package com.daniellu.lawyer.crawler.service.parser.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import com.daniellu.lawyer.crawler.dto.parser.ArticleDTO;
import com.daniellu.lawyer.crawler.service.parser.AbstractDataParser;

/**
 * 人民法院报官网页面解析器
 * 支持的URL前缀为https://www.rmfyb.com
 *
 * @author Daniel Lu
 * @since 2026-01-17
 */
@Component
public class RmfybDataParserImpl extends AbstractDataParser {

    /**
     * 返回解析器支持的URL前缀
     */
    @Override
    protected String getUrlPrefix() {
        return "https://www.rmfyb.com";
    }

    /**
     * 解析单个Map数据
     * 直接抛出UnsupportedOperationException异常
     */
    @Override
    protected Object parseMap(Map<String, Object> data, Map<String, Object> metadata) {
        throw new UnsupportedOperationException("RmfybDataParserImpl does not support parseMap");
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

        // 遍历List中的每个元素
        for (Map<String, Object> item : data) {
            try {
                // 解析并映射到ArticleDTO对象
                ArticleDTO article = parseArticle(item);
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
    private ArticleDTO parseArticle(Map<String, Object> item) {
        // 获取title字段的值，解析为Element对象
        Object titleObj = item.get("title");
        if (titleObj == null) {
            logger.warn("Item has no title field");
            return null;
        }

        String titleHtml = titleObj.toString();
        Document titleDoc = Jsoup.parse(titleHtml);
        String title = titleDoc.text().trim();

        // 获取summary字段的值，解析为Element对象
        Object summaryObj = item.get("summary");
        String summary = null;
        if (summaryObj != null) {
            String summaryHtml = summaryObj.toString();
            Document summaryDoc = Jsoup.parse(summaryHtml);
            summary = summaryDoc.text().trim();
        }

        // 获取link字段的值，解析为Element对象
        Object linkObj = item.get("link");
        if (linkObj == null) {
            logger.warn("Item has no link field");
            return null;
        }

        String linkHtml = linkObj.toString();
        Document linkDoc = Jsoup.parse(linkHtml);

        // 提取link_url字段的值，作为ArticleDTO的url字段
        Element linkUrlElement = linkDoc.selectFirst(".link_url");
        if (linkUrlElement == null) {
            logger.warn("Invalid link element, no link_url found: {}", linkHtml);
            return null;
        }
        String url = linkUrlElement.text().trim();

        // 提取page_num字段的值，用空格分隔，取第一个值作为ArticleDTO的publishedAt字段
        Element pageNumElement = linkDoc.selectFirst(".page_num");
        if (pageNumElement == null) {
            logger.warn("Invalid link element, no page_num found: {}", linkHtml);
            return null;
        }
        String pageNumStr = pageNumElement.text().trim();
        String[] pageNumParts = pageNumStr.split(" ");
        if (pageNumParts.length < 1) {
            logger.warn("Invalid page_num format: {}", pageNumStr);
            return null;
        }
        String publishedAtStr = pageNumParts[0].trim();
        LocalDateTime publishedAt = parseDateTime(publishedAtStr);
        if (publishedAt == null) {
            logger.warn("Invalid publishedAt date: {}", publishedAtStr);
            return null;
        }

        // 创建并返回ArticleDTO对象
        return ArticleDTO.builder()
                .title(title)
                .summary(summary)
                .url(url)
                .publishedAt(publishedAt)
                .build();
    }

    /**
     * 解析日期字符串为LocalDateTime
     */
    private LocalDateTime parseDateTime(String dateStr) {
        try {
            // 先解析日期部分
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            // 转换为LocalDateTime，时间部分设为00:00:00
            return date.atStartOfDay();
        } catch (DateTimeParseException e) {
            logger.warn("Failed to parse date: {}", dateStr, e);
            return null;
        }
    }

    /**
     * 将ArticleDTO转换为Map
     */
    private Map<String, Object> toMap(ArticleDTO article) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("title", article.getTitle());
        map.put("url", article.getUrl());
        map.put("publishedAt", article.getPublishedAt());
        map.put("summary", article.getSummary());
        map.put("content", article.getContent());
        map.put("source", article.getSource());
        return map;
    }
}
