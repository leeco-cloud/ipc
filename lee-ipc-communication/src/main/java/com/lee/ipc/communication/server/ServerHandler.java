package com.lee.ipc.communication.server;

import com.lee.ipc.common.IpcMessage;
import com.lee.ipc.serialization.Serializer;
import com.lee.ipc.serialization.fury.FurySerializer;
import com.lee.ipc.serialization.json.JsonSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;

/**
 * netty服务端
 * @author yanhuai lee
 */
public class ServerHandler extends SimpleChannelInboundHandler<IpcMessage> {

    private final Map<Integer, Serializer> serializers = Map.of(
            1, new JsonSerializer(),
            2, new FurySerializer()
    );

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IpcMessage msg) throws Exception {
        Serializer serializer = serializers.get(msg.getSerializerType());
        if (serializer == null) {
            throw new RuntimeException("Unsupported serializer type");
        }

        // 处理请求并生成响应
        Object request = serializer.deserialize(msg.getContent(), Object.class);
        Object response = processRequest(request);

        IpcMessage respMsg = new IpcMessage();
        respMsg.setRequestId(msg.getRequestId());
        respMsg.setSerializerType(msg.getSerializerType());
        respMsg.setContent(serializer.serialize(response));

        ctx.writeAndFlush(respMsg);
    }

    private Object processRequest(Object request) {
        // todo 实际业务处理逻辑
        return null;
    }

}
