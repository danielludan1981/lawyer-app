package com.daniellu.lawyer.crawler.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
/**
 * 分页配置模型
 * 包含分页URL的后缀列表和模板，用于构建完整的分页URL。当提供分页URL模板时，
 * 分页URL后缀列表中的元素将被替换到模板中。
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
public class CrawlPagination {

    /**
     * 分页URL后缀列表，每个元素都是分页URL的一部分，用于构建完整的分页URL。
     * 例如：仅包含参数部分["?page=1", "?page=2", "?page=3"]
     * 例如：仅包含URL路径部分["/page/1", "/page/2", "/page/3"]
     */
    private List<String> urlSegments;

    /**
     * 分页URL模板，用于构建完整的分页URL。当使用模板时必须包含一个占位符（例如："{}"），
     * 该占位符将被分页URL后缀列表中的元素替换。 使用模板时必须设置fromPage和endPage。
     * 例如：fromPage = 1, endPage = 3, urlTemplate = "/page/{}", 表示的urlSegments为["/page/1", "/page/2", "/page/3"]
     */
    private String urlTemplate;

     /**
      * 分页URL模板中占位符的索引，表示起始分页数。
      * 起始分页数必须小于等于结束分页数。
      */
    private Integer fromPage;

    /**
     * 分页URL模板中占位符的索引，表示结束分页数。
     */
    private Integer endPage;

}
