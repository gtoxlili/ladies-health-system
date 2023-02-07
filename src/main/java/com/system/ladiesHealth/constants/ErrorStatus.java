package com.system.ladiesHealth.constants;


public enum ErrorStatus {

    SUCCESS(200, "成功"),

    // Security 相关问题
    ACCOUNT_EXPIRED(301, "帐户已过期"),
    ACCESS_DENIED(302, "访问被拒绝"),
    AUTHENTICATION_ERROR(300, "鉴权问题"),

    // Servlet 相关问题
    NO_HANDLER_FOUND(404, "访问不存在的资源"),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED(405, "请求方式不支持"),
    // JSR-303 参数校验异常
    CONSTRAINT_VIOLATION(400, "参数校验异常"),
    // 所请求的 DML 操作导致违反定义的完整性约束
    DATA_INTEGRITY_VIOLATION(400, "数据完整性约束违反"),
    // 绑定到目标对象失败时抛出的异常
    BIND_EXCEPTION(400, "绑定异常"),

    // 通用异常
    // 未知的系统异常
    UNKNOWN_SYSTEM_ERROR(501, "未知的系统异常"),
    // 业务异常
    BUSINESS_EXCEPTION(500, "业务异常");


    private final Integer statusCode;
    private final String defaultMessage;


    ErrorStatus(int statusCode, String defaultMessage) {
        this.statusCode = statusCode;
        this.defaultMessage = defaultMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
