package com.daniellu.lawyer.crawler.service.parser.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.daniellu.lawyer.crawler.constant.SelectorMetadataKey;
import com.daniellu.lawyer.crawler.service.parser.AbstractDataParser;

/**
 * 无操作数据解析器实现类
 * 不做任何数据处理，直接返回传入的数据
 *
 * @author Daniel Lu
 * @since 2026-01-25
 */
@Component
public class NoopDataParserImpl extends AbstractDataParser {

    @Override
    protected String getUrlPrefix() {
        // 无操作解析器不需要URL前缀
        return null;
    }

    @Override
    public String getName() {
        return "noop";
    }

    @Override
    public boolean supports(Map<String, Object> metadata, Object data) {
        if (metadata == null) {
            return false;
        }

        // 当metadata中没有指定dataParser名称为"noop"时，返回true，否则返回false
        String specifiedParserName = (String) metadata.get(SelectorMetadataKey.USER_DATA_PARSER);
        return "noop".equals(specifiedParserName);
    }

    @Override
    public int getMatchScore(Map<String, Object> metadata, Object data) {
        // 实现getMatchScore方法，返回0
        return 0;
    }

    @Override
    protected Object parseMap(Map<String, Object> data, Map<String, Object> metadata) {
        // parse方法，直接返回传入的数据，不做任何处理
        return data;
    }

    @Override
    protected List<Map<String, Object>> parseList(List<Map<String, Object>> data, Map<String, Object> metadata) {
        // parse方法，直接返回传入的数据，不做任何处理
        return data;
    }
}