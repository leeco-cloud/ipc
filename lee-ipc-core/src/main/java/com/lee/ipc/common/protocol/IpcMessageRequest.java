package com.lee.ipc.common.protocol;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * IPC消息体
 * @author yanhuai lee
 */
@Getter
@Setter
public class IpcMessageRequest implements Serializable {

    private final String methodName;
    private final Object[] args;

    public IpcMessageRequest(String methodName, Object[] args) {
        this.methodName = methodName;
        this.args = args;
    }
}
