package com.lee.ipc.serialization.common;

/**
 * 序列化类型枚举
 * @author yanhuai lee
 */
public enum SerializerType {

    PROTOSTUFF(1),

    JSON(2),

    FURY(3);

    public final int type;

    SerializerType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
