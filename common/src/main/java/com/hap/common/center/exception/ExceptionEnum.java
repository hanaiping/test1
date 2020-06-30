package com.hap.common.center.exception;

/**
 * 定义异常枚举类
 */
public enum ExceptionEnum implements ExceptionEnums {

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),
    /**
     * 未知异常
     */
    UNKOW_ERROR(500, "未知异常，请联系管理员"),
    /**
     * token无效
     */
    MANAGER_TOKEN_ERROR(556, "用户token无效"),

    /**
     * 无权限操作
     */
    NO_PERMISSION_OPTION(399, "无权限操作，请联系管理员或登录后重试！"),
    /**
     * 未登录
     */
    NO_LOGIN_OPTION(399, "请登录后重试！"),
    /**
     * 服务器异常
     */
    SERVER_EXCEPTION(529, "服务器异常"),
    /**
     * 缺少参数
     */
    MISS_REQUESTED_PARAM(501, "缺少必要请求参数"),
    /**
     * 用户不存在
     */
    USERINFO_NOT_EXIST(502, "用户不存在"),
    /**
     * 操作失败
     */
    FAIL(500, "操作成功");
    public int code;
    public String message;

    @Override
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    ExceptionEnum() {
    }

    ExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage(int code) {
        for (ExceptionEnum exceptionEnum : ExceptionEnum.values()) {
            if (exceptionEnum.getCode() == code) {
                return exceptionEnum.getMessage();
            }
        }
        return null;
    }

}
