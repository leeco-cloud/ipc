package com.lee.ipc.starter.controller;

import com.alibaba.fastjson.JSON;
import com.lee.ipc.common.api.ClientApi;
import com.lee.ipc.common.api.ServerApi;
import com.lee.ipc.common.communication.server.IpcServer;
import com.lee.ipc.common.communication.server.ServiceBean;
import com.lee.ipc.common.serialization.common.SerializerType;
import com.lee.ipc.starter.controller.api.TestApi;
import com.lee.ipc.starter.controller.api.TestApiImpl;
import com.lee.ipc.starter.controller.custom.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class BasicController {

    @Resource
    private Customer customer;

    // http://127.0.0.1:8080/hello
    @RequestMapping("/hello")
    @ResponseBody
    public void hello() {
        customer.test();
    }

    @RequestMapping("/api")
    @ResponseBody
    public void api() throws NoSuchMethodException {
        ServerApi.registerServer(TestApi.class, new TestApiImpl(), "1.0.0", SerializerType.FURY);

        List<ServiceBean> serviceBeans = ClientApi.allIpcService();
        for (ServiceBean serviceBean : serviceBeans) {
            System.out.println(JSON.toJSONString(serviceBean));
        }

        Object invoke = ClientApi.invoke(TestApi.class, TestApi.class.getMethod("hello", String.class), new String[]{"aaa"}, "1.0.0", SerializerType.FURY, 5000);
        System.out.println(invoke);
    }

}
