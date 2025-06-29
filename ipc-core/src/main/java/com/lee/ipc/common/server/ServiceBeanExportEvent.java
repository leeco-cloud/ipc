package com.lee.ipc.common.server;

import com.lee.ipc.common.communication.server.ServiceBean;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 服务发布事件
 * @author yanhuai lee
 */
@Getter
public class ServiceBeanExportEvent extends ApplicationEvent {

    public ServiceBean serviceBean;

    public ServiceBeanExportEvent(Object source) {
        super(source);
        this.serviceBean = (ServiceBean) source;
    }

}
