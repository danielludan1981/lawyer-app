package com.daniellu.lawyer.common.exception;

/**
 * 基础抽象异常类
 * 所有自定义异常的基类，继承自RuntimeException
 */
public abstract class AbstractBaseException extends RuntimeException {
    private String errorCode;
    private String message;
    private Object details;

    public AbstractBaseException() {
        super();
    }

    public AbstractBaseException(String message) {
        super(message);
        this.message = message;
    }

    public AbstractBaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public AbstractBaseException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public AbstractBaseException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
    }

    public AbstractBaseException(String errorCode, String message, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
    }

    public AbstractBaseException(String errorCode, String message, Object details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }
}