package com.lee.ipc.common.communication.client;

import com.lee.ipc.common.communication.support.ThreadLocalContent;
import com.lee.ipc.common.constant.MessageType;
import com.lee.ipc.common.exception.IpcRuntimeException;
import com.lee.ipc.common.monitor.MonitorSupport;
import com.lee.ipc.common.protocol.IpcMessage;
import com.lee.ipc.common.protocol.IpcMessageRequest;
import com.lee.ipc.common.protocol.IpcMessageResponse;
import com.lee.ipc.common.protocol.ResponseFuture;
import com.lee.ipc.common.serialization.common.SerializerType;
import com.lee.ipc.common.spi.SpiLoader;
import com.lee.ipc.common.spi.invoke.IpcInvokeSpi;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * uds执行器
 * @author yanhuai lee
 */
@NoArgsConstructor
@AllArgsConstructor
public class IpcClientInvoke {

    private Channel channel;

    private static final AtomicLong idGenerator = new AtomicLong(1);

    public IpcMessageResponse sendRequest(String serviceUniqueKey, Class<?> serviceInterface, SerializerType serializerType,
                                          MessageType messageType, String methodName, Object[] args, Integer timeout) {
        // 生成唯一请求ID
        Long requestId = idGenerator.getAndIncrement();
        try{
            IpcMessage ipcMessage = new IpcMessage(requestId, serializerType.getType(), messageType.getMessageTypeCode());

            // 拦截器链SPI 前置处理器
            List<IpcInvokeSpi> ipcInvokeSpi = SpiLoader.loadIpcInvokeSpi();
            for (IpcInvokeSpi invokeSpi : ipcInvokeSpi) {
                args = invokeSpi.beforeInvoke(serviceInterface, methodName, args);
            }

            IpcMessageRequest ipcMessageRequest = new IpcMessageRequest(serviceUniqueKey, serviceInterface, methodName, args);

            // 用户自定义数据
            Map<String, Object> allUserData = ThreadLocalContent.getAllUserData();
            if (!CollectionUtils.isEmpty(allUserData)) {
                ipcMessageRequest.setUserData(allUserData);
            }

            // 序列化请求数据
            ipcMessage.serializeRequest(serializerType, ipcMessageRequest);

            ResponseFuture responseFuture = new ResponseFuture(ipcMessage.getRequestId());

            // 发送IPC请求
            IpcMessageResponse ipcMessageResponse = doSendIpc(ipcMessage, serializerType, responseFuture, timeout);

            Object data = ipcMessageResponse.getData();

            // 拦截器链 SPI 后置处理器
            for (IpcInvokeSpi invokeSpi : ipcInvokeSpi) {
                data = invokeSpi.afterInvoke(serviceInterface, methodName, data);
            }
            ipcMessageResponse.setData(data);

            // 上报完整链路监控数据 monitor
            MonitorSupport.recordMetrics(requestId);

            return ipcMessageResponse;
        }catch (Exception exception){
            throw new IpcRuntimeException(exception);
        }finally {
            ThreadLocalContent.clear();
            MonitorSupport.clear(requestId);
        }
    }

    private IpcMessageResponse doSendIpc(IpcMessage ipcMessage, SerializerType serializerType, ResponseFuture responseFuture, Integer timeout) throws Exception {

        channel.eventLoop().execute(()-> channel.writeAndFlush(ipcMessage));

        try{
            IpcMessage response = responseFuture.get(timeout, TimeUnit.MILLISECONDS);
            MonitorSupport.addRecord(response.getRequestId(), response);
            return response.deserializeResponse(serializerType);
        }catch (TimeoutException timeoutException){
            // 请求超时
//            return response.deserializeErrorResponse(serializerType);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void doMonitor(Long requestId, Map<String, Long> monitorData) {
        // todo 上报完整链路监控数据 monitor
        try{

        }catch (Exception exception){
            // ignore
        }
    }

}
