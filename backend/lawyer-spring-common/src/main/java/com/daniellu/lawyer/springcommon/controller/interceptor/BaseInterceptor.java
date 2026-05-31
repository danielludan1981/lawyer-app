package com.daniellu.lawyer.springcommon.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 基础拦截器
 * 提供基础的拦截器功能，如请求预处理、响应后处理等
 */
public abstract class BaseInterceptor implements HandlerInterceptor {

    /**
     * 未知IP地址常量
     */
    protected static final String UNKNOWN_IP = "unknown";

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
        // 默认返回true，允许请求继续处理
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
        // 默认不做处理
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
        // 默认不做处理
    }

    /**
     * 获取客户端IP地址
     * @param request 请求对象
     * @return 客户端IP地址
     */
    protected String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN_IP.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多个IP，取第一个（X-Forwarded-For可能包含多个IP，如client, proxy1, proxy2）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 检查请求是否为AJAX请求
     * @param request 请求对象
     * @return true表示是AJAX请求，false表示不是
     */
    protected boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(header);
    }
}