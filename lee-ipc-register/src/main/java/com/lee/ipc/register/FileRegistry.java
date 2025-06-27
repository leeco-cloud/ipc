package com.lee.ipc.register;

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
    private final Path registryFile;
    private final Map<String, String> serviceMap = new ConcurrentHashMap<>();
    private volatile boolean running = true;

    public FileRegistry(String filePath) throws IOException {
        this.registryFile = Paths.get(filePath);
        if (!Files.exists(registryFile)) {
            Files.createFile(registryFile);
        }
        loadServices();
        startWatcher();
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

    public void registerService(String serviceName, String socketPath) throws IOException {
        String entry = serviceName + "=" + socketPath + "\n";
        Files.write(registryFile, entry.getBytes(), StandardOpenOption.APPEND);
    }

    public String getServiceAddress(String serviceName) {
        return serviceMap.get(serviceName);
    }

    public void shutdown() {
        running = false;
    }
}
