package com.lee.ipc.common.util;

import com.lee.ipc.common.constant.HardCoding;
import com.lee.ipc.common.serialization.common.SerializerType;

/**
 * IPC服务名生成工具类
 * @author yanhuai lee
 */
public class IpcServerNameGenerationsUtils {

    public static String generalServiceUniqueKey(String version, String[] tags, Class<?> serviceInterface, SerializerType serializerType){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(serviceInterface.getName()).append(HardCoding.SERVICE_NAME_SPLIT);
        stringBuilder.append(version).append(HardCoding.SERVICE_NAME_SPLIT);
        if (tags != null) {
            for (String tag : tags) {
                stringBuilder.append(tag).append(HardCoding.SERVICE_NAME_SPLIT);
            }
        }
        stringBuilder.append(serializerType.name());

        return stringBuilder.toString();
    }

}
