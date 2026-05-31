package com.daniellu.lawyer.springcommon.controller.interceptor;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.stereotype.Component;

import com.daniellu.lawyer.common.constant.CommonErrCode;
import com.daniellu.lawyer.common.dto.ResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

/**
 * 请求参数校验拦截器
 * 用于处理请求参数的校验逻辑
 */
@Component
@RequiredArgsConstructor
public class RequestValidationInterceptor extends BaseInterceptor {

    // JSON序列化对象
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 请求处理前调用，进行请求参数校验
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @return true表示继续处理请求，false表示中断请求
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 进行请求参数校验
            validateRequest(request);

            // 校验通过，允许继续处理请求
            return true;
        } catch (ValidationException e) {
            // 校验失败，返回参数错误响应
            handleValidationError(request, response, e);
            return false;
        }
    }

    /**
     * 进行请求参数校验
     * @param request 请求对象
     * @throws ValidationException 校验失败时抛出的异常
     */
    private void validateRequest(HttpServletRequest request) throws ValidationException {
        // 实际项目中，应该根据请求参数的类型和注解进行校验
        // 这里为了演示，只检查请求方法为POST、PUT、PATCH时是否有请求体
        String method = request.getMethod();
        if (isBodyRequired(method)) {
            // 检查请求体是否为空
            if (request.getContentLengthLong() == 0) {
                throw new ValidationException("请求体不能为空");
            }
        }

        // 可以在这里添加更多的参数校验逻辑
        // 例如：检查必填参数是否存在、检查参数格式是否正确等
    }

    /**
     * 检查请求方法是否需要请求体
     * @param method 请求方法
     * @return true表示需要请求体，false表示不需要
     */
    private boolean isBodyRequired(String method) {
        return "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method);
    }

    /**
     * 处理参数校验错误
     * @param request 请求对象
     * @param response 响应对象
     * @param e 校验异常
     * @throws IOException IO异常
     */
    private void handleValidationError(HttpServletRequest request, HttpServletResponse response, ValidationException e) throws IOException {
        // 设置响应状态码
        response.setStatus(HttpServletResponse.SC_OK);

        // 设置响应头
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 构建响应数据
        ResponseDTO<Void> responseDTO = ResponseDTO.fail(
                CommonErrCode.BUS_PARAMETER_ERROR,
                "请求参数校验失败: " + e.getMessage()
        );

        // 序列化响应数据
        String json = objectMapper.writeValueAsString(responseDTO);

        // 输出响应数据
        try (PrintWriter writer = response.getWriter()) {
            writer.write(json);
            writer.flush();
        }
    }
}