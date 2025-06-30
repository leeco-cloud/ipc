package com.lee.ipc.common.server;

import com.lee.ipc.common.AutoConfiguration;
import com.lee.ipc.common.communication.server.IpcServer;
import com.lee.ipc.common.communication.server.ServiceBean;
import com.lee.ipc.common.register.RegistryLocalCenter;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class IpcServicePublicListener implements ApplicationListener<ServiceBeanExportEvent> {

    private final AtomicBoolean started = new AtomicBoolean(false);

    private RegistryLocalCenter registryLocalCenter;

    @Override
    public void onApplicationEvent(ServiceBeanExportEvent event) {
        ServiceBean serviceBean = event.getServiceBean();

        Environment environment = event.getEnvironment();

        if (started.compareAndSet(false, true)) {
            try {
                IpcServer ipcServer = new IpcServer();
                ipcServer.init(serviceBean.getContainerName());
                if (!RegistryLocalCenter.running.get()){
                    registryLocalCenter = new RegistryLocalCenter(environment);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 注册服务到注册中心
        registryLocalCenter.registerService(serviceBean);
    }

}
