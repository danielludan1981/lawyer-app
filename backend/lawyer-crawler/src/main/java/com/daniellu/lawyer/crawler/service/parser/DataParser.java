package com.daniellu.lawyer.crawler.service.parser;

import java.util.Map;

/**
 * 数据解析器接口
 * 定义数据解析和支持判断的方法
 *
 * @author Daniel Lu
 * @since 2026-01-17
 */
public interface DataParser {

    /**
     * 根据基础数据data，进行解析并返回处理后的结果
     *
     * @param data 基础数据，由DataExtractor提取
     * @param metadata 元数据，包含额外的参考参数
     * @return 处理后的结果
     */
    Object parse(Object data, Map<String, Object> metadata);

    /**
     * 返回解析器的名称
     * 用于在日志和调试中标识解析器
     *
     * @return 解析器名称
     */
    String getName();

    /**
     * 返回解析器是否支持当前metadata和基础数据
     * 用于判断是否选择该解析器
     *
     * @param metadata 元数据，包含额外的参考参数
     * @param data 基础爬取数据，用于提供根据数据内容选择解析器的能力
     * @return 是否支持当前metadata和数据
     */
    boolean supports(Map<String, Object> metadata, Object data);

    /**
     * 获取匹配度
     * 值越高证明约匹配
     *
     * @param metadata 元数据
     * @param data 数据
     * @return 匹配度, 值越高证明约匹配
     */
    int getMatchScore(Map<String, Object> metadata, Object data);
}
