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
        if (RegistryLocalCenter.running.get()){
            RegistryLocalCenter.getInstance().fullScanDirectory();
        }
    }

    @Override
    public void onFileChange(File file) {
        super.onFileChange(file);
        if (RegistryLocalCenter.running.get()){
            RegistryLocalCenter.getInstance().fullScanDirectory();
        }
    }

    @Override
    public void onFileCreate(File file) {
        super.onFileCreate(file);
        if (RegistryLocalCenter.running.get()){
            RegistryLocalCenter.getInstance().fullScanDirectory();
        }
    }

    @Override
    public void onDirectoryDelete(File directory) {
        super.onDirectoryDelete(directory);
        if (RegistryLocalCenter.running.get()){
            RegistryLocalCenter.getInstance().fullScanDirectory();
        }
    }

    @Override
    public void onDirectoryChange(File directory) {
        super.onDirectoryChange(directory);
        if (RegistryLocalCenter.running.get()){
            RegistryLocalCenter.getInstance().fullScanDirectory();
        }
    }

    @Override
    public void onDirectoryCreate(File directory) {
        super.onDirectoryCreate(directory);
        if (RegistryLocalCenter.running.get()){
            RegistryLocalCenter.getInstance().fullScanDirectory();
        }
    }

}
