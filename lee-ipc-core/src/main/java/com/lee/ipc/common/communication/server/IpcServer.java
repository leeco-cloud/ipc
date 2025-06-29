package com.lee.ipc.common.communication.server;

import com.lee.ipc.common.communication.IpcConfig;
import com.lee.ipc.common.communication.decode.MessageDecoder;
import com.lee.ipc.common.communication.encode.MessageEncoder;
import com.lee.ipc.common.util.FileUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * uds服务端
 * @author yanhuai lee
 */
public class IpcServer extends IpcConfig {

    private final AtomicBoolean running = new AtomicBoolean(false);

    private static Channel serverChannel;

    EventLoopGroup bossGroup = createEventLoopGroup(currentCpu, useUDS);
    EventLoopGroup workerGroup = createEventLoopGroup(currentCpu * 2, useUDS);

    public void init() throws Exception {
        start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
    }

    public void start() throws Exception {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(selectServerChannelClass(useUDS))

                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true) // 禁用Nagle算法
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) // 内存池
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())

                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        // 编解码器
                        pipeline.addLast(new MessageEncoder());
                        pipeline.addLast(new MessageDecoder());

                        // 业务处理器
                        pipeline.addLast(new ServerHandler());
                    }
                });

        ChannelFuture bindFuture;
        if (useUDS) {
            FileUtils.createFile(udsPath);
            bindFuture = bootstrap.bind(new DomainSocketAddress(udsPath));
            System.out.println("✅ Server started on UDS: " + udsPath);
        } else {
            bindFuture = bootstrap.bind(new InetSocketAddress(port));
            System.out.println("✅ Server started on TCP port: " + port);
        }

        // 异步处理绑定结果
        bindFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                serverChannel = future.channel();
                System.out.println("Server started successfully");
            } else {
                System.err.println("Server start failed: " + future.cause());
                future.cause().printStackTrace();
                stopServer();
            }
        });

    }

    private void stopServer() {
        System.out.println("Shutting down server...");

        if (serverChannel != null) {
            serverChannel.close();
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        System.out.println("Server stopped");
    }

}
