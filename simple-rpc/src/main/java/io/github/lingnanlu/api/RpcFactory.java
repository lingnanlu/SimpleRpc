package io.github.lingnanlu.api;

/**
 * Created by rico on 2017/1/10.
 */
public class RpcFactory {

    public static RpcClient newRpcClient(String host, int port) {
        return newRpcClientBuilder(host, port).build();
    }

    public static RpcClientBuilder newRpcClientBuilder(String host, int port) {
        return new RpcClientBuilder().host(host).port(port);
    }

    public static RpcServer newRpcServer(int port) {
        return newRpcServerBuilder(port).build();
    }

    public static RpcServerBuilder newRpcServerBuilder(int port) {
        return new RpcServerBuilder().port(port);
    }
}
