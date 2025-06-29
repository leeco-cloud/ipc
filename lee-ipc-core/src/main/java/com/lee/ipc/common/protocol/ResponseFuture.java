package com.lee.ipc.common.protocol;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yanhuai lee
 */
public class ResponseFuture extends CompletableFuture<IpcMessage> {

    public static final ConcurrentHashMap<Long, ResponseFuture> futures = new ConcurrentHashMap<>();

    public ResponseFuture(Long requestId){
        futures.put(requestId, this);
    }

    public void success(IpcMessage msg) {
        ResponseFuture future = futures.remove(msg.getRequestId());
        if(future != null){
            future.complete(msg);
        }
    }

}
