package com.lee.ipc.common.communication.encode;

import com.lee.ipc.common.protocol.IpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

/**
 * netty消息编码器
 * @author yanhuai lee
 */
public class MessageEncoder extends MessageToByteEncoder<IpcMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IpcMessage msg, ByteBuf out) throws Exception {
        // todo 这里之后移到主线程里

        System.out.println("🔒 Encoding message: " + msg);

        out.writeInt(0); // 预留长度位置
        out.writeLong(msg.getRequestId());
        out.writeInt(msg.getSerializerType());
        out.writeInt(msg.getMessageType());

        byte[] payload = msg.getContent();
        out.writeInt(payload.length);
        out.writeBytes(payload);

        // 4. 更新消息长度（不包括长度字段自身）
        int length = out.readableBytes() - 4;
        out.setInt(0, length);

        System.out.println("✅ Encoded message size: " + length + " bytes");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Business handler error: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}
