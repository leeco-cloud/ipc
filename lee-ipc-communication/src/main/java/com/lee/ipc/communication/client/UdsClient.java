package com.lee.ipc.communication.client;

import com.lee.ipc.common.IpcMessage;
import com.lee.ipc.communication.decode.MessageDecoder;
import com.lee.ipc.communication.encode.MessageEncoder;
import com.lee.ipc.serialization.Serializer;
import com.lee.ipc.serialization.fury.FurySerializer;
import com.lee.ipc.serialization.json.JsonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.DomainSocketChannel;

import java.util.Map;

/**
 * uds通讯协议
 * @author yanhuai lee
 */
public class UdsClient {

    private Channel channel;
    private final Map<Integer, Serializer> serializers = Map.of(
            1, new JsonSerializer(),
            2, new FurySerializer()
    );

    public void connect(String socketPath) throws Exception {
        EventLoopGroup group = new EpollEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(EpollDomainSocketChannel.class)
                .handler(new ChannelInitializer<DomainSocketChannel>() {
                    @Override
                    protected void initChannel(DomainSocketChannel ch) {
                        ch.pipeline()
                                .addLast(new MessageDecoder())
                                .addLast(new MessageEncoder())
                                .addLast(new ClientHandler());
                    }
                });

        channel = b.connect(new DomainSocketAddress(socketPath)).sync().channel();
    }

    public Object sendRequest(Object request, int serializerType) throws Exception {
        Serializer serializer = serializers.get(serializerType);
        if (serializer == null) throw new RuntimeException("Unsupported serializer");

        IpcMessage msg = new IpcMessage();
        msg.setRequestId(System.currentTimeMillis());
        msg.setSerializerType(serializerType);
        msg.setContent(serializer.serialize(request));

        // 发送请求并等待响应
        ClientHandler.ResponseFuture future = new ClientHandler.ResponseFuture(msg);
        ClientHandler.addFuture(msg.getRequestId(), future);
        channel.writeAndFlush(msg);

        return future.get();
    }

}
