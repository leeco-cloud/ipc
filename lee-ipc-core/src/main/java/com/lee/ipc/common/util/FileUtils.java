package com.lee.ipc.common.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author yanhuai lee
 */
public class FileUtils {

    public static synchronized void createFile(String fileFullPath) {
        try {
            Path socketPath = Paths.get(fileFullPath);
            if (Files.exists(socketPath)) {
                System.out.println("Deleting existing socket file: " + fileFullPath);
                Files.deleteIfExists(socketPath);
            }

            // 确保父目录存在
            File parentDir = socketPath.getParent().toFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

        } catch (Exception e) {
            // 处理创建文件时的IO异常（如路径无效、权限不足等）
            System.err.println("操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
