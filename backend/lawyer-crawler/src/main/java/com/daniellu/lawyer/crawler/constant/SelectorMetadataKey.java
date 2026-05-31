package com.daniellu.lawyer.crawler.constant;

/**
 * 选择器元数据键
 * 定义选择器配置中metadata字段的键名
 * SYS_ 前缀表示系统自动设置的元数据，用户无需主动设置。
 * USER_ 前缀表示用户主动设置的元数据，用户需要在配置中（如REST请求参数）主动设置。
 *
 * @author Daniel Lu
 * @since 2026-01-17
 */
public class SelectorMetadataKey {

    /**
     * URL元数据键
     * 用于存储选择器配置中指定的URL，该值不用用户主动设置。外部调用主动传递URL参数。
     */
    public static final String SYS_URL = "url";


    /**
     * 内容发布时间在多少天内爬取
     * 用于指定从当前时间开始，往前爬取多少天内的内容。
     */
    public static final String USER_PUBLISHED_IN_DAYS = "publishedInDays";

    /**
     * 数据解析器元数据键
     * 当用户指定数据解析器名称时，DataParserStrategy会根据名称选择合适的解析器。如果没有匹配的解析器，抛出异常
     */
    public static final String USER_DATA_PARSER = "dataParser";

}
