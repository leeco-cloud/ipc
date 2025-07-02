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

    public Integer getErrorCode() {
        if (errorCode == null || errorCode == 0) {
            return null;
        }
        return errorCode;
    }

    //    private Map<String, Long> monitorData = new ConcurrentHashMap<>();

}
