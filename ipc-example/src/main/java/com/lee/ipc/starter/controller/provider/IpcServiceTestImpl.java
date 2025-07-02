package com.lee.ipc.starter.controller.provider;

import com.alibaba.fastjson.JSON;
import com.lee.ipc.common.annotation.IpcProvider;
import com.lee.ipc.starter.controller.model.CustomerProtocolInterface;
import com.lee.ipc.starter.controller.model.ProviderProtocol;
import com.lee.ipc.starter.controller.model.ProviderProtocolInterface;

import java.util.Map;

@IpcProvider
public class IpcServiceTestImpl implements IpcServiceTest{

    @Override
    public ProviderProtocolInterface test(String name, Map<String, Object> param, CustomerProtocolInterface client) {
        System.out.println(name);
        System.out.println(JSON.toJSON(param));
        System.out.println(client.getClass().getName());

        System.out.println(com.lee.ipc.common.communication.support.UserDataSupport.getAllUserData());

        return new ProviderProtocol();
    }
}
