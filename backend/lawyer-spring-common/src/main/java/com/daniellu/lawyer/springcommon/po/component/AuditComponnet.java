package com.daniellu.lawyer.springcommon.po.component;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * JPA审计类
 * 包含createdBy、updatedBy字段
 * 所有类都标记为@Embeddable
 */
@Embeddable
@Data
public class AuditComponnet {

    /**
     * 创建者ID
     */
    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    /**
     * 创建者用户名
     */
    @Column(name = "created_by_user_name")
    private String createdByUserName;

    /**
     * 更新者ID
     */
    @Column(name = "updated_by_user_id")
    private Long updatedByUserId;

    /**
     * 更新者用户名
     */
    @Column(name = "updated_by_user_name")
    private String updatedByUserName;
}