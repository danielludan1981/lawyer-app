package com.daniellu.lawyer.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {
    /**
     * 错误码
     */
    private String code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 错误详情
     */
    private Object details;
}