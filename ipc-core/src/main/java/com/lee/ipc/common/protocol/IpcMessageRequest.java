package com.lee.ipc.common.protocol;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IPC消息体
 * @author yanhuai lee
 */
@Getter
@Setter
public class IpcMessageRequest implements Serializable {

    private String serviceUniqueKey;
    private Class<?> interfaceClass;
    private String methodName;
    private List<Type> parameterTypes;
    private Object[] args;
    private Map<String, Object> userData = new ConcurrentHashMap<>();
//    private Map<String, Long> monitorData = new ConcurrentHashMap<>();

    public IpcMessageRequest(String serviceUniqueKey, Class<?> interfaceClass, String methodName, List<Type> parameterTypes, Object[] args) {
        this.serviceUniqueKey = serviceUniqueKey;
        this.interfaceClass = interfaceClass;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }

}
