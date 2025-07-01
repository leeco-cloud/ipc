package com.lee.ipc.common.spi.invoke;

/**
 * 请求链路SPI
 * @author yanhuai lee
 */
public interface IpcInvokeSpi {

    Object[] beforeInvoke(Class<?> serviceInterface, String methodName, Object[] args);

    Object afterInvoke(Class<?> serviceInterface, String methodName, Object args);

}
