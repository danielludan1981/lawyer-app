package com.daniellu.lawyer.springcommon.controller.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 用于配置Spring MVC的拦截器、视图解析器等
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AccessLogInterceptor accessLogInterceptor;

    /**
     * 注册拦截器
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册AccessLogInterceptor，拦截所有请求
        registry.addInterceptor(accessLogInterceptor)
                .addPathPatterns("/**")
                // 可以根据需要排除某些路径，如静态资源
                .excludePathPatterns("/static/**", "/favicon.ico");
    }
}