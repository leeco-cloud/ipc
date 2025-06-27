package com.lee.ipc.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * IPC消息协议
 * @author yanhuai lee
 */
@Getter
@Setter
public class IpcMessage implements Serializable {

    private long requestId;
    private int serializerType;
    private byte[] content;

}
