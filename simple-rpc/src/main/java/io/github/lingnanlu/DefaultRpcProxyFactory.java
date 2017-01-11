package io.github.lingnanlu;

import io.github.lingnanlu.spi.RpcInvoker;
import io.github.lingnanlu.spi.RpcProxyFactory;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Proxy;

/**
 * Created by rico on 2017/1/10.
 */
public class DefaultRpcProxyFactory implements RpcProxyFactory {

    @Getter @Setter private RpcInvoker invoker;

    public <T> T getProxy(Class<T> rpcInterface) {
        return (T)Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{rpcInterface},
                new RpcInvocationHandler(invoker)
        );
    }

}
