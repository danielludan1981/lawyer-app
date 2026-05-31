package com.daniellu.lawyer.common.dto;

import com.daniellu.lawyer.common.constant.CommonErrCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 统一响应数据传输对象
 * 用于封装API接口的响应结构
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
public class ResponseDTO<T> {
    /**
     * 响应状态：true表示成功，false表示失败
     */
    private boolean success;

    /**
     * 可选，用于表示额外的成功状态码（如201、204等）
     */
    private Integer code;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 分页信息，仅在列表接口中包含
     */
    private PaginationDTO pagination;

    /**
     * 错误信息，仅在失败响应中包含
     */
    private ErrorDTO error;

    /**
     * 响应时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 成功响应静态方法
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 成功响应对象
     */
    public static <T> ResponseDTO<T> success(T data) {
        return success(data, null, null);
    }

    /**
     * 成功响应静态方法（带状态码）
     *
     * @param data 响应数据
     * @param code 额外的成功状态码
     * @param <T>  响应数据类型
     * @return 成功响应对象
     */
    public static <T> ResponseDTO<T> success(T data, Integer code) {
        return success(data, code, null);
    }

    /**
     * 成功响应静态方法（带分页）
     *
     * @param data       响应数据
     * @param pagination 分页信息
     * @param <T>        响应数据类型
     * @return 成功响应对象
     */
    public static <T> ResponseDTO<T> success(T data, PaginationDTO pagination) {
        return success(data, null, pagination);
    }

    /**
     * 成功响应静态方法（带状态码和分页）
     *
     * @param data       响应数据
     * @param code       额外的成功状态码
     * @param pagination 分页信息
     * @param <T>        响应数据类型
     * @return 成功响应对象
     */
    public static <T> ResponseDTO<T> success(T data, Integer code, PaginationDTO pagination) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setSuccess(true);
        response.setData(data);
        response.setCode(code);
        response.setPagination(pagination);
        response.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));
        return response;
    }

    /**
     * 成功响应静态方法（无数据）
     *
     * @param <T> 响应数据类型
     * @return 成功响应对象
     */
    public static <T> ResponseDTO<T> success() {
        return success(null);
    }

    /**
     * 失败响应静态方法
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     响应数据类型
     * @return 失败响应对象
     */
    public static <T> ResponseDTO<T> fail(String code, String message) {
        return fail(code, message, null);
    }

    /**
     * 失败响应静态方法（带错误详情）
     *
     * @param code    错误码
     * @param message 错误消息
     * @param details 错误详情
     * @param <T>     响应数据类型
     * @return 失败响应对象
     */
    public static <T> ResponseDTO<T> fail(String code, String message, Object details) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setSuccess(false);
        response.setError(new ErrorDTO(code, message, details));
        response.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));
        return response;
    }

    /**
     * 失败响应静态方法（默认错误码）
     *
     * @param message 错误消息
     * @param <T>     响应数据类型
     * @return 失败响应对象
     */
    public static <T> ResponseDTO<T> fail(String message) {
        return fail(CommonErrCode.BUS_OTHER_ERROR, message);
    }

    /**
     * 分页信息DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationDTO {
        /**
         * 当前页码
         */
        private int page;

        /**
         * 每页数量
         */
        private int pageSize;

        /**
         * 总记录数
         */
        private long total;

        /**
         * 总页数
         */
        private int totalPages;

        /**
         * 是否有下一页
         */
        private boolean hasNext;

        /**
         * 是否有上一页
         */
        private boolean hasPrev;
    }
}