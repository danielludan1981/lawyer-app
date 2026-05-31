package com.daniellu.lawyer.crawler.service.strategy;

import java.util.Map;

import com.daniellu.lawyer.crawler.service.parser.DataParser;

/**
 * 数据解析器策略接口
 * 用于选择合适的数据解析器
 *
 * @author Daniel Lu
 * @since 2026-01-17
 */
public interface DataParserStrategy {

    /**
     * 根据元数据和基础数据选择合适的数据解析器
     *
     * @param metadata 元数据，包含额外的参考参数
     * @param data 基础爬取数据，用于提供根据数据内容选择解析器的能力
     * @return 合适的数据解析器，如果没有找到则返回null
     * @throws IllegalStateException 如果找到多个匹配的解析器
     */
    DataParser selectParser(Map<String, Object> metadata, Object data);
}
