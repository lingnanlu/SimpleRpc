package io.github.lingnanlu.spi;

import io.craft.atom.protocol.rpc.model.RpcMessage;

/**
 * Created by rico on 2017/1/10.
 */
public interface RpcInvoker {

    RpcMessage invoke(RpcMessage req);

    void setConnector(RpcConnector connector);

    void setRegistry(RpcRegistry registry);
}
