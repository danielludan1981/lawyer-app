package com.daniellu.lawyer.crawler.service.data;

import org.jsoup.nodes.Document;

import com.daniellu.lawyer.crawler.dto.DataExtractorResult;
import com.daniellu.lawyer.crawler.dto.SelectorConfig;

/**
 * 数据提取接口
 * 定义从HTML文档中提取数据的方法
 *
 * @author Daniel Lu
 * @since 2026-01-17
 */
public interface DataExtractor {

    /**
     * 根据选择器配置从HTML文档中抽取数据
     *
     * @param document      HTML文档对象
     * @param selectorConfig 选择器配置
     * @return 数据提取结果，包含提取的数据、原始HTML和MD5值
     */
    DataExtractorResult extractData(Document document, SelectorConfig selectorConfig);
}
