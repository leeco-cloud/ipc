package com.lee.ipc.common.server;

import com.lee.ipc.common.AutoConfiguration;
import com.lee.ipc.common.annotation.IpcProvider;
import com.lee.ipc.common.cache.ServiceCache;
import com.lee.ipc.common.communication.server.ServiceBean;
import com.lee.ipc.common.exception.ErrorCode;
import com.lee.ipc.common.exception.IpcBootException;
import com.lee.ipc.common.serialization.common.SerializerType;
import com.lee.ipc.common.util.IpcServerNameGenerationsUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 服务提供者进行BPP扫描和注册
 * @author yanhuai lee
 */
@Component
public class ProviderAnnotationBeanPostProcessor implements BeanPostProcessor, BeanDefinitionRegistryPostProcessor, ApplicationEventPublisherAware, EnvironmentAware {

    public static Environment environment;

    public static BeanDefinitionRegistry registry;

    public static ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        IpcProvider annotation = bean.getClass().getAnnotation(IpcProvider.class);
        if (annotation != null) {
            String version = annotation.version();
            String[] tags = annotation.tags();
            Class<?> serviceInterface = annotation.serviceInterface();
            if (serviceInterface == Object.class) {
                Class<?>[] interfaces = bean.getClass().getInterfaces();
                if (interfaces.length > 1) {
                    throw new IpcBootException(ErrorCode.BOOT_TOO_MUCH_SERVICE_INTERFACE, beanName);
                }
                serviceInterface = interfaces[0];
            }
            SerializerType serializerType = annotation.serializerType();

            String serviceUniqueKey = IpcServerNameGenerationsUtils.generalServiceUniqueKey(version, tags, serviceInterface, serializerType, environment);

            ServiceBean existServiceBean = ServiceCache.serviceCacheMap.get(serviceUniqueKey);
            if (existServiceBean != null){
                throw new IpcBootException(ErrorCode.BOOT_TOO_MUCH_SERVICE_PROVIDER, serviceInterface.getName());
            }

            String containerName = AutoConfiguration.getContainerName(environment);
            ServiceBean serviceBean = new ServiceBean(version, tags, serviceInterface, serializerType, beanName, serviceUniqueKey, containerName);

            ServiceCache.serviceCacheMap.put(serviceUniqueKey, serviceBean);
            ServiceCache.serviceBeanCacheMap.put(serviceUniqueKey, bean);

            applicationEventPublisher.publishEvent(new ServiceBeanExportEvent(serviceBean, environment));
        }

        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 注册需要依赖的 Bean
        ProviderAnnotationBeanPostProcessor.registry = registry;
    }

//    public static void registerDependentBean(Object bean) {
//        BeanDefinition beanDefinition = BeanDefinitionBuilder
//                .genericBeanDefinition(clazz)
//                .addConstructorArgReference("customService") // 引用其他 Bean
//                .getBeanDefinition();
//
//        registry.registerBeanDefinition("dependentService", beanDefinition);
//    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        // ignore
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        ProviderAnnotationBeanPostProcessor.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void setEnvironment(Environment environment) {
        ProviderAnnotationBeanPostProcessor.environment = environment;
    }
}
