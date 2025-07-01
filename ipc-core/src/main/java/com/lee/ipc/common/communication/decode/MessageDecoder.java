package com.lee.ipc.common.communication.decode;

import com.lee.ipc.common.protocol.IpcMessage;
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
        if (in.readableBytes() < 4) {
            // 等待完整头部
            return;
        }

        in.markReaderIndex();
        int totalLength = in.readInt();
        if (in.readableBytes() < totalLength - 4) {
            in.resetReaderIndex();
            return;
        }

        IpcMessage message = new IpcMessage(in.readLong(), in.readInt(), in.readInt());

        long ipcRequestTime = in.readLong();
        long ipcResponseTime = in.readLong();

        // 请求
        if (ipcResponseTime == 0L){
            message.setIpcRequestTime(System.nanoTime() - ipcRequestTime);
        }else{
            // 响应
            message.setIpcRequestTime(ipcRequestTime);
            message.setIpcResponseTime(System.nanoTime() - ipcResponseTime);
        }

        message.setRequestDeserializeTime(in.readLong());
        message.setResponseSerializeTime(in.readLong());

        message.setBizTime(in.readLong());

        int contentLength = in.readInt();
        byte[] content = new byte[contentLength];
        in.readBytes(content);
        message.setContent(content);

        out.add(message);
    }

}
