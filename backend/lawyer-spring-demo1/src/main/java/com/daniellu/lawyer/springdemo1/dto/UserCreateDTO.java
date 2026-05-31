package com.daniellu.lawyer.springdemo1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    private String username;
    private String email;

}

