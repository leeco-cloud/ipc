package com.lee.ipc.common.communication.client;

import com.lee.ipc.common.protocol.IpcMessage;
import com.lee.ipc.common.protocol.ResponseFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * netty客户端处理器
 * @author yanhuai lee
 */
public class ClientHandler extends SimpleChannelInboundHandler<IpcMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IpcMessage msg) {
        ResponseFuture responseFuture = ResponseFuture.futures.get(msg.getRequestId());
        if (responseFuture != null) {
            responseFuture.success(msg);
        }
    }

}
