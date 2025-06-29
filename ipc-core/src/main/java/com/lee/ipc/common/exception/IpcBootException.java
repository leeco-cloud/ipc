package com.lee.ipc.common.exception;

import lombok.Getter;

@Getter
public class IpcBootException extends RuntimeException {

    private final ErrorCode errorCode;

    public IpcBootException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
