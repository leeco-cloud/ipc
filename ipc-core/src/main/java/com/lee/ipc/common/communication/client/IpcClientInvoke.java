package com.lee.ipc.common.communication.client;

import com.alibaba.fastjson.JSON;
import com.lee.ipc.common.constant.MessageType;
import com.lee.ipc.common.protocol.IpcMessage;
import com.lee.ipc.common.protocol.IpcMessageRequest;
import com.lee.ipc.common.protocol.IpcMessageResponse;
import com.lee.ipc.common.protocol.ResponseFuture;
import com.lee.ipc.common.serialization.common.SerializerType;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * uds执行器
 * @author yanhuai lee
 */
@NoArgsConstructor
@AllArgsConstructor
public class IpcClientInvoke {

    private Channel channel;

    public IpcMessageResponse sendRequest(SerializerType serializerType, MessageType messageType, String methodName, Object[] args) throws Exception {

        IpcMessage ipcMessage = new IpcMessage(System.currentTimeMillis(), serializerType.getType(), messageType.getMessageTypeCode());

        // todo 鹰眼打标

        // todo 拦截器链SPI

        IpcMessageRequest ipcMessageRequest = new IpcMessageRequest(methodName, args);

        ipcMessage.serialize(serializerType, ipcMessageRequest);

        ResponseFuture responseFuture = new ResponseFuture(ipcMessage.getRequestId());

        IpcMessageResponse ipcMessageResponse = doSendIpc(ipcMessage, serializerType, responseFuture);

        // todo 拦截器链 SPI

        // todo 鹰眼打标

        // todo 上报完整链路监控数据 monitor

        return ipcMessageResponse;
    }

    private IpcMessageResponse doSendIpc(IpcMessage ipcMessage, SerializerType serializerType, ResponseFuture responseFuture) throws Exception {

        channel.eventLoop().execute(()-> channel.writeAndFlush(ipcMessage));

        System.out.println("send request msg: " + JSON.toJSONString(ipcMessage));

        IpcMessage response = responseFuture.get(5, TimeUnit.SECONDS);

        return response.deserializeResponse(serializerType);
    }

}
