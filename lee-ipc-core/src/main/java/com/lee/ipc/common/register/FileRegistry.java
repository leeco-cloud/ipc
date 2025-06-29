package com.lee.ipc.common.register;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地化注册中心
 * @author yanhuai lee
 */
public class FileRegistry {

    public static FileRegistry registry = new FileRegistry("/Users/lee/Downloads/ipc");

    private Path registryFile;
    private final Map<String, String> serviceMap = new ConcurrentHashMap<>();
    private volatile boolean running = true;

    public FileRegistry(String filePath) {
//        try{
//            this.registryFile = Paths.get(filePath);
//            if (!Files.exists(registryFile)) {
//                Files.createFile(registryFile);
//            }
//            loadServices();
//            startWatcher();
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void loadServices() throws IOException {
        List<String> lines = Files.readAllLines(registryFile);
        serviceMap.clear();
        for (String line : lines) {
            String[] parts = line.split("=");
            if (parts.length == 2) {
                serviceMap.put(parts[0].trim(), parts[1].trim());
            }
        }
    }

    private void startWatcher() {
        Thread watcherThread = new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                Path dir = registryFile.getParent();
                dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                while (running) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.context().toString().equals(registryFile.getFileName().toString())) {
                            loadServices();
                        }
                    }
                    key.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    public Boolean registerService(String serviceName) {
//        String entry = serviceName + "=" + socketPath + "\n";
//        Files.write(registryFile, entry.getBytes(), StandardOpenOption.APPEND);
        serviceMap.put(serviceName, serviceName);
        System.out.println("注册到注册中心:" + serviceName);
        return true;
    }

    public String getServiceAddress(String serviceName) {
        return serviceMap.get(serviceName);
    }

    public void shutdown() {
        running = false;
    }
}
