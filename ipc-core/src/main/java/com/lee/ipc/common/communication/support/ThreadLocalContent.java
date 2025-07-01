package com.lee.ipc.common.communication.support;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yanhuai lee
 */
public class ThreadLocalContent {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    /**
     * 存储数据到当前线程的ThreadLocal
     * @param key 数据键
     * @param value 数据值
     */
    public static void putUserData(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    /**
     * 存储数据到当前线程的ThreadLocal
     * @param data 全量数据
     */
    public static void putAllUserData(Map<String, Object> data) {
        CONTEXT.get().putAll(data);
    }

    /**
     * 从当前线程的ThreadLocal获取数据
     * @param key 数据键
     * @return 数据值（不存在时返回null）
     */
    public static Object getUserData(String key) {
        return CONTEXT.get().get(key);
    }

    /**
     * 从当前线程的ThreadLocal获取数据
     * @return 数据集合
     */
    public static Map<String ,Object> getAllUserData() {
        return CONTEXT.get();
    }

    /**
     * 获取数据并转换为特定类型
     * @param key 数据键
     * @param clazz 目标类型Class对象
     * @return 类型转换后的数据（转换失败时返回null）
     */
    public static <T> T getUserData(String key, Class<T> clazz) {
        Object obj = getUserData(key);
        return clazz.isInstance(obj) ? clazz.cast(obj) : null;
    }

    /**
     * 移除指定键值对
     * @param key 要移除的数据键
     */
    public static void removeUserData(String key) {
        CONTEXT.get().remove(key);
    }

    /**
     * 清理当前线程的所有ThreadLocal数据（防止内存泄漏）
     */
    public static void clear() {
        CONTEXT.remove();
    }

}
