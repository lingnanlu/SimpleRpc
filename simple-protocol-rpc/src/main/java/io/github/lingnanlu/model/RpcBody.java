package io.github.lingnanlu.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rico on 2017/1/11.
 */
public class RpcBody {

    @Getter @Setter private Class<?> rpcInterface;
    @Getter @Setter private RpcMethod rpcMethod;
    @Getter @Setter private Object returnObject;

}
