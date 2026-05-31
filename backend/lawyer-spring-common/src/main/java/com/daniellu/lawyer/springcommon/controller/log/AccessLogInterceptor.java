package com.daniellu.lawyer.springcommon.controller.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Access日志记录拦截器
 * 记录所有Controller的请求和响应日志，包括请求URL、方法、参数、响应状态、处理时间等信息
 */
@Slf4j
@Component
public class AccessLogInterceptor implements HandlerInterceptor {

    // 使用NamedThreadLocal存储请求开始时间，确保线程安全
    private static final NamedThreadLocal<Long> REQUEST_START_TIME = new NamedThreadLocal<>("requestStartTime");

    // 使用NamedThreadLocal存储请求ID，用于关联请求和响应日志
    private static final NamedThreadLocal<String> REQUEST_ID = new NamedThreadLocal<>("requestId");

    /**
     * 请求处理前调用
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @return true表示继续处理请求，false表示中断请求
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 生成请求ID
        String requestId = java.util.UUID.randomUUID().toString().replace("-", "");
        REQUEST_ID.set(requestId);

        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        REQUEST_START_TIME.set(startTime);

        // 记录请求日志
        logRequest(request, requestId);

        return true;
    }

    /**
     * 请求处理完成后调用（视图渲染前）
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @param modelAndView 模型和视图
     * @throws Exception 异常
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 这个方法主要用于处理视图渲染，在REST接口中通常不需要处理
    }

    /**
     * 请求完全处理完成后调用（视图渲染后）
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @param ex 异常对象
     * @throws Exception 异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 获取请求ID和开始时间
        String requestId = REQUEST_ID.get();
        Long startTime = REQUEST_START_TIME.get();

        if (startTime != null) {
            // 计算处理时间
            long processingTime = System.currentTimeMillis() - startTime;

            // 记录响应日志
            logResponse(request, response, requestId, processingTime, ex);
        }

        // 清除线程本地变量，避免内存泄漏
        REQUEST_ID.remove();
        REQUEST_START_TIME.remove();
    }

    /**
     * 记录请求日志
     * @param request 请求对象
     * @param requestId 请求ID
     */
    private void logRequest(HttpServletRequest request, String requestId) {
        try {
            // 获取请求URL
            String url = request.getRequestURL().toString();

            // 获取请求方法
            String method = request.getMethod();

            // 获取客户端IP地址
            String clientIp = getClientIp(request);

            // 获取请求头
            Map<String, String> headers = getHeaders(request);

            // 获取请求参数
            Map<String, String[]> params = request.getParameterMap();

            // 获取请求体
            String requestBody = getRequestBody(request);

            // 构建日志信息
            Map<String, Object> logData = new HashMap<>();
            logData.put("requestId", requestId);
            logData.put("timestamp", System.currentTimeMillis());
            logData.put("url", url);
            logData.put("method", method);
            logData.put("clientIp", clientIp);
            logData.put("headers", headers);
            logData.put("params", params);

            // 只在请求体非空时记录
            if (StringUtils.hasText(requestBody)) {
                logData.put("requestBody", requestBody);
            }

            // 记录日志
            log.info("ACCESS_LOG_REQUEST: {}", logData);

        } catch (Exception e) {
            log.error("Failed to log request", e);
        }
    }

    /**
     * 记录响应日志
     * @param request 请求对象
     * @param response 响应对象
     * @param requestId 请求ID
     * @param processingTime 处理时间（毫秒）
     * @param ex 异常对象
     */
    private void logResponse(HttpServletRequest request, HttpServletResponse response, String requestId, long processingTime, Exception ex) {
        try {
            // 获取响应状态码
            int status = response.getStatus();

            // 构建日志信息
            Map<String, Object> logData = new HashMap<>();
            logData.put("requestId", requestId);
            logData.put("timestamp", System.currentTimeMillis());
            logData.put("url", request.getRequestURL().toString());
            logData.put("method", request.getMethod());
            logData.put("status", status);
            logData.put("processingTime", processingTime + "ms");

            // 如果有异常，记录异常信息
            if (ex != null) {
                logData.put("exception", ex.getClass().getName());
                logData.put("exceptionMessage", ex.getMessage());
            }

            // 记录日志
            log.info("ACCESS_LOG_RESPONSE: {}", logData);

        } catch (Exception e) {
            log.error("Failed to log response", e);
        }
    }

    /**
     * 获取客户端IP地址
     * @param request 请求对象
     * @return 客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多个IP，取第一个（X-Forwarded-For可能包含多个IP，如client, proxy1, proxy2）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取请求头
     * @param request 请求对象
     * @return 请求头Map
     */
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    /**
     * 获取请求体
     * @param request 请求对象
     * @return 请求体内容
     * @throws IOException IO异常
     */
    private String getRequestBody(HttpServletRequest request) throws IOException {
        // 如果请求对象是ContentCachingRequestWrapper类型，使用其缓存的请求体
        if (request instanceof org.springframework.web.util.ContentCachingRequestWrapper) {
            org.springframework.web.util.ContentCachingRequestWrapper cachingRequest =
                (org.springframework.web.util.ContentCachingRequestWrapper) request;
            byte[] content = cachingRequest.getContentAsByteArray();
            return new String(content, StandardCharsets.UTF_8);
        } else {
            // 否则直接读取请求体（注意：这种方式只能读取一次请求体）
            return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        }
    }
}