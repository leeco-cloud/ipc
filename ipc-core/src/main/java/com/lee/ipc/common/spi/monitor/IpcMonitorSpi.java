package com.lee.ipc.common.spi.monitor;

import java.util.Map;

/**
 * 监控上报SPI
 * @author yanhuai lee
 */
public interface IpcMonitorSpi {

    void recordMetrics(Long requestId, Map<String, Long> monitorData);

}
