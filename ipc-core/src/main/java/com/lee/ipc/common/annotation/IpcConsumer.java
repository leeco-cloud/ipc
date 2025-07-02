package com.lee.ipc.common.annotation;

import com.lee.ipc.common.serialization.common.SerializerType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * IPC服务订阅者
 * @author yanhuai lee
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface IpcConsumer {

    /**
     * version（非必填）
     * 版本号
     * 默认 1.0.0
     */
    String version() default "1.0.0";

    /**
     * tag（非必填）
     * 自定义标签，可以是字符串：XXX，可以是环境变量 例如${XXX}
     */
    String[] tags() default {};

    /**
     * timeout（非必填）
     * 指定请求超时时间，单位:毫秒
     * 默认5秒(5000毫秒)
     */
    int timeout() default 5000;

    /**
     * serializerType（非必填）
     * 序列化类型
     * 默认FURY
     */
    SerializerType serializerType() default SerializerType.FURY;

}
