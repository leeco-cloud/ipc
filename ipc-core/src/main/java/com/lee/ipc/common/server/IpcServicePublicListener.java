package com.lee.ipc.common.server;

import com.lee.ipc.common.cache.ServiceCache;
import com.lee.ipc.common.communication.server.IpcServer;
import com.lee.ipc.common.communication.server.ServiceBean;
import com.lee.ipc.common.register.FileRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class IpcServicePublicListener implements ApplicationListener<ServiceBeanExportEvent> {

    private final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(ServiceBeanExportEvent event) {
        ServiceBean serviceBean = event.getServiceBean();

        if (started.compareAndSet(false, true)) {
            try {
                IpcServer ipcServer = new IpcServer();
                ipcServer.init();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // todo 注册服务到注册中心
        Boolean registerService = FileRegistry.registry.registerService(serviceBean.getServiceUniqueKey());
        if (registerService) {
            ServiceCache.serviceCacheMap.put(serviceBean.getServiceUniqueKey(), serviceBean);
        }
    }

}
