package com.lee.ipc.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 启动日志文件管理器
 * @author yanhuai lee
 */
public class BootLogger {

    private static final Logger bootLog = LoggerFactory.getLogger("startupLogger");

    public static void info(String msg, Object... args) {
        String format = String.format(msg, args);
        bootLog.info(format);
    }

    public static void error(String msg) {
        bootLog.error(msg);
    }

    public static void error(String msg, Throwable throwable) {
        bootLog.error(msg, throwable);
    }

}
