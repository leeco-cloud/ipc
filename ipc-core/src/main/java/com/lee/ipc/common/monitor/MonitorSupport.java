package com.lee.ipc.common.monitor;

import com.alibaba.fastjson.JSON;
import com.lee.ipc.common.communication.IpcConfig;
import com.lee.ipc.common.log.RuntimeLogger;
import com.lee.ipc.common.protocol.IpcMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 监控支持
 * @author yanhuai lee
 */
public class MonitorSupport {

    public static ThreadPoolExecutor monitorThreadPool = new ThreadPoolExecutor(
            1, IpcConfig.currentCpu, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

    private static final MonitorMeta<Long, Map<String, Long>> allMonitorTime = new MonitorMeta<>(10000);

    public static void start(Long requestId, MonitorType monitorType){
        Map<String, Long> monitorTime = allMonitorTime.getOrDefault(requestId, new ConcurrentHashMap<>());
        monitorTime.put(monitorType.name(), System.nanoTime());
        allMonitorTime.put(requestId, monitorTime);
    }

    public static void stop(Long requestId, MonitorType monitorType){
        Map<String, Long> monitorTime = allMonitorTime.get(requestId);
        Long startTime = monitorTime.get(monitorType.name());
        if (startTime != null){
            monitorTime.put(monitorType.name(), System.nanoTime() - startTime);
        }
    }

    /**
     * 上报监控数据
     */
    public static void recordMetrics(Long requestId){
        try{
            monitorThreadPool.execute(()->{
                Map<String, Long> monitorData = MonitorSupport.allMonitorTime.get(requestId);
                if (monitorData != null){
                    RuntimeLogger.info(JSON.toJSONString(monitorData));
                    MonitorSupport.allMonitorTime.remove(requestId);
                }
            });
        }catch (Exception e){
            RuntimeLogger.error(e.getMessage());
        }
    }

    public static void addRecord(Long requestId, IpcMessage response) {
        try{
            Map<String, Long> monitorData = allMonitorTime.get(requestId);
            if (monitorData != null){
                Long ipcRequestTime = response.getIpcRequestTime();
                monitorData.put(MonitorType.REQUEST_IPC_SPEND_TIME.name(), ipcRequestTime);

                Long ipcResponseTime = response.getIpcResponseTime();
                monitorData.put(MonitorType.RESPONSE_IPC_SPEND_TIME.name(), ipcResponseTime);

                Long requestDeserializeTime = response.getRequestDeserializeTime();
                monitorData.put(MonitorType.REQUEST_DESERIALIZE_SPEND_TIME.name(), requestDeserializeTime);

                Long responseSerializeTime = response.getResponseSerializeTime();
                monitorData.put(MonitorType.RESPONSE_SERIALIZE_SPEND_TIME.name(), responseSerializeTime);

                Long bizTime = response.getBizTime();
                monitorData.put(MonitorType.BIZ_SPEND_TIME.name(), bizTime);
            }
        }catch (Exception e){
            // ignore
        }
    }

}
