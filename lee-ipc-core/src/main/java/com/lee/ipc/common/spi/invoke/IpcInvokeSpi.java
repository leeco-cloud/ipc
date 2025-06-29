package com.lee.ipc.common.spi.invoke;

/**
 * 请求链路SPI
 * @author yanhuai lee
 */
public interface IpcInvokeSpi {

    Object[] beforeInvoke(Object... request);

    Object afterInvoke(Object response);

}
