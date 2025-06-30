package com.lee.ipc.common.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文件工具类
 * @author yanhuai lee
 */
public class FileUtils {

    /**
     * 创建目录（包含多级目录）
     * @param dirPath 目录路径
     * @return 创建的 Path 对象
     */
    public static Path createDirectories(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        return Files.createDirectories(path);
    }

    /**
     * 创建文件（自动创建父目录）
     * @param filePath 文件路径
     * @return 创建的 Path 对象
     */
    public static Path createFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            createDirectories(parent.toString());
        }
        return Files.createFile(path);
    }

    /**
     * 删除文件
     * @param filePath 文件路径
     */
    public static void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
    }

    /**
     * 递归删除目录及内容
     * @param dirPath 目录路径
     */
    public static void deleteDirectoryRecursively(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) return;

        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 复制文件
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @param replaceExisting 是否替换已存在文件
     */
    public static void copyFile(String sourcePath, String targetPath, boolean replaceExisting) throws IOException {
        Path source = Paths.get(sourcePath);
        Path target = Paths.get(targetPath);

        CopyOption[] options = replaceExisting ?
                new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} :
                new CopyOption[]{};

        Files.copy(source, target, options);
    }

    /**
     * 移动/重命名文件
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @param replaceExisting 是否替换已存在文件
     */
    public static void moveFile(String sourcePath, String targetPath, boolean replaceExisting) throws IOException {
        Path source = Paths.get(sourcePath);
        Path target = Paths.get(targetPath);

        CopyOption[] options = replaceExisting ?
                new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} :
                new CopyOption[]{};

        Files.move(source, target, options);
    }

    /**
     * 读取文件内容为字符串 (UTF-8)
     * @param filePath 文件路径
     * @return 文件内容字符串
     */
    public static String readFileToString(String filePath) throws IOException {
        return readFileToString(filePath, StandardCharsets.UTF_8);
    }

    /**
     * 读取文件内容为字符串 (指定编码)
     * @param filePath 文件路径
     * @param charset 字符编码
     * @return 文件内容字符串
     */
    public static String readFileToString(String filePath, Charset charset) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path, charset);
    }

    /**
     * 写入字符串到文件 (UTF-8)
     * @param filePath 文件路径
     * @param content 要写入的内容
     * @param append 是否追加模式
     */
    public static void writeStringToFile(String filePath, String content, boolean append) throws IOException {
        writeStringToFile(filePath, content, StandardCharsets.UTF_8, append);
    }

    /**
     * 写入字符串到文件 (指定编码)
     * @param filePath 文件路径
     * @param content 要写入的内容
     * @param charset 字符编码
     * @param append 是否追加模式
     */
    public static void writeStringToFile(String filePath, String content, Charset charset, boolean append) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            createFile(filePath);
        }

        StandardOpenOption option = append ?
                StandardOpenOption.APPEND :
                StandardOpenOption.TRUNCATE_EXISTING;

        Files.writeString(path, content, charset,
                StandardOpenOption.WRITE,
                option);
    }

    /**
     * 检查文件/目录是否存在
     * @param path 路径
     * @return 是否存在
     */
    public static boolean exists(String path) {
        return Files.exists(Paths.get(path));
    }

    /**
     * 获取目录下的文件列表
     * @param dirPath 目录路径
     * @return 文件路径列表
     */
    public static List<String> listFiles(String dirPath) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dirPath))) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 获取目录下的文件列表 包括递归子目录
     * @param dirPath 目录路径
     * @return 文件路径列表
     */
    public static List<String> listAllFilesRecursively(String dirPath) throws IOException {
        Path start = Paths.get(dirPath);
        try (Stream<Path> stream = Files.walk(start)) {
            return stream
                    .filter(Files::isRegularFile)  // 只保留普通文件
                    .map(Path::toString)           // 转换为字符串
                    .collect(Collectors.toList());
        }
    }

    /**
     * 获取文件大小 (字节)
     * @param filePath 文件路径
     * @return 文件大小
     */
    public static long getFileSize(String filePath) throws IOException {
        return Files.size(Paths.get(filePath));
    }

    /**
     * 获取文件最后修改时间
     * @param filePath 文件路径
     * @return 最后修改时间 (毫秒)
     */
    public static long getLastModifiedTime(String filePath) throws IOException {
        return Files.getLastModifiedTime(Paths.get(filePath)).toMillis();
    }

}
