package com.lee.ipc.starter.controller.custom;

import com.alibaba.fastjson.JSON;
import com.lee.ipc.common.annotation.IpcConsumer;
import com.lee.ipc.starter.controller.model.CustomerProtocol;
import com.lee.ipc.starter.controller.model.ProviderProtocolInterface;
import com.lee.ipc.starter.controller.provider.IpcServiceTest;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yanhuai lee
 */
@Component
public class Customer {

    @IpcConsumer
    private IpcServiceTest ipcServiceTest;

    public void test(){
        Map<String, Object> param = new ConcurrentHashMap<>();
        param.put("name", "LEE");

        Map<String, Object> header = new ConcurrentHashMap<>();
        header.put("cookie", "123");
        com.lee.ipc.common.communication.support.UserDataSupport.putAllUserData(header);

        ProviderProtocolInterface client = ipcServiceTest.test("client", param, new CustomerProtocol());
        System.out.println(JSON.toJSONString(client));
    }

}
