package io.github.lingnanlu.spi;

/**
 * Created by rico on 2017/1/10.
 */
public interface RpcProxyFactory {

    <T> T getProxy(Class<T> rpcInterface);

    void setInvoker(RpcInvoker invoker);

}
