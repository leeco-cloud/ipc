package com.lee.ipc.serialization;

/**
 * 序列化统一抽象
 * @author yanhuai lee
 */
public interface Serializer {

    byte[] serialize(Object obj) throws Exception;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception;

}
