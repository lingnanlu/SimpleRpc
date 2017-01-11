package io.github.lingnanlu;

import com.esotericsoftware.reflectasm.MethodAccess;
import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.craft.atom.protocol.rpc.model.RpcMethod;
import io.github.lingnanlu.api.RpcContext;
import io.github.lingnanlu.spi.RpcApi;
import io.github.lingnanlu.spi.RpcConnector;
import io.github.lingnanlu.spi.RpcInvoker;
import io.github.lingnanlu.spi.RpcRegistry;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rico on 2017/1/11.
 */
public class DefaultRpcServerInvoker implements RpcInvoker {

    @Getter @Setter private RpcRegistry registry;


    public RpcMessage invoke(RpcMessage req) {
        String     rpcId        = req.getBody().getRpcId();
        Class<?>   rpcInterface = req.getBody().getRpcInterface();
        RpcMethod rpcMethod    = req.getBody().getRpcMethod();
        Class<?>[] paramTypes   = rpcMethod.getParameterTypes();
        Object[]   params       = rpcMethod.getParameters();
        String     methodName   = rpcMethod.getName();

        RpcApi api = registry.lookup(new DefaultRpcApi(rpcId, rpcInterface, rpcMethod));
        Object rpcObject = api.getRpcObject();
        MethodAccess ma = MethodAccess.get(rpcInterface);
        int methodIndex = ma.getIndex(methodName, paramTypes);

        Object returnObject = ma.invoke(rpcObject, methodIndex, params);

        return RpcMessages.newRsponseRpcMessage(req.getId(), returnObject);

    }

    public void setConnector(RpcConnector connector) {

    }
}
