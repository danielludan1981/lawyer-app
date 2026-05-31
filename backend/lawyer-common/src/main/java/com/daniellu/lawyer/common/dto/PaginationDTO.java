package com.daniellu.lawyer.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 分页信息数据传输对象
 * 用于封装分页请求和响应的分页信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaginationDTO<T> {
    /**
     * 当前页码
     */
    private int pageNum;
    
    /**
     * 每页条数
     */
    private int pageSize;
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 总页数
     */
    private int pages;
    
    /**
     * 当前页数据列表
     */
    private List<T> list;
    
    /**
     * 是否有下一页
     */
    private boolean hasNext;
    
    /**
     * 是否有上一页
     */
    private boolean hasPrev;
    
    /**
     * 计算总页数并设置相关属性
     */
    public void calculatePages() {
        if (total <= 0) {
            this.pages = 0;
            this.hasNext = false;
            this.hasPrev = false;
            return;
        }
        
        this.pages = (int) Math.ceil((double) total / pageSize);
        this.hasNext = pageNum < pages;
        this.hasPrev = pageNum > 1;
    }
}