package com.daniellu.lawyer.springdemo1.service.converter;

import com.daniellu.lawyer.springdemo1.dto.UserDTO;
import com.daniellu.lawyer.springdemo1.dto.UserCreateDTO;
import com.daniellu.lawyer.springdemo1.dto.UserUpdateDTO;
import com.daniellu.lawyer.springdemo1.po.UserPO;
import org.springframework.stereotype.Component;

/**
 * 用户对象转换器，负责UserPO与UserDTO之间的转换
 *
 * @author Daniel Lu
 * @since 2026-01-10
 */
@Component
public class UserConverter {

    /**
     * 将UserPO转换为UserDTO
     */
    public UserDTO convertToDTO(UserPO userPO) {
        if (userPO == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userPO.getId());
        userDTO.setUsername(userPO.getUsername());
        userDTO.setEmail(userPO.getEmail());
        userDTO.setCreatedAt(userPO.getCreatedAt());
        userDTO.setUpdatedAt(userPO.getUpdatedAt());
        userDTO.setVersion(userPO.getVersion());
        userDTO.setStatus(userPO.getStatus());
        return userDTO;
    }

    /**
     * 将UserCreateDTO转换为UserPO
     */
    public UserPO convertToPO(UserCreateDTO userCreateDTO) {
        if (userCreateDTO == null) {
            return null;
        }

        return UserPO.builder()
                .username(userCreateDTO.getUsername())
                .email(userCreateDTO.getEmail())
                .build();
    }

    /**
     * 将UserUpdateDTO的属性更新到UserPO
     */
    public void updatePOFromDTO(UserUpdateDTO userUpdateDTO, UserPO userPO) {
        if (userUpdateDTO == null || userPO == null) {
            return;
        }

        userPO.setUsername(userUpdateDTO.getUsername());
        userPO.setEmail(userUpdateDTO.getEmail());
    }
}