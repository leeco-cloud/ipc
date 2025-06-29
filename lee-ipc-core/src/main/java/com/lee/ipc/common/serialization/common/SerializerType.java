package com.lee.ipc.common.serialization.common;

import lombok.Getter;

/**
 * 序列化类型枚举
 * @author yanhuai lee
 */
@Getter
public enum SerializerType {

    PROTOSTUFF(1),

    JSON(2),

    FURY(3);

    public final int type;

    SerializerType(int type) {
        this.type = type;
    }

    public static SerializerType getSerializerType(int type){
        for (SerializerType value : SerializerType.values()) {
            if (value.type == type) {
                return value;
            }
        }
        return SerializerType.FURY;
    }

}
