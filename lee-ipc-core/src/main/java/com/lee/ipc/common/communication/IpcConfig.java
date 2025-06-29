package com.lee.ipc.common.communication;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerDomainSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueDomainSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerDomainSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * ipc netty 统一配置
 * @author yanhuai lee
 */
public abstract class IpcConfig {

    public static final Integer currentCpu = Runtime.getRuntime().availableProcessors();

    protected final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    protected final boolean useUDS = !isWindows && (Epoll.isAvailable() || KQueue.isAvailable());

    protected final int port = 8999;
    protected final String udsPath = System.getProperty("java.io.tmpdir") + "/netty_server.sock";

    protected final SocketAddress socketAddress = useUDS ? new DomainSocketAddress(udsPath) : new InetSocketAddress(port);

    protected static EventLoopGroup createEventLoopGroup(int threads, boolean useUDS) {
        if (useUDS && Epoll.isAvailable()) {
            return new EpollEventLoopGroup(threads);
        } else if (useUDS && KQueue.isAvailable()) {
            return new KQueueEventLoopGroup(threads);
        }
        return new NioEventLoopGroup(threads);
    }

    protected static Class<? extends Channel> selectClientChannelClass(boolean useUDS) {
        if (useUDS && Epoll.isAvailable()) {
            return EpollDomainSocketChannel.class;
        } else if (useUDS && KQueue.isAvailable()) {
            return KQueueDomainSocketChannel.class;
        }
        return NioSocketChannel.class;
    }

    protected static Class<? extends ServerChannel> selectServerChannelClass(boolean useUDS) {
        if (useUDS && Epoll.isAvailable()) {
            return EpollServerDomainSocketChannel.class;
        } else if (useUDS && KQueue.isAvailable()) {
            return KQueueServerDomainSocketChannel.class;
        }
        return NioServerSocketChannel.class;
    }

}
