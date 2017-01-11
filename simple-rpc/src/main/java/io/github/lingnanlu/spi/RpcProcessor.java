package io.github.lingnanlu.spi;

import io.craft.atom.protocol.rpc.model.RpcMessage;

/**
 * Created by rico on 2017/1/10.
 */
public interface RpcProcessor {

    void process(RpcMessage req, RpcChannel channel);

    void close();

    void setInvoker(RpcInvoker invoker);

    void setExecutorFactory(RpcExecutorFactory executorFactory);
}
