package com.lee.ipc.common.exception;

import com.lee.ipc.common.log.BootLogger;
import lombok.Getter;

/**
 * 启动时异常
 * @author yanhuai lee
 */
@Getter
public class IpcBootException extends RuntimeException {

    private final Integer errorCode;
    private final String errorMsg;

    public IpcBootException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorCode.getMessage();
        BootLogger.error(errorCode.format());
    }

    public IpcBootException(ErrorCode errorCode, Object... args) {
        super(errorCode.format(args));
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorCode.format(args);
        BootLogger.error(errorCode.format(args));
    }

    public IpcBootException(Throwable throwable, ErrorCode errorCode) {
        super(errorCode.getMessage(), throwable);
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorCode.getMessage();
        BootLogger.error(errorCode.format(), throwable);
    }

    public IpcBootException(Throwable throwable, ErrorCode errorCode, Object... args) {
        super(errorCode.format(args), throwable);
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorCode.format(args);
        BootLogger.error(errorCode.format(args), throwable);
    }
}
