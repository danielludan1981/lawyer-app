package com.daniellu.lawyer.crawler.enums;

/**
 * 任务状态枚举
 * 定义爬取任务的各种执行状态
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
public enum TaskStatus {

    /**
     * 任务已创建
     */
    CREATED,

    /**
     * 任务正在执行
     */
    RUNNING,

    /**
     * 任务执行成功
     */
    SUCCESS,

    /**
     * 任务执行失败
     */
    FAILED,

    /**
     * 任务已取消
     */
    CANCELLED,

}
