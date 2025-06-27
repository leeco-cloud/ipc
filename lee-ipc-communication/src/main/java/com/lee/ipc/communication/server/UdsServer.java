package com.lee.ipc.communication.server;

import com.lee.ipc.communication.decode.MessageDecoder;
import com.lee.ipc.communication.encode.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.DomainSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * uds服务端
 * @author yanhuai lee
 */
public class UdsServer {

    public void init() throws InterruptedException {
        EventLoopGroup group = new EpollEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(EpollServerDomainSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<DomainSocketChannel>() {
                        @Override
                        protected void initChannel(DomainSocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new MessageDecoder())
                                    .addLast(new MessageEncoder())
                                    .addLast(new ServerHandler());
                        }
                    });
            b.bind(new DomainSocketAddress("")).sync().channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

}
