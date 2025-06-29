package com.lee.ipc.common.communication.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author yanhuai lee
 */
public class ClientHeartbeatHandler  extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            System.out.println("❤️ Sending heartbeat...");

            ctx.writeAndFlush("HEARTBEAT");
        }
    }
}
