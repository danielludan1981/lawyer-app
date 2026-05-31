package com.daniellu.lawyer.crawler.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据提取结果
 * 封装从HTML文档中提取数据的结果
 *
 * @author Daniel Lu
 * @since 2026-01-17
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class DataExtractorResult {

    /**
     * 抽取到的数据
     * 根据不同抽取方式有不同类型，例如字符串、列表等
     */
    private Object data;

    /**
     * 抽取到数据对应的原始HTML内容
     * 当selectorConfig指定了listSelector时，包含所有匹配元素的HTML；否则包含整个Document的HTML
     */
    private String rawHtml;

    /**
     * rawHtml的MD5值
     * 用于缓存和去重
     */
    private String md5;

    /**
     * 是否停止后续分页的爬取
     * 比如，当爬取的内容已经满足需求，或者遇到了错误状态码时，设置为true
     */
    private boolean stopCrawl;
}
