package com.lee.ipc.common.communication.client;

import com.lee.ipc.common.communication.IpcConfig;
import com.lee.ipc.common.communication.decode.MessageDecoder;
import com.lee.ipc.common.communication.encode.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * uds通讯协议
 * @author yanhuai lee
 */
public class IpcClient extends IpcConfig {

    private final AtomicBoolean running = new AtomicBoolean(false);

    private Channel channel;

    EventLoopGroup group = createEventLoopGroup(currentCpu * 2, useUDS); // 0 = 默认CPU核数*2

    public static IpcClientInvoke ipcClientInvoke;

    public void init() throws Exception {
        start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        ipcClientInvoke = new IpcClientInvoke(channel);
    }

    public void start() throws Exception {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(selectClientChannelClass(useUDS))

                    // 3. 核心优化参数配置
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())

                    // 4. 处理器链
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 编解码器
                            pipeline.addLast(new MessageEncoder());
                            pipeline.addLast(new MessageDecoder());

                            // 业务处理器
                            pipeline.addLast(new ClientHandler());
                        }
                    });

            // 5. 连接服务器（UDS或TCP）
            connectServer(bootstrap, socketAddress);

            System.out.println("✅ Connection established");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void connectServer(Bootstrap bootstrap, SocketAddress socketAddress) {
        AtomicInteger retryCount = new AtomicInteger(0);

        ChannelFuture connectFuture = bootstrap.connect(socketAddress);

        connectFuture.addListener((ChannelFuture future) -> {
            if (!future.isSuccess()) {
                Throwable cause = future.cause();

                // 检查是否为UDS文件不存在的异常
                if (cause instanceof java.io.FileNotFoundException) {

                    int currentRetry = retryCount.incrementAndGet();
                    int BASE_DELAY_SECONDS = 1;

                    // 指数退避策略：2^currentRetry * base_delay
                    long delay = (long) Math.pow(2, currentRetry) * BASE_DELAY_SECONDS;
                    System.out.printf("Server not ready. Retrying (%d) in %ds...%n",
                            currentRetry, delay);

                    // 调度重试
                    future.channel().eventLoop().schedule(
                            () -> connectServer(bootstrap, socketAddress),
                            delay,
                            TimeUnit.SECONDS
                    );
                } else {
                    cause.printStackTrace();
                }
            } else {
                System.out.println("Successfully connected to server!");
                channel = future.channel();
            }
        });
    }

    private void stop(){
        channel.close();
        group.shutdownGracefully();
    }

}
