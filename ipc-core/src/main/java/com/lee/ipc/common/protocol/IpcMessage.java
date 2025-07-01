package com.lee.ipc.common.protocol;

import com.lee.ipc.common.exception.ErrorCode;
import com.lee.ipc.common.monitor.MonitorSupport;
import com.lee.ipc.common.monitor.MonitorType;
import com.lee.ipc.common.serialization.SerializationFactory;
import com.lee.ipc.common.serialization.Serializer;
import com.lee.ipc.common.serialization.common.SerializerType;
import lombok.Getter;
import lombok.Setter;

/**
 * IPC消息协议
 * @author yanhuai lee
 */
@Getter
@Setter
public class IpcMessage {

    private final Long requestId;
    private final Integer serializerType;
    private final Integer messageType;

    private Long ipcRequestTime = 0L;
    private Long ipcResponseTime = 0L;

    private Long requestDeserializeTime = 0L;
    private Long responseSerializeTime = 0L;

    private Long bizTime = 0L;

    private byte[] content;

    public IpcMessage(Long requestId, Integer serializerType, Integer messageType) {
        this.requestId = requestId;
        this.serializerType = serializerType;
        this.messageType = messageType;
    }

    public void serializeRequest(SerializerType serializerType, IpcMessageRequest ipcMessageContent) throws Exception {
        Serializer serializer = SerializationFactory.getSerializer(serializerType);
        MonitorSupport.start(requestId, MonitorType.REQUEST_SERIALIZE_SPEND_TIME);
        this.content = serializer.serialize(ipcMessageContent);
        MonitorSupport.stop(requestId, MonitorType.REQUEST_SERIALIZE_SPEND_TIME);
    }

    public void serializeResponse(SerializerType serializerType, IpcMessageResponse ipcMessageContent) throws Exception {
        Serializer serializer = SerializationFactory.getSerializer(serializerType);
        long start = System.nanoTime();
        this.content = serializer.serialize(ipcMessageContent);
        this.responseSerializeTime = System.nanoTime() - start;
    }

    public IpcMessageRequest deserializeRequest(SerializerType serializerType) throws Exception {
        Serializer serializer = SerializationFactory.getSerializer(serializerType);
        long start = System.nanoTime();
        IpcMessageRequest deserialize = serializer.deserialize(content, IpcMessageRequest.class);
        this.requestDeserializeTime = System.nanoTime() - start;
        return deserialize;
    }

    public IpcMessageResponse deserializeResponse(SerializerType serializerType) throws Exception {
        Serializer serializer = SerializationFactory.getSerializer(serializerType);
        MonitorSupport.start(requestId, MonitorType.RESPONSE_DESERIALIZE_SPEND_TIME);
        if (content == null || content.length == 0) {
            // 服务端位置异常
            IpcMessageResponse ipcMessageResponse = new IpcMessageResponse();
            ipcMessageResponse.setErrorCode(ErrorCode.SERVICE_INVOKE_ERROR.getCode());
            ipcMessageResponse.setErrorMsg(ErrorCode.SERVICE_INVOKE_ERROR.getMessage());
            return ipcMessageResponse;
        }
        IpcMessageResponse deserialize = serializer.deserialize(content, IpcMessageResponse.class);
        MonitorSupport.stop(requestId, MonitorType.RESPONSE_DESERIALIZE_SPEND_TIME);
        return deserialize;
    }

}
