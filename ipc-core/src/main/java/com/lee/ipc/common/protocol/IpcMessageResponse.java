package com.lee.ipc.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * IPC消息体
 * @author yanhuai lee
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpcMessageResponse implements Serializable {

    private Object data;

    private Integer errorCode;
    private String errorMsg;

}
