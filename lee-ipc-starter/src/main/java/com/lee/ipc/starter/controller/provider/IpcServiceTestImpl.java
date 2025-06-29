package com.lee.ipc.starter.controller.provider;

import com.lee.ipc.common.annotation.IpcProvider;

@IpcProvider
public class IpcServiceTestImpl implements IpcServiceTest{

    @Override
    public void test() {
        System.out.println("test");
    }
}
