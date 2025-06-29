package com.lee.ipc.common.communication.server;

import com.alibaba.fastjson.JSON;
import com.lee.ipc.common.communication.IpcConfig;
import com.lee.ipc.common.protocol.IpcMessage;
import com.lee.ipc.common.protocol.IpcMessageRequest;
import com.lee.ipc.common.protocol.IpcMessageResponse;
import com.lee.ipc.common.serialization.common.SerializerType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * netty服务端
 * @author yanhuai lee
 */
public class ServerHandler extends SimpleChannelInboundHandler<IpcMessage> {

    public static ThreadPoolExecutor businessThreadPool = new ThreadPoolExecutor(
            IpcConfig.currentCpu * 2, IpcConfig.currentCpu * 4, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10000), new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IpcMessage msg) {
        // 异步处理业务逻辑（不阻塞IO线程）
        businessThreadPool.execute(() -> {
            IpcMessage result;
            try {
                result = invokeService(msg);
                IpcMessage finalResult = result;
                ctx.executor().execute(() -> ctx.writeAndFlush(finalResult));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 执行服务链路
     */
    private IpcMessage invokeService(IpcMessage msg) throws Exception {
        System.out.println("receive request msg: " + JSON.toJSONString(msg));

        SerializerType serializerType = SerializerType.getSerializerType(msg.getSerializerType());

        // 处理请求并生成响应
        IpcMessageRequest request = msg.deserializeRequest(serializerType);

        Object response = doInvokeService(request.getMethodName(), request.getArgs());

        IpcMessage respMsg = new IpcMessage(msg.getRequestId(), msg.getSerializerType(), msg.getMessageType());
        IpcMessageResponse ipcMessageResponse = new IpcMessageResponse(response);

        respMsg.serialize(serializerType, ipcMessageResponse);

        System.out.println("send response msg: " + JSON.toJSONString(respMsg));

        return respMsg;
    }

    /**
     * 执行业务逻辑
     */
    private Object doInvokeService(String methodName, Object[] args) throws Exception {
        // todo 实际业务处理逻辑
        Map<String,String> data = new HashMap<>();
        data.put("nihao " , "yanhuai lee");
        return data;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Business handler error: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

}
