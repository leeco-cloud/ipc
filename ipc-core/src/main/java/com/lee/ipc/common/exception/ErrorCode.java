package com.lee.ipc.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 通用错误 1000-1999
    PARAM_INVALID(1001, "参数校验失败"),
    RESOURCE_NOT_FOUND(1002, "资源不存在"),

    // 启动错误 2000-2999
    BOOT_TOO_MUCH_SERVICE_INTERFACE(2001, "服务提供者存在多个接口定义,请声明具体接口: serviceInterface"),
    BOOT_TOO_MUCH_SERVICE_PROVIDER(2002, "服务提供者存在多个相同定义的实现,请检查: serviceInterface + {0}"),

    // 客户端调用错误 3000-3999
    REQUEST_SERIALIZER_ERROR(3001, "客户端序列化失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
