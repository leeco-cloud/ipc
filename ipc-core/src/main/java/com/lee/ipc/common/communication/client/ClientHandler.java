package com.lee.ipc.common.communication.client;

import com.lee.ipc.common.exception.ErrorCode;
import com.lee.ipc.common.log.RuntimeLogger;
import com.lee.ipc.common.protocol.IpcMessage;
import com.lee.ipc.common.protocol.ResponseFuture;
import com.lee.ipc.common.register.RegistryLocalCenter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * netty客户端处理器
 * @author yanhuai lee
 */
public class ClientHandler extends SimpleChannelInboundHandler<IpcMessage> {

    private final String containerName;

    public ClientHandler(String containerName){
        this.containerName = containerName;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IpcMessage msg) {
        ResponseFuture responseFuture = ResponseFuture.futures.get(msg.getRequestId());
        if (responseFuture != null) {
            responseFuture.success(msg);
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        RuntimeLogger.error(ErrorCode.NETTY_CLIENT_ERROR);
        IpcClient.allClients.remove(containerName);
        RegistryLocalCenter.getInstance().reconnectAllServer();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        RuntimeLogger.error(ErrorCode.NETTY_CLIENT_ERROR.getMessage(), cause);
        ctx.close();
        IpcClient.allClients.remove(containerName);
        RegistryLocalCenter.getInstance().reconnectAllServer();
    }

}
