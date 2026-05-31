package com.daniellu.lawyer.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * 统一请求数据传输对象
 * 用于封装POST、PUT、PATCH等请求的数据结构
 *
 * @param <T> 业务数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RequestDTO<T> {
    /**
     * 业务数据，泛型类型T
     */
    private T data;
    
    /**
     * 元数据，用于传递辅助信息
     * 如traceId、timestamp等
     */
    private Map<String, Object> metadata;
}
