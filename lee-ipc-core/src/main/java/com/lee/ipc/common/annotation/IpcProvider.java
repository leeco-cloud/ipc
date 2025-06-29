package com.lee.ipc.common.annotation;

import com.lee.ipc.common.serialization.common.SerializerType;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * IPC服务提供者
 * @author yanhuai lee
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Component
public @interface IpcProvider {

    /**
     * version（非必填）:版本号
     */
    String version() default "1.0.0";

    /**
     * tag（非必填）: 自定义标签，可以是字符串，可以是环境变量
     */
    String[] tags() default {};

    /**
     * serviceInterface（非必填）:服务接口，当父层超过2个，需要指定serviceInterface
     */
    Class<?> serviceInterface() default Object.class;

    /**
     * serializerType（非必填）: 序列化类型，默认FURY
     */
    SerializerType serializerType() default SerializerType.FURY;

}
