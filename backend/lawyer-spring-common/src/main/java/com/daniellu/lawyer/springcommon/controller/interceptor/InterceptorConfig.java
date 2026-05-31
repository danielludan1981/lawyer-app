package com.daniellu.lawyer.springcommon.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 * 用于注册所有的通用拦截器
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Autowired
    private RequestValidationInterceptor requestValidationInterceptor;

    /**
     * 注册拦截器
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册权限拦截器
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                // 可以根据需要排除某些路径
                .excludePathPatterns("/api/public/**", "/api/auth/**");

        // 注册请求参数校验拦截器
        registry.addInterceptor(requestValidationInterceptor)
                .addPathPatterns("/**")
                // 可以根据需要排除某些路径
                .excludePathPatterns("/static/**", "/favicon.ico");
    }
}