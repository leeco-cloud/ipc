package com.lee.ipc.common.client;

import com.lee.ipc.common.communication.client.IpcClient;
import com.lee.ipc.common.communication.server.ServiceBean;
import com.lee.ipc.common.constant.MessageType;
import com.lee.ipc.common.exception.ErrorCode;
import com.lee.ipc.common.exception.IpcRuntimeException;
import com.lee.ipc.common.protocol.IpcMessageResponse;
import com.lee.ipc.common.register.RegistryLocalCenter;
import com.lee.ipc.common.serialization.common.SerializerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceBean implements InvocationHandler{

    /**
     * 版本号
     */
    private String version = "1.0.0";

    /**
     * 自定义标签，可以是字符串，可以是环境变量
     */
    private String[] tags;

    /**
     * 指定请求超时时间，单位:毫秒，默认5秒
     */
    private Integer timeout = 5000;

    /**
     * 接口声明类
     */
    private Class<?> serviceInterface;

    /**
     * 序列化类型，默认FURY
     */
    private SerializerType serializerType = SerializerType.FURY;

    /**
     * 服务唯一编码
     */
    private String serviceUniqueKey;

    /**
     * 代理对象
     */
    private Object proxy;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return doInvoke(method, args, serviceInterface, serializerType, serviceUniqueKey, timeout);
    }

    public static Object doInvoke(Method method, Object[] args, Class<?> serviceInterface, SerializerType serializerType, String serviceUniqueKey, Integer timeout) {
        List<ServiceBean> serviceBeans = RegistryLocalCenter.serviceUniqueKeyServiceMap.get(serviceUniqueKey);
        if (CollectionUtils.isEmpty(serviceBeans)) {
            throw new IpcRuntimeException(ErrorCode.CLIENT_UN_READY, serviceInterface);
        }
        ServiceBean serviceBean = serviceBeans.get(0);
        return doInvokeByContainer(serviceBean.getContainerName(), method, args, serviceInterface, serializerType, serviceUniqueKey, timeout);

    }

    public static Object doInvokeByContainer(String containerName, Method method, Object[] args, Class<?> serviceInterface, SerializerType serializerType, String serviceUniqueKey, Integer timeout) {
        IpcClient ipcClient = IpcClient.allClients.get(containerName);
        if (ipcClient == null) {
            throw new IpcRuntimeException(ErrorCode.CLIENT_UN_READY, serviceInterface);
        }

        Type[] genericTypes = method.getParameterTypes();

        IpcMessageResponse ipcMessageResponse = ipcClient.ipcClientInvoke.sendRequest(serviceUniqueKey, serviceInterface, serializerType, MessageType.NORMAL, method.getName(), List.of(genericTypes), args, timeout);
        return ipcMessageResponse.getData();
    }

}
