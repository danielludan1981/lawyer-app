package com.daniellu.lawyer.common.exception;

import com.daniellu.lawyer.common.constant.CommonErrCode;

/**
 * 系统异常类
 * 表示系统级别的错误，如数据库连接失败、服务调用超时等
 */
public class SystemException extends AbstractBaseException {

    public SystemException() {
        super(CommonErrCode.SYS_OTHER_ERROR, "系统内部错误");
    }

    public SystemException(String message) {
        super(CommonErrCode.SYS_OTHER_ERROR, message);
    }

    public SystemException(String errorCode, String message) {
        super(errorCode, message);
    }

    public SystemException(String message, Throwable cause) {
        super(CommonErrCode.SYS_OTHER_ERROR, message, cause);
    }

    public SystemException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public SystemException(String errorCode, String message, Object details) {
        super(errorCode, message, details);
    }

    public SystemException(String errorCode, String message, Object details, Throwable cause) {
        super(errorCode, message, details, cause);
    }
}