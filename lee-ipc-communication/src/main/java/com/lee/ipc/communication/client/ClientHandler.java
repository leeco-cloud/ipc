package com.lee.ipc.communication.client;

import com.lee.ipc.common.IpcMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * netty客户端处理器
 * @author yanhuai lee
 */
public class ClientHandler extends SimpleChannelInboundHandler<IpcMessage> {
    private static final ConcurrentHashMap<Long, ResponseFuture> futures = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IpcMessage msg) {
        ResponseFuture future = futures.remove(msg.getRequestId());
        if (future != null) {
            future.setResponse(msg);
        }
    }

    static void addFuture(long requestId, ResponseFuture future) {
        futures.put(requestId, future);
    }

    public static class ResponseFuture {
        private final CountDownLatch latch = new CountDownLatch(1);
        private volatile IpcMessage response;

        public ResponseFuture(IpcMessage request) {}

        public Object get() throws InterruptedException {
            latch.await(5, TimeUnit.SECONDS);
            return response != null ? response.getContent() : null;
        }

        public void setResponse(IpcMessage response) {
            this.response = response;
            latch.countDown();
        }
    }

}
