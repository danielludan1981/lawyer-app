package com.daniellu.lawyer.springcommon.po.component;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * JPA UUID类
 * 包含uuid字段
 * 所有类都标记为@Embeddable
 */
@Embeddable
public class UUIDComponent {

    /**
     * UUID字符串，尤其在MySQL中使用BINARY(16)存储UUID时
     */
    @Column(name = "uuid", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    protected java.util.UUID uuid;


}