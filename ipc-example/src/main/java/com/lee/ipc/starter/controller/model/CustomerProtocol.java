package com.lee.ipc.starter.controller.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yanhuai lee
 */

public class CustomerProtocol implements CustomerProtocolInterface {

    private Integer id = 1;

    private String name = "hahaha";

    private Map<String, Object> param;

    public CustomerProtocol(){
        param = new ConcurrentHashMap<>();
        param.put("aaa","aaa");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}
