package com.lee.ipc.common.client;

import com.lee.ipc.common.cache.ReferenceCache;
import com.lee.ipc.common.communication.client.IpcClient;
import com.lee.ipc.common.exception.ErrorCode;
import com.lee.ipc.common.exception.IpcBootException;
import com.lee.ipc.common.register.RegistryLocalCenter;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class IpcConsumeReadyListener implements ApplicationListener<IpcConsumeReadyEvent> {

    private final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(IpcConsumeReadyEvent event) {
        ReferenceBean referenceBean = event.getReferenceBean();

        Environment environment = event.getEnvironment();

        ReferenceCache.referenceCacheMap.put(referenceBean.getServiceUniqueKey(), referenceBean);

        if (started.compareAndSet(false, true)) {
            try {
                IpcClient ipcClient = new IpcClient();
                ipcClient.init();
                if (!RegistryLocalCenter.running.get()){
                    RegistryLocalCenter.getInstance().init(environment);
                }
            } catch (Exception e) {
                throw new IpcBootException(e, ErrorCode.BOOT_CUSTOM_ERROR);
            }
        }
    }

}
