package com.lee.ipc.common.serialization;

import com.lee.ipc.common.serialization.common.SerializerType;
import com.lee.ipc.common.serialization.fury.FurySerializer;
import com.lee.ipc.common.serialization.json.JsonSerializer;
import com.lee.ipc.common.serialization.protobuf.ProtobufSerializer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yanhuai lee
 */
public class SerializationFactory {

    private static final ConcurrentMap<SerializerType, Serializer> SERIALIZATION_MAP = new ConcurrentHashMap<>();

    static {
        SERIALIZATION_MAP.put(SerializerType.PROTOSTUFF, new ProtobufSerializer());
        SERIALIZATION_MAP.put(SerializerType.JSON, new JsonSerializer());
        SERIALIZATION_MAP.put(SerializerType.FURY, new FurySerializer());
    }

    public static Serializer getSerializer(SerializerType serializerType){
        return SERIALIZATION_MAP.get(serializerType);
    }

}
