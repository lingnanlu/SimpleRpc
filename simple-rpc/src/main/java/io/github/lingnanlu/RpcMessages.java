package io.github.lingnanlu;

import io.craft.atom.protocol.rpc.model.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by rico on 2017/1/10.
 */
public class RpcMessages {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    private static RpcMessage newRpcMessage() {
        RpcMessage rm = new RpcMessage();
        RpcHeader rh = new RpcHeader();
        RpcBody rb = new RpcBody();
        rm.setHeader(rh);
        rm.setBody(rb);
        return rm;
    }

    public static RpcMessage newRequestRpcMessage(Class<?> rpcInterface, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        RpcMessage req = newRpcMessage();

        req.setId(ID_GENERATOR.incrementAndGet());

        RpcBody body = req.getBody();
        body.setRpcInterface(rpcInterface);
        body.setRpcOption(new RpcOption());
        RpcMethod method = new RpcMethod();
        method.setName(methodName);
        method.setParameterTypes(parameterTypes);
        method.setParameters(parameterTypes);
        body.setRpcMethod(method);
        return req;
    }

    public static Object unpackResponseMessage(RpcMessage rsp) {

        return rsp.getReturnObject();
    }

    public static RpcMessage newRsponseRpcMessage(long id, Object returnObject) {
        return null;
    }
}
