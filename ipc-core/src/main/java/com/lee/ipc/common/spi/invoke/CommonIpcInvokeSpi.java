package com.lee.ipc.common.spi.invoke;

/**
 * 请求链路SPI
 * @author yanhuai lee
 */
public class CommonIpcInvokeSpi implements IpcInvokeSpi{

    @Override
    public Object[] beforeInvoke(Class<?> serviceInterface, String methodName, Object... args) {
        return args;
    }

    @Override
    public Object afterInvoke(Class<?> serviceInterface, String methodName, Object response) {
        return response;
    }
}
