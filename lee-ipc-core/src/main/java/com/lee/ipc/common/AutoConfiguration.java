package com.lee.ipc.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@Configurable
@ComponentScan(basePackages = {"com.lee.ipc.common"})
public class AutoConfiguration {

    public static String currentContainerPort;

    @Autowired
    private Environment environment;

    public String getCurrentPort(){
        return environment.getProperty("server.port");
    }

}
