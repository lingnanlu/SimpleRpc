package io.github.lingnanlu;

import io.craft.atom.protocol.rpc.model.RpcMethod;
import io.github.lingnanlu.api.RpcParameter;
import io.github.lingnanlu.spi.RpcApi;
import lombok.Getter;

/**
 * Created by rico on 2017/1/11.
 */
public class DefaultRpcApi implements RpcApi {


    @Getter  private String key;
    @Getter  private String name;
    @Getter  private String rpcId;
    @Getter  private Class<?> rpcInterface;
    @Getter  private RpcMethod rpcMethod;
    @Getter  private Object rpcObject;
    @Getter  private RpcParameter rpcParameter;


    public DefaultRpcApi(String rpcId, Class<?> rpcInterface, RpcMethod rpcMethod) {
        this.key = key;
        this.name = name;
        this.rpcId = rpcId;
        this.rpcInterface = rpcInterface;
        this.rpcMethod = rpcMethod;
    }

    public DefaultRpcApi(String key, String name, String rpcId, Class<?> rpcInterface, RpcMethod rpcMethod, Object rpcObject, RpcParameter rpcParameter) {
        this.key = key;
        this.name = name;
        this.rpcId = rpcId;
        this.rpcInterface = rpcInterface;
        this.rpcMethod = rpcMethod;
        this.rpcObject = rpcObject;
        this.rpcParameter = rpcParameter;
    }

    public DefaultRpcApi(String rpcId, Class<?> rpcInterface, RpcMethod rpcMethod, Object rpcObject, RpcParameter rpcParameter) {
    }

    public String getId() {
        return null;
    }

    public Class<?> getInterface() {
        return null;
    }

    public String getMethodName() {
        return null;
    }

    public Class<?>[] getMethodParameterTypes() {
        return new Class<?>[0];
    }
}
