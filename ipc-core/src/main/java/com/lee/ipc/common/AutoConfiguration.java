package com.lee.ipc.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@Configurable
@ComponentScan(basePackages = {"com.lee.ipc.common"})
public class AutoConfiguration {

    @Value("${custom.logging.base-path:}")
    private String basePath;

    @PostConstruct
    public void setSystemProperties() {
        if (StringUtils.isEmpty(basePath)) {
            basePath = System.getProperty("user.dir");
        }
        System.setProperty("ipc.log.base.path", basePath);
    }

    public static String getContainerName(Environment environment){
        return environment.getProperty("ipc.container.name", environment.getProperty("spring.application.name", "default"));
    }

    public static String getLocalRegisterCenterPath(Environment environment){
        return environment.getProperty("ipc.local.register", System.getProperty("java.io.tmpdir"));
    }

}
