package com.lee.ipc.common.api;

import com.lee.ipc.common.client.ReferenceBean;
import com.lee.ipc.common.communication.server.ServiceBean;
import com.lee.ipc.common.register.RegistryLocalCenter;
import com.lee.ipc.common.serialization.common.SerializerType;
import com.lee.ipc.common.util.IpcServerNameGenerationsUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 客户端API
 * @author yanhuai lee
 */
public class ClientApi {

    public static List<ServiceBean> allIpcService(){
        Map<String, List<ServiceBean>> containerServiceMap = RegistryLocalCenter.containerServiceMap;
        if (!containerServiceMap.isEmpty()) {
            return containerServiceMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
        }
        return null;
    }

    public static Object invoke(Class<?> serviceInterface, Method method, Object[] args, String version, SerializerType serializerType, Integer timeout) {
        String serviceUniqueKey = IpcServerNameGenerationsUtils.generalServiceUniqueKey(version, null, serviceInterface, serializerType, null);
        return ReferenceBean.doInvoke(null, method, args, serviceInterface, serializerType, serviceUniqueKey, timeout);
    }

}
