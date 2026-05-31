package com.daniellu.lawyer.crawler.dto;

import java.util.Map;

import com.daniellu.lawyer.crawler.enums.SelectorType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 选择器配置
 * 定义爬取目标页面时需要提取的字段和对应的选择器
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class SelectorConfig {

    /**
     * 字段选择器映射
     * key: 字段名称
     * value: 选择器表达式（CSS或XPath）
     */
    private Map<String, String> fieldSelectors;

    /**
     * 列表选择器
     * 用于定位页面中的列表项
     */
    private String listSelector;

    /**
     * 分页选择器
     * 用于定位分页元素
     */
    private String paginationSelector;

    /**
     * 选择器类型（CSS或XPath）
     */
    private SelectorType selectorType;

    /**
     * 元数据
     * 用于存储额外的元数据，可作为抽取时内容解析器选择的参考参数
     * 目前可以仅传递一个关键的参数即url, 可以通过url的匹配选择不同的解析器
     */
    private Map<String, Object> metadata;
}
