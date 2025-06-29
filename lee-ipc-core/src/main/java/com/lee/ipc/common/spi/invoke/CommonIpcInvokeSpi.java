package com.lee.ipc.common.spi.invoke;

/**
 * 请求链路SPI
 * @author yanhuai lee
 */
public class CommonIpcInvokeSpi implements IpcInvokeSpi{

    @Override
    public Object[] beforeInvoke(Object... request) {
        return request;
    }

    @Override
    public Object afterInvoke(Object response) {
        return response;
    }
}
