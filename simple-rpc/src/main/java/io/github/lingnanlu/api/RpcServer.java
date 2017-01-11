package io.github.lingnanlu.api;

/**
 * Created by rico on 2017/1/10.
 */
public interface RpcServer {

    void open();
    void close();
    void export(Class<?> rpcInterface, Object rpcObject, RpcParameter rpcParameter);
    void export(String rpcId, Class<?> rpcInterface, String rpcMethodName, Class<?>[] rpcMethodParameterTypes, Object rpcObject, RpcParameter rpcParameter);
    void unexport(Class<?> rpcInterface);

}
