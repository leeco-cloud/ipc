package com.lee.ipc.common.client;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.env.Environment;

/**
 * 服务订阅事件
 * @author yanhuai lee
 */
@Getter
public class IpcConsumeReadyEvent extends ApplicationEvent {

    public ReferenceBean referenceBean;

    public Environment environment;

    public IpcConsumeReadyEvent(Object source, Environment environment) {
        super(source);
        this.referenceBean = (ReferenceBean) source;
        this.environment = environment;
    }

}
