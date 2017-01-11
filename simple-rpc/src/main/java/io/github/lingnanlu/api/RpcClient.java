package io.github.lingnanlu.api;

/**
 * Created by rico on 2017/1/10.
 */
public interface RpcClient {

    <T> T refer(Class<T> rpcInterface);

    void open();

    void close();


}
