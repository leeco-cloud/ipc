package com.lee.ipc.common.register;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;

/**
 * 文件监听器
 * @author yanhuai lee
 */
public class FileMonitor extends FileAlterationListenerAdaptor {

    @Override
    public void onFileDelete(File file) {
        super.onFileDelete(file);
        RegistryLocalCenter.INSTANCE.fullScanDirectory();
    }

    @Override
    public void onFileChange(File file) {
        super.onFileChange(file);
        RegistryLocalCenter.INSTANCE.fullScanDirectory();
    }

    @Override
    public void onFileCreate(File file) {
        super.onFileCreate(file);
        RegistryLocalCenter.INSTANCE.fullScanDirectory();
    }

    @Override
    public void onDirectoryDelete(File directory) {
        super.onDirectoryDelete(directory);
        RegistryLocalCenter.INSTANCE.fullScanDirectory();
    }

    @Override
    public void onDirectoryChange(File directory) {
        super.onDirectoryChange(directory);
        RegistryLocalCenter.INSTANCE.fullScanDirectory();
    }

    @Override
    public void onDirectoryCreate(File directory) {
        super.onDirectoryCreate(directory);
        RegistryLocalCenter.INSTANCE.fullScanDirectory();
    }

}
