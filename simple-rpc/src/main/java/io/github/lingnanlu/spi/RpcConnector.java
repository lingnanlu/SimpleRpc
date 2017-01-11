package io.github.lingnanlu.spi;

import io.craft.atom.protocol.rpc.model.RpcMessage;

/**
 * Created by rico on 2017/1/10.
 */
public interface RpcConnector {

    long connect();

    void close();

    RpcMessage send(RpcMessage req, boolean async);

    void setProtocol(RpcProtocol protocol);
}
