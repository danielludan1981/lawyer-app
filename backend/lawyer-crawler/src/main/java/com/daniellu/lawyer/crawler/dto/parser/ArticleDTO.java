package com.daniellu.lawyer.crawler.dto.parser;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 解析文章内容
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class ArticleDTO {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章来源
     */
    private String source;

    /**
     * 文章来源URL
     */
    private String url;

}
