package com.daniellu.lawyer.common.exception;

import com.daniellu.lawyer.common.constant.CommonErrCode;

/**
 * 业务异常类
 * 表示业务逻辑层面的错误，如参数验证失败、数据不存在等
 */
public class BusinessException extends AbstractBaseException {

    public BusinessException() {
        super(CommonErrCode.BUS_OTHER_ERROR, "业务处理失败");
    }

    public BusinessException(String message) {
        super(CommonErrCode.BUS_OTHER_ERROR, message);
    }

    public BusinessException(String errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(String message, Throwable cause) {
        super(CommonErrCode.BUS_OTHER_ERROR, message, cause);
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public BusinessException(String errorCode, String message, Object details) {
        super(errorCode, message, details);
    }

    public BusinessException(String errorCode, String message, Object details, Throwable cause) {
        super(errorCode, message, details, cause);
    }
}