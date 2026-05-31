package com.daniellu.lawyer.springdemo1.service.impl;

import com.daniellu.lawyer.springdemo1.dto.UserDTO;
import com.daniellu.lawyer.springdemo1.dto.UserCreateDTO;
import com.daniellu.lawyer.springdemo1.dto.UserUpdateDTO;
import com.daniellu.lawyer.springdemo1.po.UserPO;
import com.daniellu.lawyer.common.constant.CommonErrCode;
import com.daniellu.lawyer.common.exception.BusinessException;
import com.daniellu.lawyer.springdemo1.dao.db.UserRepository;
import com.daniellu.lawyer.springdemo1.service.UserService;
import com.daniellu.lawyer.springdemo1.service.converter.UserConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* 用户服务实现类，负责用户相关的业务逻辑处理
*
* @author Daniel Lu
* @since 2026-01-03
*/
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    @Override
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        // 不检查用户名和邮箱是否已存在，让数据库约束来处理

        UserPO userPO = userConverter.convertToPO(userCreateDTO);
        UserPO savedUserPO = userRepository.save(userPO);
        return userConverter.convertToDTO(savedUserPO);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userConverter::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userConverter::convertToDTO);
    }

    @Override
    public Optional<UserDTO> getUserByUsername(String username) {
        UserPO userPO = userRepository.findByUsername(username);
        return Optional.ofNullable(userPO)
                .map(userConverter::convertToDTO);
    }

    @Override
    public UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        return userRepository.findById(id)
                .map(userPO -> {
                    userConverter.updatePOFromDTO(userUpdateDTO, userPO);
                    UserPO updatedUserPO = userRepository.save(userPO);
                    return userConverter.convertToDTO(updatedUserPO);
                })
                .orElseThrow(() -> new BusinessException(CommonErrCode.BUS_DATA_NOT_FOUND_ERROR, "用户不存在，ID: " + id));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException(CommonErrCode.BUS_DATA_NOT_FOUND_ERROR, "用户不存在，ID: " + id);
        }
        userRepository.deleteById(id);
    }
}