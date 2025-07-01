package com.lee.ipc.common.monitor;

/**
 * @author yanhuai lee
 */
public enum MonitorType {

    /**
     * QPS
     */
    QPS_COUNT,

    /**
     * 异常数
     */
    ERROR_COUNT,

    /**
     * 总耗时
     */
    ALL_SPEND_TIME,

    /**
     * 请求序列化耗时
     */
    REQUEST_SERIALIZE_SPEND_TIME,

    /**
     * 请求通讯耗时
     */
    REQUEST_IPC_SPEND_TIME,

    /**
     * 请求反序列化耗时
     */
    REQUEST_DESERIALIZE_SPEND_TIME,

    /**
     * 业务处理耗时
     */
    BIZ_SPEND_TIME,

    /**
     * 响应序列化耗时
     */
    RESPONSE_SERIALIZE_SPEND_TIME,

    /**
     * 响应通讯耗时
     */
    RESPONSE_IPC_SPEND_TIME,

    /**
     * 响应反序列化耗时
     */
    RESPONSE_DESERIALIZE_SPEND_TIME

}
