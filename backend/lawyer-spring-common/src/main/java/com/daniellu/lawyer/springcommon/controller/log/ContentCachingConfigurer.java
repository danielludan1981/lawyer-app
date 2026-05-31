package com.daniellu.lawyer.springcommon.controller.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * 内容缓存配置类
 * 配置ContentCachingRequestWrapper和ContentCachingResponseWrapper，以支持多次读取请求体和响应体
 */
@Configuration
public class ContentCachingConfigurer {

    /**
     * 创建内容缓存过滤器
     * @return 内容缓存过滤器
     */
    @Bean
    public OncePerRequestFilter contentCachingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                // 创建内容缓存请求包装器
                ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request, 1024);

                // 创建内容缓存响应包装器
                ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

                try {
                    // 继续过滤链
                    filterChain.doFilter(cachingRequest, cachingResponse);
                } finally {
                    // 必须复制响应体，否则客户端将无法接收到响应
                    cachingResponse.copyBodyToResponse();
                }
            }
        };
    }
}