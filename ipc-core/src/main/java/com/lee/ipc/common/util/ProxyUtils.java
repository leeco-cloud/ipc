package com.lee.ipc.common.util;

import com.lee.ipc.common.client.ReferenceBean;

import java.lang.reflect.Proxy;

/**
 * 代理工具类
 * @author yanhuai lee
 */
public class ProxyUtils {

    public static Object getProxy(Class<?> serviceInterface, ReferenceBean referenceBean){
        return Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                referenceBean);
    }

}
