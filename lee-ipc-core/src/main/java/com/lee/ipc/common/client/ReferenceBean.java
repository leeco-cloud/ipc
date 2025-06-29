package com.lee.ipc.common.client;

import com.lee.ipc.common.communication.client.IpcClient;
import com.lee.ipc.common.constant.MessageType;
import com.lee.ipc.common.protocol.IpcMessageResponse;
import com.lee.ipc.common.serialization.common.SerializerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        IpcMessageResponse ipcMessageResponse = IpcClient.ipcClientInvoke.sendRequest(SerializerType.FURY, MessageType.NORMAL, method.getName(), args);
        return ipcMessageResponse.getData();
    }

}
