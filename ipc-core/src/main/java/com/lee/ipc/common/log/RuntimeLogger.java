package com.lee.ipc.common.log;

import com.lee.ipc.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 运行时日志
 * @author yanhuai lee
 */
public class RuntimeLogger {

    private static final Logger runtimeLog = LoggerFactory.getLogger("runtimeLogger");

    public static void info(String msg, Object... args) {
        String format = String.format(msg, args);
        runtimeLog.info(format);
    }

    public static void error(String msg, Object... args) {
        String format = String.format(msg, args);
        runtimeLog.error(format);
    }

    public static void error(String msg, Throwable throwable) {
        runtimeLog.error(msg, throwable);
    }

    public static void error(ErrorCode errorCode) {
        String format = errorCode.format();
        runtimeLog.error(format);
    }

}
