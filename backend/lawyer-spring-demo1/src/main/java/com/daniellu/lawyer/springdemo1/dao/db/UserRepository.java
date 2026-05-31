package com.daniellu.lawyer.springdemo1.dao.db;

import com.daniellu.lawyer.springdemo1.po.UserPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserPO, Long> {

    // 根据用户名查询用户
    UserPO findByUsername(String username);

    // 根据邮箱查询用户
    UserPO findByEmail(String email);

    // 检查用户名是否存在
    boolean existsByUsername(String username);

    // 检查邮箱是否存在
    boolean existsByEmail(String email);

}