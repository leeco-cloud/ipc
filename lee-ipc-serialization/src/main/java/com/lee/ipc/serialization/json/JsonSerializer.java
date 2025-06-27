package com.lee.ipc.serialization.json;

import com.alibaba.fastjson.JSON;
import com.lee.ipc.serialization.Serializer;

/**
 * Json序列化
 * @author yanhuai lee
 */
public class JsonSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }

}
