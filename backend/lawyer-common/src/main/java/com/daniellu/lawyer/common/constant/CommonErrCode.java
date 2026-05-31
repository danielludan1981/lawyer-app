package com.daniellu.lawyer.common.constant;

/**
 * 公共错误码常量类
 */
public final class CommonErrCode {
    private CommonErrCode() {
        // 私有构造方法，防止实例化
    }

    // 系统异常错误码 (01开头)
    public static final String SYS_OTHER_ERROR = "010000"; // 其他系统异常
    public static final String SYS_NETWORK_ERROR = "010001"; // 网络异常
    public static final String SYS_FILE_ERROR = "010002"; // 文件异常
    public static final String SYS_CONFIG_ERROR = "010003"; // 配置异常
    public static final String SYS_DATABASE_ERROR = "010004"; // 数据库异常
    public static final String SYS_IO_ERROR = "010005"; // 文件异常

    // 业务异常错误码 (02开头)
    public static final String BUS_OTHER_ERROR = "020000"; // 其他业务异常
    public static final String BUS_PARAMETER_ERROR = "020001"; // 请求参数异常
    public static final String BUS_VERSION_CONFLICT_ERROR = "020002"; // 更新资源版本冲突异常
    public static final String BUS_REQUEST_LIMIT_ERROR = "020003"; // 请求超限异常
    public static final String BUS_REQUEST_TIMEOUT_ERROR = "020004"; // 请求超时异常
    public static final String BUS_RESOURCE_NOT_FOUND_ERROR = "020005"; // 请求资源不存在异常,如请求的URL地址不存在
    public static final String BUS_AUTHENTICATION_ERROR = "020006"; // 身份验证失败异常
    public static final String BUS_AUTHORIZATION_ERROR = "020007"; // 无授权异常
    public static final String BUS_DATA_NOT_FOUND_ERROR = "020008"; // 请求数据不存在异常, 如查询数据库无结果
    public static final String BUS_DUPLICATE_DATA_ERROR = "020009"; // 数据重复异常
    public static final String BUS_PARTIAL_SUCCESS_ERROR = "020010"; // 部分成功异常
}
