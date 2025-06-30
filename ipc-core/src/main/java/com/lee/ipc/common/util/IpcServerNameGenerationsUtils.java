package com.lee.ipc.common.util;

import com.lee.ipc.common.constant.HardCoding;
import com.lee.ipc.common.serialization.common.SerializerType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

/**
 * IPC服务名生成工具类
 * @author yanhuai lee
 */
public class IpcServerNameGenerationsUtils {

    public static String generalServiceUniqueKey(String version, String[] tags, Class<?> serviceInterface, SerializerType serializerType, Environment environment){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(serviceInterface.getName()).append(HardCoding.SERVICE_NAME_SPLIT);
        stringBuilder.append(version).append(HardCoding.SERVICE_NAME_SPLIT);
        if (tags != null) {
            for (String tag : tags) {
                // 解析是否是环境变量
                if (StringUtils.isBlank(tag)) {
                    continue;
                }
                if (tag.startsWith(HardCoding.PLACEHOLDER_LEFT) && tag.endsWith(HardCoding.PLACEHOLDER_RIGHT) && tag.length() >= 3) {
                    tag = tag.substring(2, tag.length() - 1);
                    tag = environment.getProperty(tag);
                }
                stringBuilder.append(tag).append(HardCoding.SERVICE_NAME_SPLIT);
            }
        }
        stringBuilder.append(serializerType.name());

        return stringBuilder.toString();
    }

}
