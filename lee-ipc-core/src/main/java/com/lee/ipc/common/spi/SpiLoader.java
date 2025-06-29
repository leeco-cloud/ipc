package com.lee.ipc.common.spi;

import com.lee.ipc.common.spi.container.CommonLifeSpiImpl;
import com.lee.ipc.common.spi.container.ContainerLifeSpi;
import com.lee.ipc.common.spi.invoke.CommonIpcInvokeSpi;
import com.lee.ipc.common.spi.invoke.IpcInvokeSpi;

import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * spi加载器
 * @author yanhuai lee
 */
public class SpiLoader {

    private static final List<IpcInvokeSpi> ipcInvokeSpi = new CopyOnWriteArrayList<>();

    private static ContainerLifeSpi containerLifeSpi = new CommonLifeSpiImpl();

    static{
        // 加载请求拦截器扩展
        ipcInvokeSpi.add(new CommonIpcInvokeSpi());
        ServiceLoader<IpcInvokeSpi> ipcInvokeSpiServiceLoader = ServiceLoader.load(IpcInvokeSpi.class);
        while (ipcInvokeSpiServiceLoader.iterator().hasNext()) {
            ipcInvokeSpi.add(ipcInvokeSpiServiceLoader.iterator().next());
        }

        // 加载容器生命周期扩展
        ServiceLoader<ContainerLifeSpi> containerLifeSpiServiceLoader = ServiceLoader.load(ContainerLifeSpi.class);
        if (containerLifeSpiServiceLoader.iterator().hasNext()) {
            containerLifeSpi = containerLifeSpiServiceLoader.iterator().next();
        }
    }

    private static ContainerLifeSpi loadContainerLifeSpi() {
        return containerLifeSpi;
    }

    private static List<IpcInvokeSpi> loadIpcInvokeSpi() {
        return ipcInvokeSpi;
    }

}
