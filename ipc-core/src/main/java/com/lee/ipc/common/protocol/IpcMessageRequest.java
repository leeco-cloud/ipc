package com.lee.ipc.common.protocol;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IPC消息体
 * @author yanhuai lee
 */
@Getter
@Setter
public class IpcMessageRequest implements Serializable {

    private final String serviceUniqueKey;
    private final Class<?> interfaceClass;
    private final String methodName;
    private final Object[] args;
    private Map<String, Object> userData = new ConcurrentHashMap<>();

    public IpcMessageRequest(String serviceUniqueKey, Class<?> interfaceClass, String methodName, Object[] args) {
        this.serviceUniqueKey = serviceUniqueKey;
        this.interfaceClass = interfaceClass;
        this.methodName = methodName;
        this.args = args;
    }

}
