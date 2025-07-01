package com.lee.ipc.common.exception;

import com.lee.ipc.common.log.RuntimeLogger;
import lombok.Getter;

/**
 * 运行时异常
 * @author yanhuai lee
 */
@Getter
public class IpcRuntimeException extends RuntimeException {

    private final Integer errorCode;
    private final String errorMsg;

    public IpcRuntimeException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMsg = message;
        RuntimeLogger.error("[" + errorCode + "]" + errorMsg);
    }

    public IpcRuntimeException(Throwable throwable) {
        super(throwable);
        this.errorCode = null;
        this.errorMsg = null;
        RuntimeLogger.error(throwable.getMessage(), throwable);
    }

    public IpcRuntimeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorCode.getMessage();
        RuntimeLogger.error(errorCode.format());
    }

    public IpcRuntimeException(ErrorCode errorCode, Object... args) {
        super(errorCode.format(args));
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorCode.format(args);
        RuntimeLogger.error(errorCode.format(args));
    }

    public IpcRuntimeException(Throwable throwable, ErrorCode errorCode) {
        super(errorCode.getMessage(), throwable);
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorCode.getMessage();
        RuntimeLogger.error(errorCode.format(), throwable);
    }

    public IpcRuntimeException(Throwable throwable, ErrorCode errorCode, Object... args) {
        super(errorCode.format(args), throwable);
        this.errorCode = errorCode.getCode();
        this.errorMsg = errorCode.format(args);
        RuntimeLogger.error(errorCode.format(args), throwable);
    }

}
