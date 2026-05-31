package com.daniellu.lawyer.springdemo1.service;

import com.daniellu.lawyer.springdemo1.dto.UserDTO;
import com.daniellu.lawyer.springdemo1.dto.UserCreateDTO;
import com.daniellu.lawyer.springdemo1.dto.UserUpdateDTO;

import java.util.List;
import java.util.Optional;

/**
* 用户服务接口，负责用户相关的业务逻辑处理
*
* @author Daniel Lu
* @since 2026-01-03
*/
public interface UserService {

    /**
    * 创建用户
    *
    * @param userCreateDTO 用户创建信息
    * @return 创建的用户信息
    * @throws IllegalArgumentException 当用户名或邮箱已存在时抛出
    */
    UserDTO createUser(UserCreateDTO userCreateDTO);

    /**
    * 获取所有用户
    *
    * @return 用户列表
    */
    List<UserDTO> getAllUsers();

    /**
    * 根据ID获取用户
    *
    * @param id 用户ID
    * @return 用户信息，如果不存在则返回Optional.empty()
    */
    Optional<UserDTO> getUserById(Long id);

    /**
    * 根据用户名获取用户
    *
    * @param username 用户名
    * @return 用户信息，如果不存在则返回Optional.empty()
    */
    Optional<UserDTO> getUserByUsername(String username);

    /**
    * 更新用户信息
    *
    * @param id 用户ID
    * @param userUpdateDTO 用户更新信息
    * @return 更新后的用户信息
    * @throws IllegalArgumentException 当用户不存在时抛出
    */
    UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);

    /**
    * 删除用户
    *
    * @param id 用户ID
    * @throws IllegalArgumentException 当用户不存在时抛出
    */
    void deleteUser(Long id);
}