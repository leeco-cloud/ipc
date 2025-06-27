package com.lee.ipc.serialization.fury;

import com.lee.ipc.serialization.Serializer;

/**
 * fury序列化
 * @author yanhuai lee
 */
public class FurySerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        return FuryConfiguration.fory.serialize(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return FuryConfiguration.fory.deserialize(bytes, clazz);
    }

}
