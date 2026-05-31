package com.daniellu.lawyer.springcommon.controller.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.daniellu.lawyer.common.constant.CommonErrCode;
import com.daniellu.lawyer.common.dto.ResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * 权限拦截器
 * 用于处理权限校验逻辑
 */
@Component
public class PermissionInterceptor extends BaseInterceptor {

    // 权限配置映射表，key为请求URL，value为所需权限列表
    private final Map<String, List<String>> permissionConfig = new ConcurrentHashMap<>();

    // JSON序列化对象
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 请求处理前调用，进行权限校验
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @return true表示继续处理请求，false表示中断请求
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求URL
        String url = request.getRequestURI();

        // 获取请求方法
        String method = request.getMethod();

        // 组合URL和方法，作为权限配置的key
        String key = method + " " + url;

        // 获取当前用户的权限列表（实际项目中应该从认证信息中获取）
        List<String> userPermissions = getUserPermissions(request);

        // 检查是否需要权限校验
        if (permissionConfig.containsKey(key)) {
            // 获取所需权限列表
            List<String> requiredPermissions = permissionConfig.get(key);

            // 检查用户是否拥有所需权限
            boolean hasPermission = requiredPermissions.stream()
                    .anyMatch(userPermissions::contains);

            if (!hasPermission) {
                // 用户没有所需权限，返回无授权错误
                handleUnauthorized(request, response);
                return false;
            }
        }

        // 用户拥有所需权限，允许继续处理请求
        return true;
    }

    /**
     * 从请求中获取当前用户的权限列表
     * @param request 请求对象
     * @return 用户的权限列表
     */
    private List<String> getUserPermissions(HttpServletRequest request) {
        // 实际项目中，应该从认证信息中获取用户权限
        // 这里为了演示，返回一个默认的权限列表
        return List.of("ROLE_USER", "PERMISSION_READ");
    }

    /**
     * 处理无授权情况
     * @param request 请求对象
     * @param response 响应对象
     * @throws IOException IO异常
     */
    private void handleUnauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应状态码
        response.setStatus(HttpServletResponse.SC_OK);

        // 设置响应头
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 构建响应数据
        ResponseDTO<Void> responseDTO = ResponseDTO.fail(
                CommonErrCode.BUS_AUTHORIZATION_ERROR,
                "您没有权限访问该资源"
        );

        // 序列化响应数据
        String json = objectMapper.writeValueAsString(responseDTO);

        // 输出响应数据
        try (PrintWriter writer = response.getWriter()) {
            writer.write(json);
            writer.flush();
        }
    }

    /**
     * 配置权限
     * @param url 请求URL
     * @param method 请求方法
     * @param permissions 所需权限列表
     */
    public void configurePermission(String url, String method, List<String> permissions) {
        String key = method + " " + url;
        permissionConfig.put(key, permissions);
    }

    /**
     * 移除权限配置
     * @param url 请求URL
     * @param method 请求方法
     */
    public void removePermission(String url, String method) {
        String key = method + " " + url;
        permissionConfig.remove(key);
    }
}