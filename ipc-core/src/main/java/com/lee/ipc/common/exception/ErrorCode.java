package com.lee.ipc.common.exception;

import lombok.Getter;
import org.slf4j.helpers.MessageFormatter;

@Getter
public enum ErrorCode {

    // 通用错误 1000-1999

    // 启动错误 2000-2999
    BOOT_TOO_MUCH_SERVICE_INTERFACE(2001, "服务提供者存在多个接口定义,请声明具体接口: {}"),
    BOOT_TOO_MUCH_SERVICE_PROVIDER(2002, "服务提供者存在多个相同定义的实现,请检查: {}"),

    // 客户端错误 3000-3999
    REQUEST_SERIALIZER_ERROR(3001, "客户端序列化失败"),
    REQUEST_TIME_OUT_ERROR(3002, "客户端请求超时: {} - {}"),

    // 服务端错误 4000-4999
    REQUEST_DESERIALIZE_ERROR(4001, "客户端反序列化失败"),

    // 注册中心错误 5000-5999
    REGISTER_REFRESH_CENTER_ERROR(5001, "刷新注册中心异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String format() {
        return "[" + code + "]" + message;
    }

    public String format(Object... args) {
        return "[" + code + "]" + MessageFormatter.arrayFormat(message, args).getMessage();
    }

}
