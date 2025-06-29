package com.lee.ipc.common.serialization.protobuf;

import com.alibaba.fastjson.JSON;
import com.lee.ipc.common.serialization.Serializer;

/**
 * protobuf序列化
 * @author yanhuai lee
 */
public class ProtobufSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }

}
