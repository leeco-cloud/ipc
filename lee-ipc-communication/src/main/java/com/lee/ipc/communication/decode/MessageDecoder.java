package com.lee.ipc.communication.decode;

import com.lee.ipc.common.IpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * netty消息解码器
 * @author yanhuai lee
 */
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 20) return; // 等待完整头部

        in.markReaderIndex();
        int totalLength = in.readInt();
        if (in.readableBytes() < totalLength - 4) {
            in.resetReaderIndex();
            return;
        }

        IpcMessage message = new IpcMessage();
        message.setRequestId(in.readLong());
        message.setSerializerType(in.readInt());
        int contentLength = in.readInt();
        byte[] content = new byte[contentLength];
        in.readBytes(content);
        message.setContent(content);

        out.add(message);
    }

}
