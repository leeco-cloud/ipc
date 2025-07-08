![img](https://my-resource.oss-cn-hangzhou.aliyuncs.com/image/IPC%E5%AE%8C%E6%95%B4%E6%9E%B6%E6%9E%84%E5%9B%BE.png)



## 1.传送门：
[技术方案](https://leenotes.cn/posts/45495.html#4-%E6%8A%80%E6%9C%AF%E8%B0%83%E7%A0%94%E5%92%8C%E9%80%89%E5%9E%8B)



## 2.接入方式

### 2.1服务注册

```
/**
* version（非必填）:版本号, 默认1.0.0
* tag（非必填）: 自定义标签，可以是字符串，可以是环境变量
* serviceInterface（非必填）:接口，当上层接口超过2个，需要指定serviceInterface
* serializationType（非必填）: 序列化类型，默认FURY
*/
@IpcProvider(version = "1.0", tags = {"abc","${env}"}, serviceInterface = TestApi.class, serializationType = SerializationType.FURY)
public class TestApiImpl implements TestApi{
    // ...
}
```



### 2.2 服务发现

```
/**
* version（非必填）:版本号
* tag（非必填）: 自定义标签，可以是字符串，可以是环境变量
* timeout（非必填）:指定请求超时时间，单位:毫秒，默认20秒
* serializationType（非必填）: 序列化类型，默认FURY
*/
@IpcConsumer(version = "1.0", tags = {"abc","${env}"}, timeout = 5000, serializationType = SerializationType.FURY)
private TestApi testApi;
```


### 2.3 配置
```
// 容器名/应用名
ipc.container.name=ips-starter
// 注册中心目录
ipc.local.register=/Users/lee/ipc/register
// IPC框架日志目录
ipc.log.base.path=/Users/lee/ipc/log
```

### 2.4 扩展点

#### 2.4.1 用户数据
当每一次请求的时候 用户需要透传个性化数据 到服务端 可以使用：

```
// 客户端：
com.lee.ipc.common.communication.support.UserDataSupport.putAllUserData()

// 服务端：
com.lee.ipc.common.communication.support.UserDataSupport.putAllUserData()
```

#### 2.4.2 拦截器AOP
当每一次请求的时候 用户需要对请求前后做AOP处理 可以使用：

```
// 1. 创建SPI声明文件：resources/META-INF/services/com.lee.ipc.common.spi.invoke.IpcInvokeSpi
// 2. 实现 com.lee.ipc.common.spi.invoke.IpcInvokeSpi 接口
public interface IpcInvokeSpi {

    Object[] beforeInvoke(Class<?> serviceInterface, String methodName, Object[] args);

    Object afterInvoke(Class<?> serviceInterface, String methodName, Object args);

}
```

#### 2.4.3 容器生命周期SPI

```
// 1. 创建SPI声明文件：resources/META-INF/services/com.lee.ipc.common.spi.container.ContainerLifeSpi
// 2. 实现 com.lee.ipc.common.spi.container.ContainerLifeSpi 接口
public interface ContainerLifeSpi {

    void start();

	void stop();

	boolean isRunning();
    
    default void restart(){
        stop();
        start();
    }

}
```

#### 2.4.4 监控上报SPI
每一次完成请求之后 都会触发调用监控数据上报：

```
// 1. 创建SPI声明文件：resources/META-INF/services/com.lee.ipc.common.spi.monitor.IpcMonitorSpi
// 2. 实现 com.lee.ipc.common.spi.monitor.IpcMonitorSpi 接口
public interface IpcMonitorSpi {

    Object[] beforeInvoke(Class<?> serviceInterface, String methodName, Object[] args);

    Object afterInvoke(Class<?> serviceInterface, String methodName, Object args);

}
```

#### 2.4.5 手动服务上报
```java
ServerApi.registerServer(Class<?> serviceInterface, Object instance, String version, SerializerType serializerType);
```

#### 2.4.5 手动服务调用
```java
ClientApi.invoke(Class<?> serviceInterface, Method method, Object[] args, String version, SerializerType serializerType, Integer timeout);
```

## 3. todo

### 3.1 模块隔离：sofa-ark

### 3.2 IPC服务降级成RPC

