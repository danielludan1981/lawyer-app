package com.daniellu.lawyer.springcommon.controller.advice;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.daniellu.lawyer.common.constant.CommonErrCode;
import com.daniellu.lawyer.common.dto.ResponseDTO;
import com.daniellu.lawyer.common.exception.BusinessException;
import com.daniellu.lawyer.common.exception.SystemException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理器
 * 统一处理所有Controller层的异常，将其转换为自定义的响应结构返回给前端
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * @param e 业务异常
     * @return 统一响应结构
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDTO<Void>> handleBusinessException(BusinessException e) {
        ResponseDTO<Void> response = ResponseDTO.fail(e.getErrorCode(), e.getMessage(), e.getDetails());
        log.error("BusinessException - errorCode: {}, errorMessage: {}", e.getErrorCode(), e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理系统异常
     * @param e 系统异常
     * @return 统一响应结构
     */
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ResponseDTO<Void>> handleSystemException(SystemException e) {
        ResponseDTO<Void> response = ResponseDTO.fail(e.getErrorCode(), e.getMessage(), e.getDetails());
        log.error("SystemException - errorCode: {}, errorMessage: {}", e.getErrorCode(), e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理数据库约束异常（如唯一索引冲突）
     * @param e 数据库约束异常
     * @return 统一响应结构
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseDTO<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String errorMessage = "数据操作失败：" + e.getMessage();
        String errorCode = CommonErrCode.BUS_DUPLICATE_DATA_ERROR;

        // 尝试从异常信息中提取更具体的错误信息
        if (e.getMessage().contains("unique constraint")) {
            errorMessage = "数据已存在，请检查输入信息";
        }
        log.error("DataIntegrityViolationException - errorCode: {}, errorMessage: {}", errorCode, errorMessage);
        ResponseDTO<Void> response = ResponseDTO.fail(errorCode, errorMessage, null);
        return ResponseEntity.ok(response);
    }

    /**
     * 处理实体未找到异常
     * @param e 实体未找到异常
     * @return 统一响应结构
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseDTO<Void>> handleEntityNotFoundException(EntityNotFoundException e) {
        ResponseDTO<Void> response = ResponseDTO.fail(CommonErrCode.BUS_DATA_NOT_FOUND_ERROR, "请求的资源不存在", null);
        log.error("EntityNotFoundException - errorCode: {}, errorMessage: {}", CommonErrCode.BUS_DATA_NOT_FOUND_ERROR, e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理方法参数验证异常（@Validated注解验证失败）
     * @param e 方法参数验证异常
     * @return 统一响应结构
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 收集所有验证错误信息
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "参数验证失败"
                ));

        ResponseDTO<Void> response = ResponseDTO.fail(
                CommonErrCode.BUS_PARAMETER_ERROR,
                "请求参数验证失败",
                errors
        );
        log.error("MethodArgumentNotValidException - errorCode: {}, errorMessage: {}", CommonErrCode.BUS_PARAMETER_ERROR, e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理数据绑定异常（如请求参数类型不匹配）
     * @param e 数据绑定异常
     * @return 统一响应结构
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseDTO<Void>> handleBindException(BindException e) {
        // 收集所有绑定错误信息
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "参数绑定失败"
                ));

        ResponseDTO<Void> response = ResponseDTO.fail(
                CommonErrCode.BUS_PARAMETER_ERROR,
                "请求参数绑定失败",
                errors
        );
        log.error("BindException - errorCode: {}, errorMessage: {}", CommonErrCode.BUS_PARAMETER_ERROR, e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理缺少请求参数异常
     * @param e 缺少请求参数异常
     * @return 统一响应结构
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDTO<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        ResponseDTO<Void> response = ResponseDTO.fail(
                CommonErrCode.BUS_PARAMETER_ERROR,
                "缺少必要的请求参数：" + e.getParameterName(),
                null
        );
        log.error("MissingServletRequestParameterException - errorCode: {}, errorMessage: {}", CommonErrCode.BUS_PARAMETER_ERROR, e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理HTTP消息不可读异常（如JSON格式错误）
     * @param e HTTP消息不可读异常
     * @return 统一响应结构
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDTO<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ResponseDTO<Void> response = ResponseDTO.fail(
                CommonErrCode.BUS_PARAMETER_ERROR,
                "请求数据格式错误，请检查JSON语法",
                null
        );
        log.error("HttpMessageNotReadableException - errorCode: {}, errorMessage: {}", CommonErrCode.BUS_PARAMETER_ERROR, e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理请求方法不支持异常
     * @param e 请求方法不支持异常
     * @return 统一响应结构
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDTO<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        ResponseDTO<Void> response = ResponseDTO.fail(
                CommonErrCode.BUS_PARAMETER_ERROR,
                "不支持的请求方法：" + e.getMethod(),
                null
        );
        log.error("HttpRequestMethodNotSupportedException - errorCode: {}, errorMessage: {}", CommonErrCode.BUS_PARAMETER_ERROR, e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理HTTP消息转换异常（如JSON格式错误）
     * @param e HTTP消息转换异常
     * @return 统一响应结构
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ResponseDTO<Void>> handleHttpMessageConversionException(HttpMessageConversionException e) {
        ResponseDTO<Void> response = ResponseDTO.fail(
                CommonErrCode.BUS_PARAMETER_ERROR,
                "请求数据格式错误，请检查JSON语法",
                null
        );
        log.error("HttpMessageConversionException - errorCode: {}, errorMessage: {}", CommonErrCode.BUS_PARAMETER_ERROR, e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理资源未找到异常
     * @param e 资源未找到异常
     * @return 统一响应结构
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseDTO<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        ResponseDTO<Void> response = ResponseDTO.fail(
                CommonErrCode.BUS_RESOURCE_NOT_FOUND_ERROR,
                "请求的资源不存在",
                null
        );
        log.error("NoResourceFoundException - errorCode: {}, errorMessage: {}", CommonErrCode.BUS_RESOURCE_NOT_FOUND_ERROR, e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理验证异常
     * @param e 验证异常
     * @return 统一响应结构
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseDTO<Void>> handleValidationException(ValidationException e) {
        ResponseDTO<Void> response = ResponseDTO.fail(
                CommonErrCode.BUS_PARAMETER_ERROR,
                "数据验证失败：" + e.getMessage(),
                null
        );
        log.error("ValidationException - errorCode: {}, errorMessage: {}", CommonErrCode.BUS_PARAMETER_ERROR, e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理SQLException异常
     * @param e SQLException异常
     * @return 统一响应结构
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ResponseDTO<Void>> handleSQLException(SQLException e) {
        ResponseDTO<Void> response = ResponseDTO.fail(
                CommonErrCode.SYS_DATABASE_ERROR,
                "数据库操作失败",
                null
        );
        log.error("SQLException - errorCode: {}, errorMessage: {}", CommonErrCode.SYS_DATABASE_ERROR, e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理IOException异常
     * @param e IOException异常
     * @return 统一响应结构
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ResponseDTO<Void>> handleIOException(IOException e) {
        ResponseDTO<Void> response = ResponseDTO.fail(
                CommonErrCode.SYS_IO_ERROR,
                "IO操作失败",
                null
        );
        log.error("IOException - errorCode: {}, errorMessage: {}", CommonErrCode.SYS_IO_ERROR, e.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * 处理其他所有未捕获的异常
     * @param e 未捕获的异常
     * @return 统一响应结构
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Void>> handleException(Exception e) {
        ResponseDTO<Void> response = ResponseDTO.fail(
                CommonErrCode.SYS_OTHER_ERROR,
                "服务器内部错误",
                null
        );
        // 未知异常（预期外）打印完整栈轨迹
        log.error("Exception - errorCode: {}, errorMessage: {}", CommonErrCode.SYS_OTHER_ERROR, e.getMessage(), e);
        return ResponseEntity.ok(response);
    }
}