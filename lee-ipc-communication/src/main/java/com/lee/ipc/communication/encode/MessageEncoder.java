package com.lee.ipc.communication.encode;

import com.lee.ipc.common.IpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * netty消息编码器
 * @author yanhuai lee
 */
public class MessageEncoder extends MessageToByteEncoder<IpcMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IpcMessage msg, ByteBuf out) throws Exception {
        // todo 这里之后移到主线程里
        byte[] content = msg.getContent();
        int contentLength = content.length;
        int totalLength = 4 + 8 + 4 + 4 + contentLength; // 计算总长度

        // 协议结构: [总长度][requestId][序列化类型][内容长度][内容]
        out.writeInt(totalLength);
        out.writeLong(msg.getRequestId());
        out.writeInt(msg.getSerializerType());
        out.writeInt(contentLength);
        out.writeBytes(content);
    }

}
