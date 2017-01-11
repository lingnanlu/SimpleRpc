package io.github.lingnanlu;

import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.github.lingnanlu.spi.RpcConnector;

/**
 * Created by rico on 2017/1/10.
 */
public class DefaultRpcConnector implements RpcConnector {
    public long connect() {
        return 0;
    }

    public void close() {

    }

    public RpcMessage send(RpcMessage req, boolean async) {
        return null;
    }
}
