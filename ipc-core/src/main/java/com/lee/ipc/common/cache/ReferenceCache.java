package com.lee.ipc.common.cache;

import com.lee.ipc.common.client.ReferenceBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceCache {

    public static Map<String, ReferenceBean> referenceCacheMap = new ConcurrentHashMap<>();

    public static Map<String, Object> referenceProxyCacheMap = new ConcurrentHashMap<>();

}
