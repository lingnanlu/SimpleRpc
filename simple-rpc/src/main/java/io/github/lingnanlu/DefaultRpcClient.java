package io.github.lingnanlu;

import io.github.lingnanlu.api.RpcClient;
import io.github.lingnanlu.spi.RpcConnector;
import io.github.lingnanlu.spi.RpcInvoker;
import io.github.lingnanlu.spi.RpcProtocol;
import io.github.lingnanlu.spi.RpcProxyFactory;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rico on 2017/1/10.
 */
public class DefaultRpcClient implements RpcClient {


    @Getter @Setter private String host;
    @Getter @Setter private int port;
    @Getter @Setter private int connections;
    @Getter @Setter private RpcConnector connector;
    @Getter @Setter private RpcProtocol protocol;
    @Getter @Setter private RpcInvoker invoker;
    @Getter @Setter private RpcProxyFactory proxyFactory;

    //导出接口就是返回接口的一个代理类， 由该代理类负责与远程进行通讯
    public <T> T refer(Class<T> rpcInterface) {
        return proxyFactory.getProxy(rpcInterface);
    }

    public void open() {

    }

    public void close() {

    }

    //初始化阶段， 负责连接Client端各个组件
    public void init() {

        connector.setProtocol(protocol);
        invoker.setConnector(connector);
        proxyFactory.setInvoker(invoker);

    }




}
