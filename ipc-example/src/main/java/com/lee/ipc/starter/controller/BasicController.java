package com.lee.ipc.starter.controller;

import com.lee.ipc.common.communication.server.IpcServer;
import com.lee.ipc.starter.controller.custom.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

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

}
