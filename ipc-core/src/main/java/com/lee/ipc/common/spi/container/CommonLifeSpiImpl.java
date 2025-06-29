package com.lee.ipc.common.spi.container;

/**
 * 通用容器生命周期SPI实现
 * @author yanhuai lee
 */
public class CommonLifeSpiImpl implements ContainerLifeSpi {

    @Override
    public int containerPort() {
        return 8999;
    }

    @Override
    public String containerName() {
        return "testContainer";
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
