package com.lee.ipc.common.server;

import com.lee.ipc.common.serialization.common.SerializerType;

/**
 * 服务元数据管理
 * @author yanhuai lee
 */
public class ServiceManager {

    /**
     * bean（必填）: Bean对象
     * injectedType(非必填): 服务接口, 但是当接口>=2个的时候必填
     * version（非必填）:版本号， 默认1.0.0
     * serializationType : 序列化类型
     */
    public static void registerService(Object bean, Class<?> injectedType, String version, SerializerType serializerType){

    }

}
