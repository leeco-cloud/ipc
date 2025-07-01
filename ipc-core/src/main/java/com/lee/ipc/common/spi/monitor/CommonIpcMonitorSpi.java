package com.lee.ipc.common.spi.monitor;

import com.alibaba.fastjson.JSON;
import com.lee.ipc.common.log.RuntimeLogger;

import java.util.Map;

/**
 * 请求链路SPI
 * @author yanhuai lee
 */
public class CommonIpcMonitorSpi implements IpcMonitorSpi {

    @Override
    public void recordMetrics(Long requestId, Map<String, Long> monitorData) {
        RuntimeLogger.info(JSON.toJSONString(monitorData));
    }

}
