package com.lee.ipc.common.communication.client;

import com.lee.ipc.common.communication.IpcConfig;
import com.lee.ipc.common.communication.decode.MessageDecoder;
import com.lee.ipc.common.communication.encode.MessageEncoder;
import com.lee.ipc.common.log.BootLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.unix.DomainSocketAddress;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * uds通讯协议
 * @author yanhuai lee
 */
public class IpcClient extends IpcConfig {

    private final AtomicBoolean running = new AtomicBoolean(false);

    public String containerName;

    public Channel channel;
    private SocketAddress socketAddress;
    private final EventLoopGroup group = createEventLoopGroup(currentCpu * 2, useUDS); // 0 = 默认CPU核数*2

    public IpcClientInvoke ipcClientInvoke;

    public static Map<String, IpcClient> allClients = new ConcurrentHashMap<>();

    public void init(String containerName) {
        this.containerName = containerName;

        String udsPath = System.getProperty("java.io.tmpdir") + "/" + containerName + ".sock";
        socketAddress = useUDS ? new DomainSocketAddress(udsPath) : new InetSocketAddress(port);

        start(containerName);
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void start(String containerName) {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(selectClientChannelClass(useUDS))

                    // 核心优化参数配置
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())

                    // 处理器链
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 编解码器
                            pipeline.addLast(new MessageEncoder());
                            pipeline.addLast(new MessageDecoder());

                            // 业务处理器
                            pipeline.addLast(new ClientHandler(containerName));
                        }
                    });

            // 连接服务器（UDS或TCP）
            connectServer(bootstrap, socketAddress);

            BootLogger.info("✅ Connection established");
        } catch (Exception e) {
            // 状态是重置为false
            running.set(false);
            BootLogger.error(e.getMessage(), e);
        }
    }

    private void connectServer(Bootstrap bootstrap, SocketAddress socketAddress) {
        AtomicInteger retryCount = new AtomicInteger(0);

        ChannelFuture connectFuture = bootstrap.connect(socketAddress);
        channel = connectFuture.channel();
        connectFuture.addListener((ChannelFuture future) -> {
            if (!future.isSuccess()) {
                int currentRetry = retryCount.incrementAndGet();
                int BASE_DELAY_SECONDS = 1;

                // 指数退避策略：2^currentRetry * base_delay
                long delay = (long) Math.pow(2, currentRetry) * BASE_DELAY_SECONDS;
                BootLogger.error(future.cause().getMessage(), future.cause());
                BootLogger.info("服务未就绪. 重试 (%d) in %ds...%n", currentRetry, delay);

                // 调度重试
                future.channel().eventLoop().schedule(
                        () -> connectServer(bootstrap, socketAddress),
                        delay,
                        TimeUnit.SECONDS
                );
            } else {
                BootLogger.info("Successfully connected to server!");
                channel = future.channel();
                ipcClientInvoke = new IpcClientInvoke(channel);
            }
        });
    }

    private void stop(){
        channel.close();
        group.shutdownGracefully();
    }

}
