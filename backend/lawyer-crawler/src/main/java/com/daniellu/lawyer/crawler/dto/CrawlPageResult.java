    package com.daniellu.lawyer.crawler.dto;

import java.util.List;

import com.daniellu.lawyer.common.dto.ErrorDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 单页爬取结果
 * 封装单个页面的爬取结果
 *
 * @author Daniel Lu
 * @since 2026-01-16
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class CrawlPageResult {

    /**
     * 该页面爬取的完整URL
     */
    private String url;

    /**
     * 该页爬取结果数据
     * 单页爬取时为Map<String, Object>
     * 多页或列表爬取时为List<Map<String, Object>>
     */
    private Object data;

    /**
     * 该页原始HTML内容
     */
    private String rawHtml;

    /**
     * 该页爬取耗时（毫秒）
     */
    private long elapsedTime;

    /**
     * 该页是否爬取成功
     */
    private boolean success;

    /**
     * 该页爬取失败时的错误信息
     */
    private ErrorDTO error;

    /**
     * 页面内容的MD5值
     * 如果SelectorConfig指定了listSelector，则使用该选择器抓取的html生成md5
     * 否则使用rawHtml生成md5
     */
    private String md5;

    /**
     * 获取数据记录数
     * 如果data为List，则返回List大小
     * 否则返回1（表示单条记录）
     */
    public int getDataRecordCount() {
        if (data == null) {
            return 0;
        }
        if (data instanceof List) {
            return ((List<?>) data).size();
        }
        return 1;
    }

}