package com.lee.ipc.common.communication.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * netty心跳处理器
 * @author yanhuai lee
 */
public class ServerHeartbeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            System.out.println("⚠️ Connection idle, closing: " + ctx.channel());
            ctx.close();
        }
    }

}
