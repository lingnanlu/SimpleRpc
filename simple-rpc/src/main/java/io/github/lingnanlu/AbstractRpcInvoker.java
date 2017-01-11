package io.github.lingnanlu;

import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.github.lingnanlu.spi.RpcConnector;
import io.github.lingnanlu.spi.RpcInvoker;

/**
 * Created by rico on 2017/1/10.
 */
public abstract class AbstractRpcInvoker implements RpcInvoker {

    public void setConnector(RpcConnector connector) {

    }
}
