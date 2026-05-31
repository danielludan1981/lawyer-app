package com.daniellu.lawyer.crawler.service.parser.impl;

import java.util.List;
import java.util.Map;

import com.daniellu.lawyer.crawler.service.parser.AbstractDataParser;

/**
 * 测试用的数据解析器实现类
 * 用于测试增强的Parser选择策略
 *
 * @author Daniel Lu
 * @since 2026-01-25
 */
public class TestDataParser extends AbstractDataParser {

    private final String urlPrefix;
    private final String name;

    public TestDataParser(String urlPrefix, String name) {
        this.urlPrefix = urlPrefix;
        this.name = name;
    }

    @Override
    protected String getUrlPrefix() {
        return urlPrefix;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected Object parseMap(Map<String, Object> data, Map<String, Object> metadata) {
        // 简单的测试实现
        return "Parsed by " + name;
    }

    @Override
    protected List<Map<String, Object>> parseList(List<Map<String, Object>> data, Map<String, Object> metadata) {
        // 简单的测试实现
        return data;
    }
}