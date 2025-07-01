package com.lee.ipc.common.communication.encode;

import com.lee.ipc.common.protocol.IpcMessage;
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
        System.out.println("🔒 Encoding message: " + msg);

        out.writeInt(0); // 预留长度位置
        out.writeLong(msg.getRequestId());
        out.writeInt(msg.getSerializerType());
        out.writeInt(msg.getMessageType());

        // 请求
        if (msg.getIpcRequestTime().equals(0L)){
            out.writeLong(System.nanoTime());
            out.writeLong(0L);
        }else{
            // 响应
            out.writeLong(msg.getIpcRequestTime());
            out.writeLong(System.nanoTime());
        }

        out.writeLong(msg.getRequestDeserializeTime());
        out.writeLong(msg.getResponseSerializeTime());

        out.writeLong(msg.getBizTime());

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
