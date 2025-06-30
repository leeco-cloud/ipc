package com.lee.ipc.common.communication.server;

import com.lee.ipc.common.serialization.common.SerializerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 服务元数据
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBean {

    /**
     * 版本号
     */
    private String version = "1.0.0";

    /**
     * 自定义标签，可以是字符串，可以是环境变量
     */
    private String[] tags;

    /**
     * 服务接口
     */
    private Class<?> serviceInterface;

    /**
     * 序列化类型，默认FURY
     */
    private SerializerType serializerType = SerializerType.FURY;

    /**
     * IOC中的beanName
     */
    private String beanName;

    /**
     * 根据服务标签等属性生成的唯一标识
     */
    private String serviceUniqueKey;

    /**
     * 容器名
     */
    private String containerName;

}
