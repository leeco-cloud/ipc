package com.lee.ipc.common.api;

import com.lee.ipc.common.serialization.common.SerializerType;
import com.lee.ipc.common.server.ProviderAnnotationBeanPostProcessor;

/**
 * 服务端API
 * @author yanhuai lee
 */
public class ServerApi {

    /**
     * 判断服务端是否ready 可以接受注册
     */
    public static Boolean canRegister(){
        return ProviderAnnotationBeanPostProcessor.canRegister.get();
    }

    /**
     * 注册IPC服务
     */
    public static void registerServer(Class<?> serviceInterface, Object instance, String version, SerializerType serializerType) {
        ProviderAnnotationBeanPostProcessor.registerServer(version,null, serviceInterface, serializerType, instance.getClass().getName(), instance);
    }

}
