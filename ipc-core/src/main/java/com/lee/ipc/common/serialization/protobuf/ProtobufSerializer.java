package com.lee.ipc.common.serialization.protobuf;

import com.google.protobuf.ByteString;
import com.lee.ipc.common.protocol.IpcMessageRequest;
import com.lee.ipc.common.protocol.IpcMessageResponse;
import com.lee.ipc.common.serialization.SerializationFactory;
import com.lee.ipc.common.serialization.Serializer;
import com.lee.ipc.common.serialization.protobuf.request.IpcMessageRequestProto;
import com.lee.ipc.common.serialization.protobuf.response.IpcMessageResponseProto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * protobuf序列化
 * @author yanhuai lee
 */
public class ProtobufSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) throws Exception {
        if (obj instanceof IpcMessageRequest){
            // 请求序列化
            IpcMessageRequest messageRequest = (IpcMessageRequest) obj;

            IpcMessageRequestProto.Builder builder =
                    IpcMessageRequestProto.newBuilder()
                            .setServiceUniqueKey(messageRequest.getServiceUniqueKey())
                            .setInterfaceClass(messageRequest.getInterfaceClass().getName())
                            .setMethodName(messageRequest.getMethodName());

            // 序列化方法入参类型数组
            if (messageRequest.getParameterTypes() != null) {
                for (Type type : messageRequest.getParameterTypes()) {
                    builder.addParameterTypes(ByteString.copyFromUtf8(type.getTypeName()));
                }
            }

            // 序列化参数数组
            if (messageRequest.getArgs() != null) {
                for (Object arg : messageRequest.getArgs()) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(arg);
                    oos.close();
                    builder.addArgs(ByteString.copyFrom(bos.toByteArray()));
                }
            }

            // 序列化用户数据
            if (messageRequest.getUserData() != null) {
                for (Map.Entry<String, Object> entry : messageRequest.getUserData().entrySet()) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(entry.getValue());
                    oos.close();
                    builder.putUserData(entry.getKey(), ByteString.copyFrom(bos.toByteArray()));
                }
            }

            return builder.build().toByteArray();
        }else{
            // 响应序列化
            IpcMessageResponse messageResponse = (IpcMessageResponse) obj;

            IpcMessageResponseProto.Builder builder = IpcMessageResponseProto.newBuilder();

            if (messageResponse.getErrorCode() != null){
                builder.setErrorCode(messageResponse.getErrorCode());
            }
            if (messageResponse.getErrorMsg() != null){
                builder.setErrorMsg(messageResponse.getErrorMsg());
            }

            if (messageResponse.getData() != null){
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(messageResponse.getData());
                oos.close();
                builder.setData(ByteString.copyFrom(bos.toByteArray()));
            }

            return builder.build().toByteArray();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        if (clazz.equals(IpcMessageRequest.class)) {
            // 请求
            IpcMessageRequestProto proto = IpcMessageRequestProto.parseFrom(bytes);

            Class<?> interfaceClass = Class.forName(proto.getInterfaceClass());

            List<Type> typeList = null;
            List<ByteString> parameterTypesList = proto.getParameterTypesList();
            if (!parameterTypesList.isEmpty()){
                typeList = new ArrayList<>();
                for (ByteString typeBytes : proto.getParameterTypesList()) {
                    String typeName = typeBytes.toStringUtf8();
                    // 类型解析
                    Class<?> type = Class.forName(typeName);
                    typeList.add(type);
                }
            }

            // 反序列化参数
            Object[] args = null;
            if (proto.getArgsCount() > 0){
                args = new Object[proto.getArgsCount()];
                for (int i = 0; i < proto.getArgsCount(); i++) {
                    byte[] byteArray = proto.getArgs(i).toByteArray();
                    if (byteArray == null || byteArray.length == 0) {
                        continue;
                    }
                    try (ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
                         ObjectInputStream ois = new ObjectInputStream(bis)) {
                        args[i] = ois.readObject();
                    }
                }
            }

            // 创建请求对象
            IpcMessageRequest request = new IpcMessageRequest(
                    proto.getServiceUniqueKey(),
                    interfaceClass,
                    proto.getMethodName(),
                    typeList,
                    args
            );

            // 反序列化用户数据
            Map<String, Object> userData = new ConcurrentHashMap<>();
            if (!proto.getUserDataMap().isEmpty()){
                for (Map.Entry<String, ByteString> entry : proto.getUserDataMap().entrySet()) {
                    ObjectInputStream ois = new ObjectInputStream(
                            new ByteArrayInputStream(entry.getValue().toByteArray())
                    );
                    userData.put(entry.getKey(), ois.readObject());
                }
            }
            request.setUserData(userData);

            return (T) request;
        }else{
            IpcMessageResponseProto proto = IpcMessageResponseProto.parseFrom(bytes);

            Object data = null;
            if (!proto.getData().isEmpty()){
                try (ByteArrayInputStream bis = new ByteArrayInputStream(proto.getData().toByteArray());
                     ObjectInputStream ois = new ObjectInputStream(bis)) {
                    data = ois.readObject();
                }
            }

            IpcMessageResponse response = new IpcMessageResponse(data,proto.getErrorCode(),proto.getErrorMsg());

            return (T) response;
        }
    }

}
