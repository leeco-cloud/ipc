package com.lee.ipc.common.communication.client;

import com.alibaba.fastjson.JSON;
import com.lee.ipc.common.protocol.IpcMessage;
import com.lee.ipc.common.protocol.ResponseFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * netty客户端处理器
 * @author yanhuai lee
 */
public class ClientHandler extends SimpleChannelInboundHandler<IpcMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IpcMessage msg) {
        System.out.println("receive response msg: " + JSON.toJSONString(msg));

        ResponseFuture responseFuture = ResponseFuture.futures.get(msg.getRequestId());
        if (responseFuture != null) {
            responseFuture.success(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Business handler error: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}
