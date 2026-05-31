package com.daniellu.lawyer.common.po;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 持久化对象基础类
 * 定义了PO类可能会使用的公共字段
 */
@Data
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractBasePO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 创建人用户ID
     */
    private Long createdByUserId;

    /**
     * 创建人
     */
    private String createByUserName;

    /**
     * 更新人用户ID
     */
    private Long updatedByUserId;

    /**
     * 更新人
     */
    private String updatedByUserName;


    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 逻辑删除标识（0：未删除，1：已删除）
     */
    private Integer status;
}