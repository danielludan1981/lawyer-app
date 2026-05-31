package com.daniellu.lawyer.crawler.service.parser.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.daniellu.lawyer.crawler.service.parser.AbstractDataParser;

/**
 * 国务院国资委官网-互动交流数据解析器实现类
 * 支持的URL前缀为http://www.sasac.gov.cn/hdjl_index.html
 *
 * @author Daniel Lu
 * @since 2026-01-25
 */
@Component
public class SasacGovCommunicationDataParserImpl extends AbstractDataParser {

    @Override
    protected String getUrlPrefix() {
        return "http://www.sasac.gov.cn/hdjl_index.html";
    }

    @Override
    public String getName() {
        return "SasacGovCommunicationDataParser";
    }

    @Override
    public boolean supports(Map<String, Object> metadata, Object data) {
        if (metadata == null) {
            return false;
        }

        // 检查URL是否匹配
        Object urlObj = metadata.get("url");
        if (urlObj == null) {
            return false;
        }

        String url = urlObj.toString();
        return url.contains(getUrlPrefix());
    }

    @Override
    public int getMatchScore(Map<String, Object> metadata, Object data) {
        if (metadata == null) {
            return 0;
        }

        // 检查URL是否匹配
        Object urlObj = metadata.get("url");
        if (urlObj == null) {
            return 0;
        }

        String url = urlObj.toString();
        if (url.contains(getUrlPrefix())) {
            return getUrlPrefix().length(); // 完全匹配，返回完整前缀长度
        }

        return 0; // 不匹配，返回0
    }

    @Override
    protected Object parseMap(Map<String, Object> data, Map<String, Object> metadata) {
        // 直接抛出UnsupportedOperationException异常
        throw new UnsupportedOperationException(
                "SasacGovCommunicationDataParser does not support parseMap operation. " +
                "Please use parseList method instead."
        );
    }

    @Override
    protected List<Map<String, Object>> parseList(List<Map<String, Object>> data, Map<String, Object> metadata) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取metadata中的url字段，作为baseUrl
        String baseUrl = (String) metadata.get("url");
        if (baseUrl == null) {
            logger.warn("No baseUrl found in metadata, returning empty list");
            return new ArrayList<>();
        }

        List<Map<String, Object>> result = new ArrayList<>();

        // 遍历List中的每个元素
        for (Map<String, Object> item : data) {
            try {
                // 将item转换为字符串以便解析
                String htmlContent = item.toString();

                // 使用Jsoup解析HTML
                Document doc = Jsoup.parse(htmlContent);

                // 获取所有链接元素
                Elements links = doc.select("a[href]");

                // 遍历每个链接元素
                for (Element link : links) {
                    try {
                        // 获取href字段的值，解析为Element对象
                        String href = link.attr("href");

                        // 从Element对象中提取text值，作为ArticleDTO的title字段，注意trim()去掉首尾空格
                        String title = link.text().trim();

                        // 从Element对象中提取href字段的值，根据baseUrl和href字段的值（该值为相对url路径），拼接成完整的url，作为ArticleDTO的url字段
                        String fullUrl;
                        if (href.startsWith("http://") || href.startsWith("https://")) {
                            // 如果href已经是完整URL，直接使用
                            fullUrl = href;
                        } else {
                            // 如果href是相对路径，与baseUrl拼接
                            if (baseUrl.endsWith("/")) {
                                fullUrl = baseUrl + href;
                            } else {
                                fullUrl = baseUrl + "/" + href;
                            }
                        }

                        // 获取publishedAt字段的值，解析为Element对象
                        // 原始的publishedAt代表的element字符串<span>[12-29]</span>需要从item中获取
                        String publishedAt = null;

                        // 从item中获取publishedAt字段的HTML字符串
                        Object publishedAtObj = item.get("publishedAt");
                        if (publishedAtObj != null) {
                            String publishedAtHtml = publishedAtObj.toString();

                            // 使用Jsoup解析HTML字符串为Element对象
                            Element publishedAtElement = org.jsoup.Jsoup.parse(publishedAtHtml).selectFirst("span");

                            if (publishedAtElement != null) {
                                // 从Element对象中提取text值，去除无意义的方括号，作为ArticleDTO的publishedAt字段
                                String publishedAtText = publishedAtElement.text().trim();

                                // 去除方括号，格式为[12-29] -> 12-29
                                if (publishedAtText.startsWith("[") && publishedAtText.endsWith("]")) {
                                    publishedAtText = publishedAtText.substring(1, publishedAtText.length() - 1);
                                }

                                // 解析方式为年部分统一为当年，拼接提取的月和日，格式为yyyy-MM-dd
                                if (publishedAtText.matches("\\d{1,2}-\\d{1,2}")) {
                                    String[] parts = publishedAtText.split("-");
                                    String month = parts[0];
                                    String day = parts[1];

                                    // 确保月份和日期是两位数格式
                                    if (month.length() == 1) {
                                        month = "0" + month;
                                    }
                                    if (day.length() == 1) {
                                        day = "0" + day;
                                    }

                                    // 获取当前年份
                                    java.time.LocalDate today = java.time.LocalDate.now();
                                    int currentYear = today.getYear();
                                    String year = String.valueOf(currentYear);

                                    // 构建完整日期
                                    String fullDate = year + "-" + month + "-" + day;

                                    // 检查日期是否大于今天
                                    try {
                                        java.time.LocalDate parsedDate = java.time.LocalDate.parse(fullDate);
                                        if (parsedDate.isAfter(today)) {
                                            // 如果日期大于今天，则将年份改为去年
                                            year = String.valueOf(currentYear - 1);
                                            fullDate = year + "-" + month + "-" + day;
                                        }
                                        publishedAt = fullDate;
                                    } catch (Exception e) {
                                        logger.warn("Unable to parse date: {}, error: {}", fullDate, e.getMessage());
                                        publishedAt = null;
                                    }
                                } else {
                                    // 如publishedAt无法解析为日期格式，则设置为null，打印日志且，该元素不加入到最终返回的列表中
                                    logger.warn("Unable to parse publishedAt date format: {}, skipping article", publishedAtText);
                                    publishedAt = null;
                                }
                            } else {
                                logger.warn("No span element found in publishedAt HTML: {}", publishedAtHtml);
                            }
                        } else {
                            logger.warn("No publishedAt field found in item");
                        }

                        // 只有当publishedAt成功解析时才创建结果Map
                        if (publishedAt != null) {
                            Map<String, Object> articleMap = new java.util.HashMap<>();
                            articleMap.put("title", title);
                            articleMap.put("url", fullUrl);
                            articleMap.put("publishedAt", publishedAt);

                            result.add(articleMap);
                        }
                    } catch (Exception e) {
                        logger.error("Error parsing link element: {}", e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                logger.error("Error parsing HTML content: {}", e.getMessage(), e);
            }
        }

        logger.info("Parsed {} articles from SASAC government communication data", result.size());
        return result;
    }
}