package com.lee.ipc.common;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@Configurable
@ComponentScan(basePackages = {"com.lee.ipc.common"})
public class AutoConfiguration {

    public static String getContainerName(Environment environment){
        return environment.getProperty("ipc.container.name", environment.getProperty("spring.application.name", "default"));
    }

    public static String getLocalRegisterCenterPath(Environment environment){
        return environment.getProperty("ipc.local.register", System.getProperty("java.io.tmpdir"));
    }

}
