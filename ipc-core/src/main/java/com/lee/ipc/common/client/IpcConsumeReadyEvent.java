package com.lee.ipc.common.client;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 服务订阅事件
 * @author yanhuai lee
 */
@Getter
public class IpcConsumeReadyEvent extends ApplicationEvent {

    public ReferenceBean referenceBean;

    public IpcConsumeReadyEvent(Object source) {
        super(source);
        this.referenceBean = (ReferenceBean) source;
    }

}
