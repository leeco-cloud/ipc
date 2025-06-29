package com.lee.ipc.starter.controller.custom;

import com.lee.ipc.common.annotation.IpcConsumer;
import com.lee.ipc.starter.controller.provider.IpcServiceTest;
import org.springframework.stereotype.Component;

/**
 * @author yanhuai lee
 */
@Component
public class Customer {

    @IpcConsumer
    private IpcServiceTest ipcServiceTest;

    public void test(){
        ipcServiceTest.test();
    }

}
