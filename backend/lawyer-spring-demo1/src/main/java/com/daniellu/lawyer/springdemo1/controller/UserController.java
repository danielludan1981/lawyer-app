package com.daniellu.lawyer.springdemo1.controller;

import com.daniellu.lawyer.springdemo1.dto.UserDTO;
import com.daniellu.lawyer.springdemo1.dto.UserCreateDTO;
import com.daniellu.lawyer.springdemo1.dto.UserUpdateDTO;
import com.daniellu.lawyer.springdemo1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 创建用户
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        UserDTO createdUser = userService.createUser(userCreateDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.OK);
    }

    // 获取所有用户
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // 根据ID获取用户
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<UserDTO> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 根据用户名获取用户
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        Optional<UserDTO> user = userService.getUserByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 更新用户信息
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO updatedUser = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}