package com.lee.ipc.common.register;

import com.alibaba.fastjson.JSON;
import com.lee.ipc.common.AutoConfiguration;
import com.lee.ipc.common.communication.server.ServiceBean;
import com.lee.ipc.common.exception.ErrorCode;
import com.lee.ipc.common.log.RuntimeLogger;
import com.lee.ipc.common.util.FileUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
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

    private static final ReentrantLock updateLock = new ReentrantLock();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private static FileAlterationMonitor monitor;

    public static String registerPath;

    private static final Map<String, List<ServiceBean>> serviceMap = new ConcurrentHashMap<>();

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
        registerPath = AutoConfiguration.getLocalRegisterCenterPath(environment) + "/" + AutoConfiguration.getContainerName(environment);

        FileUtils.deleteDirectoryRecursively(registerPath);
        FileUtils.createDirectories(registerPath);

        Runtime.getRuntime().addShutdownHook(new Thread(()->stopServer(registerPath)));

        // 启动定时任务 定时刷新服务集合
        scheduler.scheduleAtFixedRate(this::fullScanDirectory, 0, 10, TimeUnit.SECONDS);

        // 启动文件监听
        startWatching();
    }

    public void fullScanDirectory() {
        updateLock.lock();
        try{
            List<String> serviceFilePaths = FileUtils.listAllFilesRecursively(registerPath);
            if (serviceFilePaths != null) {
                for (String serviceFilePath : serviceFilePaths) {
                    String serviceBeanStr = FileUtils.readFileToString(serviceFilePath, StandardCharsets.UTF_8);
                    ServiceBean serviceBean = JSON.parseObject(serviceBeanStr, ServiceBean.class);
                    List<ServiceBean> serviceBeans = serviceMap.getOrDefault(serviceBean.getContainerName(), new CopyOnWriteArrayList<>());
                    serviceBeans.add(serviceBean);
                    serviceMap.put(serviceBean.getContainerName(), serviceBeans);
                }
            }
        } catch (IOException e) {
            RuntimeLogger.error(ErrorCode.REGISTER_REFRESH_CENTER_ERROR);
        } finally {
            updateLock.unlock();
        }
    }

    public void registerService(ServiceBean serviceBean) {
        updateLock.lock();
        try{
            String servicePath = registerPath + "/" + serviceBean.getServiceUniqueKey();
            FileUtils.writeStringToFile(servicePath, JSON.toJSONString(serviceBean), StandardCharsets.UTF_8, false);
            List<ServiceBean> serviceBeans = serviceMap.getOrDefault(serviceBean.getContainerName(), new CopyOnWriteArrayList<>());
            serviceBeans.add(serviceBean);
            serviceMap.put(serviceBean.getContainerName(), serviceBeans);
        } catch (IOException e) {
            System.err.println("Error scanning directory: " + e.getMessage());
        } finally {
            updateLock.unlock();
        }
    }

    public static void removeService() {
        stopServer(registerPath);
    }

    private static void stopServer(String registerPath) {
        scheduler.shutdownNow();
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
