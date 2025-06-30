package com.lee.ipc.common.client;

import com.lee.ipc.common.annotation.IpcConsumer;
import com.lee.ipc.common.cache.ReferenceCache;
import com.lee.ipc.common.serialization.common.SerializerType;
import com.lee.ipc.common.util.IpcServerNameGenerationsUtils;
import com.lee.ipc.common.util.ProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * 服务订阅者进行BPP扫描 生成代理
 * @author yanhuai lee
 */
@Component
public class ReferenceAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, ApplicationContextAware, ApplicationEventPublisherAware {

    public static ApplicationContext applicationContext;

    public static ApplicationEventPublisher applicationEventPublisher;

    @Override
    public PropertyValues postProcessProperties(
            @NonNull PropertyValues pvs,
            @NonNull Object bean,
            @NonNull String beanName) throws BeansException {

        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (field.isAnnotationPresent(IpcConsumer.class) && field.getType().isInterface()) {

                IpcConsumer annotation = field.getAnnotation(IpcConsumer.class);
                String version = annotation.version();
                String[] tags = annotation.tags();
                Integer timeout = annotation.timeout();
                SerializerType serializerType = annotation.serializerType();

                Environment environment = applicationContext.getEnvironment();

                String serviceUniqueKey = IpcServerNameGenerationsUtils.generalServiceUniqueKey(version, tags, field.getType(), serializerType, environment);

                ReferenceBean referenceBean = getReferenceBean(version, tags, timeout, field.getType(), serializerType, serviceUniqueKey);

                Object proxy = getOrCreateProxy(referenceBean);

                field.setAccessible(true);
                field.set(bean, proxy);

                applicationEventPublisher.publishEvent(new IpcConsumeReadyEvent(referenceBean, environment));
            }
        });

        return pvs;
    }

    private ReferenceBean getReferenceBean(String version, String[] tags, Integer timeout, Class<?> serviceInterface, SerializerType serializerType, String serviceUniqueKey) {
        if (ReferenceCache.referenceCacheMap.containsKey(serviceUniqueKey)) {
            return ReferenceCache.referenceCacheMap.get(serviceUniqueKey);
        }

        ReferenceBean referenceBean = new ReferenceBean(version, tags, timeout, serviceInterface, serializerType, serviceUniqueKey, null);
        // 加入缓存
        ReferenceCache.referenceCacheMap.put(referenceBean.getServiceUniqueKey(), referenceBean);

        return referenceBean;
    }

    private Object getOrCreateProxy(ReferenceBean referenceBean) {
        if (ReferenceCache.referenceProxyCacheMap.containsKey(referenceBean.getServiceUniqueKey())) {
            return ReferenceCache.referenceProxyCacheMap.get(referenceBean.getServiceUniqueKey());
        }

        // 创建新代理
        Object proxy = ProxyUtils.getProxy(referenceBean.getServiceInterface(), referenceBean);

        referenceBean.setProxy(proxy);

        // 注册到容器
        registerSingleton(referenceBean.getServiceUniqueKey(), proxy);

        // 加入缓存
        ReferenceCache.referenceProxyCacheMap.put(referenceBean.getServiceUniqueKey(), proxy);

        return proxy;
    }

    private void registerSingleton(String beanName, Object proxy) {
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        if (autowireCapableBeanFactory instanceof DefaultSingletonBeanRegistry) {
            synchronized (this) {
                DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) autowireCapableBeanFactory;
                if (!registry.containsSingleton(beanName)) {
                    registry.registerSingleton(beanName, proxy);
                }
            }
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        ReferenceAnnotationBeanPostProcessor.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ReferenceAnnotationBeanPostProcessor.applicationContext = applicationContext;
    }

}
