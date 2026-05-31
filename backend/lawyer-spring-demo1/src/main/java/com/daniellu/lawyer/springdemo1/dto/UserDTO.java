package com.daniellu.lawyer.springdemo1.dto;

import com.daniellu.lawyer.common.dto.AbstractDataDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends AbstractDataDTO{

    private String username;
    private String email;

}
