package io.github.lingnanlu;

import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.github.lingnanlu.spi.RpcInvoker;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by rico on 2017/1/10.
 */
public class RpcInvocationHandler implements InvocationHandler {

    @Getter @Setter private RpcInvoker invoker;
    public RpcInvocationHandler(RpcInvoker invoker) {
        this.invoker = invoker;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Class<?> rpcInterface = method.getDeclaringClass();
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] parameters = args;

        RpcMessage req = RpcMessages.newRequestRpcMessage(rpcInterface, methodName, parameterTypes, parameters);

        RpcMessage rsp = invoker.invoke(req);

        return RpcMessages.unpackResponseMessage(rsp);
    }
}
