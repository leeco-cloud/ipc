package com.lee.ipc.common.spi.container;

import org.springframework.context.Lifecycle;

/**
 * 容器生命周期SPI
 * @author yanhuai lee
 */
public interface ContainerLifeSpi extends Lifecycle {

    /**
     * 容器端口
     */
    int containerPort();

    /**
     * 容器名
     */
    String containerName();

}
