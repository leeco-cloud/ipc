package com.lee.ipc.common.communication.support;

import java.util.Map;

/**
 * 用户数据工具类
 * @author yanhuai lee
 */
public class UserDataSupport {

    public static void putUserData(String key, Object value) {
        ThreadLocalContent.putUserData(key, value);
    }

    public static void putAllUserData(Map<String, Object> userData) {
        ThreadLocalContent.putAllUserData(userData);
    }

    public static Map<String, Object> getAllUserData() {
        return ThreadLocalContent.getAllUserData();
    }

}