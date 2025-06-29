package com.lee.ipc.common.client;

import com.lee.ipc.common.cache.ReferenceCache;
import com.lee.ipc.common.communication.client.IpcClient;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class IpcConsumeReadyListener implements ApplicationListener<IpcConsumeReadyEvent> {

    private final AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(IpcConsumeReadyEvent event) {
        ReferenceBean referenceBean = event.getReferenceBean();

        ReferenceCache.referenceCacheMap.put(referenceBean.getServiceUniqueKey(), referenceBean);

        if (started.compareAndSet(false, true)) {

            // todo 启动定时任务定时从注册中心(共享目录)拉取服务列表

            // 启动客户端 netty 链接服务端
            try {
                IpcClient ipcClient = new IpcClient();
                ipcClient.init();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // todo 启动 FileListener 文件监听，实时监听注册中心服务状态变化
        }
    }

}
