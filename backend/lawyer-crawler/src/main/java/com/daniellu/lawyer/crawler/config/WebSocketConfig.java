package com.daniellu.lawyer.crawler.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置类
 * 配置STOMP协议的WebSocket支持
 *
 * @author Daniel Lu
 * @since 2026-01-11
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的消息代理，用于广播消息
        config.enableSimpleBroker("/topic");
        
        // 设置应用程序目的地前缀
        config.setApplicationDestinationPrefixes("/app");
        
        // 设置用户目的地前缀
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点，客户端将使用此端点连接
        registry.addEndpoint("/ws-crawler")
                .setAllowedOrigins("*") // 允许所有来源，生产环境应限制
                .withSockJS(); // 启用SockJS回退支持
    }
}
