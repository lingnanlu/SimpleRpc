package io.github.lingnanlu;

import io.craft.atom.protocol.rpc.model.RpcMethod;
import io.github.lingnanlu.api.RpcParameter;
import io.github.lingnanlu.api.RpcServer;
import io.github.lingnanlu.spi.*;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by rico on 2017/1/10.
 */
public class DefaultRpcServer implements RpcServer {

    @Getter @Setter private String host;
    @Getter @Setter private int port;
    @Getter @Setter private RpcAcceptor acceptor;
    @Getter @Setter private RpcProcessor processor;
    @Getter @Setter private RpcProtocol protocol;
    @Getter @Setter private RpcInvoker invoker;
    @Getter @Setter private RpcExecutorFactory executorFactory;
    @Getter @Setter private RpcRegistry registry;

    public DefaultRpcServer() {

        acceptor = new DefaultRpcAcceptor();
        protocol = new DefaultRpcProtocol();
        processor = new DefaultRpcProcessor();
        invoker = new DefaultRpcServerInvoker();
        executorFactory = new DefaultRpcExecutorFactory();
        registry = new DefaultRpcRegistry();
    }

    public void init() {
        SocketAddress address = (host == null ? new InetSocketAddress(port) : new InetSocketAddress(host, port));
        executorFactory.setRegistry(registry);
        invoker.setRegistry(registry);
        processor.setInvoker(invoker);
        processor.setExecutorFactory(executorFactory);
        acceptor .setProcessor(processor);
        acceptor .setProtocol(protocol);
        acceptor .setAddress(address);
    }

    public void open() {

    }

    public void close() {

    }

    public void export(Class<?> rpcInterface, Object rpcObject, RpcParameter rpcParameter) {

    }

    public void export(String rpcId, Class<?> rpcInterface, String rpcMethodName, Class<?>[] rpcMethodParameterTypes, Object rpcObject, RpcParameter rpcParameter) {
        DefaultRpcApi api = new DefaultRpcApi(rpcId, rpcInterface, new RpcMethod(rpcMethodName, rpcMethodParameterTypes), rpcObject, rpcParameter);
        registry.register(api);
    }

    public void unexport(Class<?> rpcInterface) {

    }
}
