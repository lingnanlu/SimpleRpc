package io.github.lingnanlu.api;

import io.github.lingnanlu.*;
import io.github.lingnanlu.spi.RpcConnector;
import io.github.lingnanlu.spi.RpcInvoker;
import io.github.lingnanlu.spi.RpcProtocol;
import io.github.lingnanlu.spi.RpcProxyFactory;

/**
 * Created by rico on 2017/1/10.
 *
 * 构建类， 这是一种软件设计方法， 将软件的装配放到一个单独的类中，将装配与运行分开， 这样， 运行时就不需要总是检查程序中的组件是否为空了
 */
public class RpcClientBuilder {

    private String host;
    private int port;

    //以下是Client需要的组件， 由Builder负责装配
    private RpcConnector connector = new DefaultRpcConnector();
    private RpcProtocol protocol = new DefaultRpcProtocol();
    private RpcProxyFactory proxyFactory = new DefaultRpcProxyFactory();
    private RpcInvoker invoker = new DefaultRpcClientInvoker();

    //Builder的链式装配方法
    public RpcClientBuilder host(String host) { this.host = host; return this;}
    public RpcClientBuilder port(int port) { this.port = port; return this;}
    public RpcClientBuilder rpcConnector(RpcConnector connector) { this.connector = connector; return this;}
    public RpcClientBuilder rpcProtocol(RpcProtocol protocol) { this.protocol = protocol; return this;}
    public RpcClientBuilder rpcProxyFactory(RpcProxyFactory proxyFactory) { this.proxyFactory = proxyFactory; return this;}
    public RpcClientBuilder rpcInvoker(RpcInvoker invoker) { this.invoker = invoker; return this;}


    public RpcClient build() {
        DefaultRpcClient client = new DefaultRpcClient();
        client.setHost(host);
        client.setPort(port);
        client.setConnector(connector);
        client.setProtocol(protocol);
        client.setProxyFactory(proxyFactory);
        client.setInvoker(invoker);

        client.init();
        return client;


    }
}
