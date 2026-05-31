package com.daniellu.lawyer.springcommon.po;

import java.time.LocalDateTime;

import com.daniellu.lawyer.common.constant.DataStatus;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@Data
public abstract class AbstractJpaBasePO {

    @Column(name = "created_at", updatable = false, nullable = false)
    protected LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    protected LocalDateTime updatedAt;

    @Column(name = "status", nullable = false)
    protected Integer status;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = DataStatus.NORMAL;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
