package com.lee.ipc.common.spi;

import com.lee.ipc.common.spi.container.CommonLifeSpiImpl;
import com.lee.ipc.common.spi.container.ContainerLifeSpi;
import com.lee.ipc.common.spi.invoke.CommonIpcInvokeSpi;
import com.lee.ipc.common.spi.invoke.IpcInvokeSpi;
import com.lee.ipc.common.spi.monitor.CommonIpcMonitorSpi;
import com.lee.ipc.common.spi.monitor.IpcMonitorSpi;

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

    private static final List<IpcMonitorSpi> ipcMonitorSpi = new CopyOnWriteArrayList<>();

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

        // 加载监控上报扩展
        ipcMonitorSpi.add(new CommonIpcMonitorSpi());
        ServiceLoader<IpcMonitorSpi> ipcMonitorSpiServiceLoader = ServiceLoader.load(IpcMonitorSpi.class);
        while (ipcMonitorSpiServiceLoader.iterator().hasNext()) {
            ipcMonitorSpi.add(ipcMonitorSpiServiceLoader.iterator().next());
        }
    }

    public static ContainerLifeSpi loadContainerLifeSpi() {
        return containerLifeSpi;
    }

    public static List<IpcInvokeSpi> loadIpcInvokeSpi() {
        return ipcInvokeSpi;
    }

    public static List<IpcMonitorSpi> loadIpcMonitorSpi() {
        return ipcMonitorSpi;
    }

}
