package com.lee.ipc.starter.controller.api;

/**
 * @author yanhuai lee
 */
public class TestApiImpl implements TestApi {

    @Override
    public String hello(String name) {
        return name + " hello";
    }

}
