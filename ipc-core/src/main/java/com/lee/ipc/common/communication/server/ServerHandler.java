package com.lee.ipc.common.communication.server;

import com.lee.ipc.common.cache.ServiceCache;
import com.lee.ipc.common.communication.IpcConfig;
import com.lee.ipc.common.communication.support.ThreadLocalContent;
import com.lee.ipc.common.exception.ErrorCode;
import com.lee.ipc.common.log.RuntimeLogger;
import com.lee.ipc.common.protocol.IpcMessage;
import com.lee.ipc.common.protocol.IpcMessageRequest;
import com.lee.ipc.common.protocol.IpcMessageResponse;
import com.lee.ipc.common.serialization.common.SerializerType;
import com.lee.ipc.common.spi.SpiLoader;
import com.lee.ipc.common.spi.invoke.IpcInvokeSpi;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
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
            result = invokeService(msg);
            IpcMessage finalResult = result;
            ctx.executor().execute(() -> ctx.writeAndFlush(finalResult));
        });
    }

    /**
     * 执行服务链路
     */
    private IpcMessage invokeService(IpcMessage msg) {
        SerializerType serializerType = SerializerType.getSerializerType(msg.getSerializerType());

        IpcMessage respMsg = new IpcMessage(msg.getRequestId(), msg.getSerializerType(), msg.getMessageType());
        respMsg.setIpcRequestTime(msg.getIpcRequestTime());
        respMsg.setIpcResponseTime(msg.getIpcResponseTime());

        try{
            // 处理请求并生成响应
            IpcMessageRequest request;
            try{
                request = msg.deserializeRequest(serializerType);
                respMsg.setRequestDeserializeTime(msg.getRequestDeserializeTime());
            }catch (Exception e){
                // 请求反序列化错误
                RuntimeLogger.error(ErrorCode.REQUEST_DESERIALIZE_ERROR.getMessage(), e);
                IpcMessageResponse ipcMessageResponse = new IpcMessageResponse();
                ipcMessageResponse.setErrorCode(ErrorCode.REQUEST_DESERIALIZE_ERROR.getCode());
                ipcMessageResponse.setErrorMsg(e.getMessage());
                respMsg.serializeResponse(serializerType, ipcMessageResponse);
                return respMsg;
            }

            // 拦截器链SPI 前置处理器
            List<IpcInvokeSpi> ipcInvokeSpi = SpiLoader.loadIpcInvokeSpi();

            Object[] args = request.getArgs();
            for (IpcInvokeSpi invokeSpi : ipcInvokeSpi) {
                args = invokeSpi.beforeInvoke(request.getInterfaceClass(), request.getMethodName(), args);
            }

            Map<String, Object> userData = request.getUserData();
            // 用户自定义数据
            if (!CollectionUtils.isEmpty(userData)) {
                ThreadLocalContent.putAllUserData(userData);
            }

            long startTime = System.nanoTime();
            Object response = doInvokeService(request.getServiceUniqueKey(), request.getMethodName(), request.getParameterTypes(), request.getArgs());
            long bizSpendTime = System.nanoTime() - startTime;

            // 拦截器链SPI 后置处理器
            for (IpcInvokeSpi invokeSpi : ipcInvokeSpi) {
                response = invokeSpi.afterInvoke(request.getInterfaceClass(), request.getMethodName(), response);
            }

            respMsg.setBizTime(bizSpendTime);

            IpcMessageResponse ipcMessageResponse = new IpcMessageResponse();
            ipcMessageResponse.setData(response);

            try{
                respMsg.serializeResponse(serializerType, ipcMessageResponse);
            }catch (Exception e){
                RuntimeLogger.error(ErrorCode.RESPONSE_SERIALIZER_ERROR.getMessage(), e);
                ipcMessageResponse = new IpcMessageResponse();
                ipcMessageResponse.setErrorCode(ErrorCode.RESPONSE_SERIALIZER_ERROR.getCode());
                ipcMessageResponse.setErrorMsg(e.getMessage());

                respMsg.serializeResponse(serializerType, ipcMessageResponse);
                return respMsg;
            }

            return respMsg;
        } catch (Exception e) {
            RuntimeLogger.error(ErrorCode.SERVICE_INVOKE_ERROR.getMessage(), e);
            IpcMessageResponse ipcMessageResponse = new IpcMessageResponse();
            ipcMessageResponse.setErrorCode(ErrorCode.SERVICE_INVOKE_ERROR.getCode());
            ipcMessageResponse.setErrorMsg(e.getMessage());

            try{
                respMsg.serializeResponse(serializerType, ipcMessageResponse);
                return respMsg;
            }catch (Exception exception){
                return respMsg;
            }

        } finally {
            ThreadLocalContent.clear();
        }
    }

    /**
     * 执行业务逻辑
     */
    private Object doInvokeService(String serviceUniqueKey, String methodName, List<Type> typeList, Object[] args) throws Exception {
        // 实际业务处理逻辑
        Object service = ServiceCache.serviceBeanCacheMap.get(serviceUniqueKey);

        // 解析参数类型
        if (args == null || args.length == 0) {
            // 获取方法并调用
            Method method = service.getClass().getMethod(methodName);
            return method.invoke(service);
        }else{
//            Class<?>[] argTypes = new Class[args.length];
//            for (int i = 0; i < args.length; i++) {
//                argTypes[i] = args[i].getClass();
//            }
            // 获取方法并调用
            Method method = service.getClass().getMethod(methodName, getRawTypes(typeList));
            return method.invoke(service, args);
        }

    }

    // 提取原始类型用于方法查找
    private Class<?>[] getRawTypes(List<Type> types) {
        return types.stream()
                .map(type -> {
                    if (type instanceof Class) return (Class<?>) type;
                    if (type instanceof ParameterizedType)
                        return (Class<?>) ((ParameterizedType) type).getRawType();
                    return Object.class; // 兜底处理
                })
                .toArray(Class<?>[]::new);
    }

}
