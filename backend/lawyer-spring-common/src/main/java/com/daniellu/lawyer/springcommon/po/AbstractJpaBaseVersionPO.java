package com.daniellu.lawyer.springcommon.po;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@MappedSuperclass
@Data
public abstract class AbstractJpaBaseVersionPO extends AbstractJpaBasePO {

    @Version
    private Integer version;
}
