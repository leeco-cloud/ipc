package com.lee.ipc.starter.controller.provider;

import com.lee.ipc.starter.controller.model.CustomerProtocolInterface;
import com.lee.ipc.starter.controller.model.ProviderProtocolInterface;

import java.util.Map;

public interface IpcServiceTest {

    ProviderProtocolInterface test(String name, Map<String, Object> param, CustomerProtocolInterface client);

}
