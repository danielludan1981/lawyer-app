package com.daniellu.lawyer.crawler.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 任务元数据
 * 存储爬取任务的元信息，用于识别和跟踪任务
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class TaskMetadata {

    /**
     * 任务唯一标识符
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务创建时间戳
     */
    private long createdAt;

    /**
     * 任务创建者
     */
    private String createdByUserName;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 回调URL
     * 任务完成后通知的地址
     */
    private String callbackUrl;

    /**
     * 额外的任务参数
     */
    private String extraParams;
}
