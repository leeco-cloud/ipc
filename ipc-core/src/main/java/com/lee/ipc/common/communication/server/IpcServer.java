package com.lee.ipc.common.communication.server;

import com.lee.ipc.common.communication.IpcConfig;
import com.lee.ipc.common.communication.decode.MessageDecoder;
import com.lee.ipc.common.communication.encode.MessageEncoder;
import com.lee.ipc.common.log.BootLogger;
import com.lee.ipc.common.register.RegistryLocalCenter;
import com.lee.ipc.common.util.FileUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.unix.DomainSocketAddress;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * uds服务端
 * @author yanhuai lee
 */
public class IpcServer extends IpcConfig {

    private String containerName;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private static Channel serverChannel;
    private final EventLoopGroup bossGroup = createEventLoopGroup(currentCpu, useUDS);
    private final EventLoopGroup workerGroup = createEventLoopGroup(currentCpu * 2, useUDS);
    private SocketAddress socketAddress;

    public void init(String containerName) throws Exception {
        this.containerName = containerName;
        String udsPath = System.getProperty("java.io.tmpdir") + "/" + containerName + ".sock";
        socketAddress = useUDS ? new DomainSocketAddress(udsPath) : new InetSocketAddress(port);
        if (useUDS) {
            if (FileUtils.exists(udsPath)) {
                FileUtils.deleteFile(udsPath);
            }
            FileUtils.createFile(udsPath);
        }
        start(udsPath);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> stopServer(udsPath)));
    }

    public void start(String udsPath) {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(selectServerChannelClass(useUDS));

        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);

        if (!useUDS){
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true); // 禁用Nagle算法
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        }

        bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) ;// 内存池
        bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator());

        bootstrap.childHandler(new ChannelInitializer<>() {
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

        bindFuture = bootstrap.bind(socketAddress);
        BootLogger.info("容器:" + containerName + ", ✅ Server started ");

        // 异步处理绑定结果
        bindFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                serverChannel = future.channel();
                BootLogger.info("容器:" + containerName + ", Server started successfully");
            } else {
                BootLogger.error("容器:" + containerName + ", Server start failed: " + future.cause());
                stopServer(udsPath);
            }
        });

    }

    private void stopServer(String udsPath) {
        BootLogger.info("容器:" + containerName + ", Shutting down server...");

        if (serverChannel != null) {
            serverChannel.close();
        }

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        RegistryLocalCenter.removeService();

        try{
            FileUtils.deleteFile(udsPath);
        }catch (Exception e){
            // ignore
        }

        BootLogger.info("容器:" + containerName + ", Server stopped");
    }

}
