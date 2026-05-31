package com.daniellu.lawyer.springdemo1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 配置CSRF保护
                .csrf(csrf -> csrf.disable())

                // 配置请求授权规则
                .authorizeHttpRequests(authorize -> authorize
                        // 允许不需要认证的端点
                        .requestMatchers("/", "/index.html", "/h2-console/**").permitAll()
                        .requestMatchers("/api/performance/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/prometheus", "/actuator/metrics").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // 需要认证的端点
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )

                // 配置表单登录
                .formLogin(withDefaults())

                // 配置HTTP Basic认证
                .httpBasic(withDefaults())

                // 配置H2控制台支持iframe
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    /**
     * 配置用户详情服务
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // 创建内存中的用户存储
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * 配置密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}