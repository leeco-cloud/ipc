package com.lee.ipc.common.server;

import com.lee.ipc.common.communication.server.ServiceBean;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.env.Environment;

/**
 * 服务发布事件
 * @author yanhuai lee
 */
@Getter
public class ServiceBeanExportEvent extends ApplicationEvent {

    public ServiceBean serviceBean;

    public Environment environment;

    public ServiceBeanExportEvent(Object source, Environment environment) {
        super(source);
        this.serviceBean = (ServiceBean) source;
        this.environment = environment;
    }

}
