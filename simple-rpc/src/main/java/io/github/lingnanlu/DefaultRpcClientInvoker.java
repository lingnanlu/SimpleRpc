package io.github.lingnanlu;

import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.github.lingnanlu.api.RpcContext;
import io.github.lingnanlu.spi.RpcConnector;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rico on 2017/1/10.
 */
public class DefaultRpcClientInvoker extends AbstractRpcInvoker {

    @Setter @Getter private RpcConnector connector;
    public RpcMessage invoke(RpcMessage req) {
        RpcContext ctx = RpcContext.getContext();
        boolean async = ctx.isAsync();
        return connector.send(req, async);
    }
}
