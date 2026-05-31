package com.daniellu.lawyer.crawler.service.data.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daniellu.lawyer.common.util.Md5Util;
import com.daniellu.lawyer.crawler.constant.SelectorMetadataKey;
import com.daniellu.lawyer.crawler.dto.DataExtractorResult;
import com.daniellu.lawyer.crawler.dto.SelectorConfig;
import com.daniellu.lawyer.crawler.enums.SelectorType;
import com.daniellu.lawyer.crawler.service.data.DataExtractor;
import com.daniellu.lawyer.crawler.service.parser.DataParser;
import com.daniellu.lawyer.crawler.service.strategy.DataParserStrategy;

/**
 * 数据提取实现类
 * 实现从HTML文档中提取数据的逻辑
 *
 * @author Daniel Lu
 * @since 2026-01-17
 */
@Service
public class DataExtractorImpl implements DataExtractor {

    private final DataParserStrategy parserStrategy;

    /**
     * 注入数据解析器策略
     *
     * @param parserStrategy 数据解析器策略
     */
    @Autowired
    public DataExtractorImpl(DataParserStrategy parserStrategy) {
        this.parserStrategy = parserStrategy;
    }

    @Override
    public DataExtractorResult extractData(Document document, SelectorConfig selectorConfig) {
        // 初始化提取结果
        DataExtractorResult result = new DataExtractorResult();

        // 提取原始HTML内容
        String rawHtml;
        if (selectorConfig == null || selectorConfig.getListSelector() == null) {
            // 没有listSelector，使用整个document的HTML
            rawHtml = document.html();
        } else {
            // 有listSelector，使用所有匹配元素的HTML
            String listSelector = selectorConfig.getListSelector();
            SelectorType selectorType = selectorConfig.getSelectorType();
            Elements elements = selectElements(document, listSelector, selectorType);
            StringBuilder sb = new StringBuilder();
            for (Element element : elements) {
                sb.append(element.html());
            }
            rawHtml = sb.toString();
        }
        result.setRawHtml(rawHtml);

        // 生成MD5值
        String md5 = Md5Util.generateMd5(rawHtml);
        result.setMd5(md5);

        // 提取基础数据
        Object baseData;
        if (selectorConfig == null) {
            // 如果没有选择器配置，返回整个HTML
            baseData = document.html();
        } else if (selectorConfig.getListSelector() != null) {
            // 如果有列表选择器，提取列表数据
            baseData = extractListData(document, selectorConfig);
        } else {
            // 否则提取单个对象数据
            baseData = extractSingleData(document, selectorConfig);
        }

        // 根据metadata选择合适的解析器，并对数据进行解析
        Object parsedData = baseData;
        if (selectorConfig != null && selectorConfig.getMetadata() != null) {
            DataParser parser = parserStrategy.selectParser(selectorConfig.getMetadata(), baseData);
            if (parser != null) {
                parsedData = parser.parse(baseData, selectorConfig.getMetadata());
            }
        }

        // 检查是否需要判断发布日期
        if (selectorConfig != null && selectorConfig.getMetadata() != null) {
            // 获取publishedInDays参数
            Object publishedInDaysObj = selectorConfig.getMetadata().get(SelectorMetadataKey.USER_PUBLISHED_IN_DAYS);
            if (publishedInDaysObj != null) {
                try {
                    int publishedInDays = Integer.parseInt(publishedInDaysObj.toString());
                    // 计算截止日期
                    LocalDateTime cutoffDate = LocalDateTime.now().minusDays(publishedInDays);

                    // 如果解析结果是List类型，检查每个对象的publishedAt属性
                    if (parsedData instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> listData = (List<Map<String, Object>>) parsedData;

                        // 创建新的列表用于存储过滤后的数据
                        List<Map<String, Object>> filteredList = new ArrayList<>();
                        boolean hasOldRecord = false;

                        // 完整遍历所有记录
                        for (Map<String, Object> item : listData) {
                            if (item.containsKey("publishedAt")) {
                                Object publishedAtObj = item.get("publishedAt");
                                if (publishedAtObj instanceof LocalDateTime) {
                                    LocalDateTime publishedAt = (LocalDateTime) publishedAtObj;
                                    // 如果发布日期在指定时间段内，添加到过滤列表
                                    if (!publishedAt.isBefore(cutoffDate)) {
                                        filteredList.add(item);
                                    } else {
                                        // 标记存在旧记录
                                        hasOldRecord = true;
                                    }
                                } else {
                                    // 如果publishedAt不是LocalDateTime类型，保留该记录
                                    filteredList.add(item);
                                }
                            } else {
                                // 如果没有publishedAt属性，保留该记录
                                filteredList.add(item);
                            }
                        }

                        // 如果存在旧记录，设置stopCrawl为true
                        if (hasOldRecord) {
                            result.setStopCrawl(true);
                        }

                        // 使用过滤后的数据
                        parsedData = filteredList;
                    }
                } catch (NumberFormatException e) {
                    // 忽略无效的publishedInDays值
                }
            }
        }

        // 设置最终提取的数据
        result.setData(parsedData);

        return result;
    }

    /**
     * 提取单个对象数据
     */
    private Map<String, Object> extractSingleData(Document document, SelectorConfig selectorConfig) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> fieldSelectors = selectorConfig.getFieldSelectors();

        if (fieldSelectors != null) {
            for (Map.Entry<String, String> entry : fieldSelectors.entrySet()) {
                String fieldName = entry.getKey();
                String selector = entry.getValue();
                Object value = extractFieldValue(document, selector, selectorConfig.getSelectorType());
                result.put(fieldName, value);
            }
        }

        return result;
    }

    /**
     * 提取列表数据
     */
    private List<Map<String, Object>> extractListData(Document document, SelectorConfig selectorConfig) {
        List<Map<String, Object>> result = new ArrayList<>();
        String listSelector = selectorConfig.getListSelector();
        Map<String, String> fieldSelectors = selectorConfig.getFieldSelectors();
        SelectorType selectorType = selectorConfig.getSelectorType();

        Elements listItems = selectElements(document, listSelector, selectorType);

        for (Element item : listItems) {
            Map<String, Object> itemData = new HashMap<>();
            if (fieldSelectors != null) {
                for (Map.Entry<String, String> entry : fieldSelectors.entrySet()) {
                    String fieldName = entry.getKey();
                    String selector = entry.getValue();
                    Object value = extractFieldValue(item, selector, selectorType);
                    itemData.put(fieldName, value);
                }
            }
            result.add(itemData);
        }

        return result;
    }

    /**
     * 提取字段值
     */
    private Object extractFieldValue(Element element, String selector, SelectorType selectorType) {
        Elements elements = selectElements(element, selector, selectorType);
        if (elements.isEmpty()) {
            return null;
        }
        if (elements.size() == 1) {
            return elements.first().outerHtml();
        }
        // 如果有多个元素，返回文本列表
        List<String> values = new ArrayList<>();
        for (Element el : elements) {
            values.add(el.outerHtml());
        }
        return values;
    }

    /**
     * 选择元素
     */
    private Elements selectElements(Element element, String selector, SelectorType selectorType) {
        if (selectorType == SelectorType.XPATH) {
            return element.selectXpath(selector);
        } else {
            return element.select(selector);
        }
    }
}
