package com.lee.ipc.starter.controller.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yanhuai lee
 */

public class ProviderProtocol implements ProviderProtocolInterface {

    private Integer id = 2;

    private String name = "heiheihei";

    private Map<String, Object> param;

    public ProviderProtocol(){
        param = new ConcurrentHashMap<>();
        param.put("bbb","bbb");
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
