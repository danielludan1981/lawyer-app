package com.daniellu.lawyer.common.dto;

import com.daniellu.lawyer.common.po.AbstractBasePO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class AbstractDataDTO extends AbstractBasePO {

}
