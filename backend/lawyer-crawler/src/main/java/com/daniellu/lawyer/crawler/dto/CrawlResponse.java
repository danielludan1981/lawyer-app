package com.daniellu.lawyer.crawler.dto;

import java.util.Map;

import com.daniellu.lawyer.common.dto.ErrorDTO;
import com.daniellu.lawyer.crawler.enums.CrawlStrategy;
import com.daniellu.lawyer.crawler.enums.TaskStatus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 爬取结果
 * 封装爬取任务的执行结果
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class CrawlResponse {

    /**
     * 任务元数据
     */
    private TaskMetadata metadata;

    /**
     * 爬取状态
     */
    private TaskStatus status;

    /**
     * 爬取结果映射
     * key: 页面完整URL
     * value: 对应页面的爬取结果
     */
    private Map<String, CrawlPageResult> pageResultMap;

    /**
     * 爬取总耗时（毫秒）
     */
    private long elapsedTime;

    /**
     * 爬取的页面数量
     */
    private int pageCount;

    /**
     * 错误信息
     */
    private ErrorDTO error;

    /**
     * 使用的爬取策略
     */
    private CrawlStrategy usedStrategy;
}
