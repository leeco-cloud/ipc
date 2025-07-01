package com.lee.ipc.common.register;

import com.alibaba.fastjson.JSON;
import com.lee.ipc.common.AutoConfiguration;
import com.lee.ipc.common.communication.client.IpcClient;
import com.lee.ipc.common.communication.server.ServiceBean;
import com.lee.ipc.common.exception.ErrorCode;
import com.lee.ipc.common.log.RuntimeLogger;
import com.lee.ipc.common.util.FileUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 本地化注册中心
 * @author yanhuai lee
 */
public class RegistryLocalCenter {

    private static volatile RegistryLocalCenter INSTANCE = new RegistryLocalCenter();

    public static AtomicBoolean running = new AtomicBoolean(false);

    private static final ReentrantLock refreshLock = new ReentrantLock();
    private static final ScheduledExecutorService refreshScheduler = Executors.newScheduledThreadPool(2);

    private static final ReentrantLock reconnectLock = new ReentrantLock();
    private static final ScheduledExecutorService reconnectScheduler = Executors.newScheduledThreadPool(2);

    private static FileAlterationMonitor monitor;

    public static String registerPath;
    public static String registerContainerPath;

    public static final Map<String, List<ServiceBean>> containerServiceMap = new ConcurrentHashMap<>();
    public static final Map<String, List<ServiceBean>> serviceUniqueKeyServiceMap = new ConcurrentHashMap<>();

    public static RegistryLocalCenter getInstance() {
        if (null == INSTANCE) {
            synchronized (RegistryLocalCenter.class) {
                if (null == INSTANCE) {
                    INSTANCE = new RegistryLocalCenter();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Environment environment) throws Exception {
        registerPath = AutoConfiguration.getLocalRegisterCenterPath(environment);
        registerContainerPath = AutoConfiguration.getLocalRegisterCenterPath(environment) + "/" + AutoConfiguration.getContainerName(environment);

        FileUtils.deleteDirectoryRecursively(registerContainerPath);
        FileUtils.createDirectories(registerContainerPath);

        Runtime.getRuntime().addShutdownHook(new Thread(()->stopServer(registerContainerPath)));

        fullScanDirectory();
        reconnectAllServer();

        // 启动定时任务 定时刷新服务集合
        refreshScheduler.scheduleAtFixedRate(this::fullScanDirectory, 10, 10, TimeUnit.SECONDS);
        // 启动定时任务 定时检查连接是否正常
        reconnectScheduler.scheduleAtFixedRate(this::reconnectAllServer, 10, 10, TimeUnit.SECONDS);

        // 启动文件监听
        startWatching();

        running.set(true);
    }

    public void fullScanDirectory() {
        refreshLock.lock();
        try{
            List<String> serviceFilePaths = FileUtils.listAllFilesJson(registerPath);
            if (serviceFilePaths != null) {
                for (String serviceFilePath : serviceFilePaths) {
                    String serviceBeanStr = FileUtils.readFileToString(serviceFilePath, StandardCharsets.UTF_8);
                    ServiceBean serviceBean;
                    try{
                        serviceBean = JSON.parseObject(serviceBeanStr, ServiceBean.class);
                    }catch (Exception e){
                        continue;
                    }
                    if (serviceBean == null || StringUtils.isBlank(serviceBean.getContainerName())){
                        continue;
                    }
                    List<ServiceBean> serviceBeans = containerServiceMap.getOrDefault(serviceBean.getContainerName(), new CopyOnWriteArrayList<>());
                    serviceBeans.add(serviceBean);
                    containerServiceMap.put(serviceBean.getContainerName(), serviceBeans);

                    List<ServiceBean> serviceUniqueKeyServiceBean = serviceUniqueKeyServiceMap.getOrDefault(serviceBean.getServiceUniqueKey(), new CopyOnWriteArrayList<>());
                    serviceUniqueKeyServiceBean.add(serviceBean);
                    serviceUniqueKeyServiceMap.put(serviceBean.getServiceUniqueKey(), serviceUniqueKeyServiceBean);
                }
                reconnectAllServer();
            }
        } catch (Exception e) {
            RuntimeLogger.error(ErrorCode.REGISTER_REFRESH_CENTER_ERROR.getMessage(), e);
        } finally {
            refreshLock.unlock();
        }
    }

    private void reconnectAllServer(){
        reconnectLock.lock();
        try{
            for (String containerName : containerServiceMap.keySet()) {
                IpcClient ipcClient = IpcClient.allClients.get(containerName);
                if (ipcClient != null) {
                    continue;
                }
                ipcClient = new IpcClient();
                ipcClient.init(containerName);
                IpcClient.allClients.put(containerName, ipcClient);
            }
        } catch (Exception e) {
            RuntimeLogger.error(ErrorCode.REGISTER_REFRESH_CENTER_ERROR.getMessage(), e);
        } finally {
            reconnectLock.unlock();
        }
    }

    public void registerService(ServiceBean serviceBean) {
        refreshLock.lock();
        try{
            String servicePath = registerContainerPath + "/" + serviceBean.getServiceUniqueKey() + ".json";
            FileUtils.writeStringToFile(servicePath, JSON.toJSONString(serviceBean), StandardCharsets.UTF_8, false);
            List<ServiceBean> serviceBeans = containerServiceMap.getOrDefault(serviceBean.getContainerName(), new CopyOnWriteArrayList<>());
            serviceBeans.add(serviceBean);
            containerServiceMap.put(serviceBean.getContainerName(), serviceBeans);

            List<ServiceBean> serviceUniqueKeyServiceBean = serviceUniqueKeyServiceMap.getOrDefault(serviceBean.getServiceUniqueKey(), new CopyOnWriteArrayList<>());
            serviceUniqueKeyServiceBean.add(serviceBean);
            serviceUniqueKeyServiceMap.put(serviceBean.getServiceUniqueKey(), serviceUniqueKeyServiceBean);
        } catch (IOException e) {
            System.err.println("Error scanning directory: " + e.getMessage());
        } finally {
            refreshLock.unlock();
            fullScanDirectory();
        }
    }

    public static void removeService() {
        stopServer(registerContainerPath);
    }

    private static void stopServer(String registerPath) {
        refreshScheduler.shutdownNow();
        reconnectScheduler.shutdownNow();
        try {
            FileUtils.deleteDirectoryRecursively(registerPath);
            monitor.stop();
        } catch (Exception e) {
            // ignore
        }
    }

    private void startWatching() throws Exception {
        FileAlterationObserver observer = new FileAlterationObserver(registerPath);
        observer.addListener(new FileMonitor());
        monitor = new FileAlterationMonitor(10000, observer); // 5秒轮询
        monitor.start();
    }

}
