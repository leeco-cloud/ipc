package com.lee.ipc.common.monitor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yanhuai lee
 */
public class MonitorMeta<K, V> extends LinkedHashMap<K, V> {

    private final int maxSize;

    public MonitorMeta(int maxSize) {
        // 设置访问顺序为false（基于插入顺序），初始容量16，负载因子0.75f
        super(16, 0.75f, false);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // 当元素数量超过最大容量时移除最旧条目
        return size() > maxSize;
    }

    @Override
    public V put(K key, V value) {
        // 若Key已存在，先删除旧值再插入（保证更新时移动到最新位置）
        if (containsKey(key)) {
            remove(key);
        }
        return super.put(key, value);
    }
}
