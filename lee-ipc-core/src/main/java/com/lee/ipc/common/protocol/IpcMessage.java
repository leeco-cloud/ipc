package com.lee.ipc.common.protocol;

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
    private byte[] content;

    public IpcMessage(Long requestId, Integer serializerType, Integer messageType) {
        this.requestId = requestId;
        this.serializerType = serializerType;
        this.messageType = messageType;
    }

    public void serialize(SerializerType serializerType, Object ipcMessageContent) throws Exception {
        Serializer serializer = SerializationFactory.getSerializer(serializerType);
        this.content = serializer.serialize(ipcMessageContent);
    }

    public IpcMessageRequest deserializeRequest(SerializerType serializerType) throws Exception {
        Serializer serializer = SerializationFactory.getSerializer(serializerType);
        return serializer.deserialize(content, IpcMessageRequest.class);
    }

    public IpcMessageResponse deserialize(SerializerType serializerType) throws Exception {
        Serializer serializer = SerializationFactory.getSerializer(serializerType);
        return serializer.deserialize(content, IpcMessageResponse.class);
    }

}
