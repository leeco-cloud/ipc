package com.lee.ipc.common.cache;

import com.lee.ipc.common.communication.server.ServiceBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceCache {

    public static Map<String, ServiceBean> serviceCacheMap = new ConcurrentHashMap<>();

}
