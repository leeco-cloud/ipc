package com.lee.ipc.common.spi.container;

import org.springframework.context.Lifecycle;

/**
 * 容器生命周期SPI
 * @author yanhuai lee
 */
public interface ContainerLifeSpi extends Lifecycle {

    default void restart(){
        stop();
        start();
    }

}
