package io.github.lingnanlu.api;

import io.github.lingnanlu.*;
import io.github.lingnanlu.spi.*;

/**
 * Created by rico on 2017/1/11.
 */
public class RpcServerBuilder {

    private String             host                                               ;
    private int                port                                               ;
    private int                connections       = Integer.MAX_VALUE              ;
    private int                ioTimeoutInMillis = Integer.MAX_VALUE              ;
    private RpcAcceptor acceptor          = new DefaultRpcAcceptor()       ;
    private RpcInvoker invoker           = new DefaultRpcServerInvoker()  ;
    private RpcProtocol protocol          = new DefaultRpcProtocol()       ;
    private RpcProcessor processor         = new DefaultRpcProcessor()      ;
    private RpcExecutorFactory executorFactory   = new DefaultRpcExecutorFactory();
    private RpcRegistry        registry          = new DefaultRpcRegistry()       ;


    public RpcServerBuilder host              (String             host             ) { this.host               = host             ; return this; }
    public RpcServerBuilder port              (int                port             ) { this.port               = port             ; return this; }
    public RpcServerBuilder connections       (int                connections      ) { this.connections        = connections      ; return this; }
    public RpcServerBuilder ioTimeoutInMillis (int                ioTimeoutInMillis) { this.ioTimeoutInMillis  = ioTimeoutInMillis; return this; }
    public RpcServerBuilder rpcAcceptor       (RpcAcceptor        acceptor         ) { this.acceptor           = acceptor         ; return this; }
    public RpcServerBuilder rpcInvoker        (RpcInvoker         invoker          ) { this.invoker            = invoker          ; return this; }
    public RpcServerBuilder rpcProtocol       (RpcProtocol        protocol         ) { this.protocol           = protocol         ; return this; }
    public RpcServerBuilder rpcProcessor      (RpcProcessor       processor        ) { this.processor          = processor        ; return this; }
    public RpcServerBuilder rpcExecutorFactory(RpcExecutorFactory executorFactory  ) { this.executorFactory    = executorFactory  ; return this; }
    public RpcServerBuilder rpcRegistry       (RpcRegistry        registry         ) { this.registry           = registry         ; return this; }


    public RpcServer build() {
        DefaultRpcServer rs = new DefaultRpcServer();
        rs.setHost(host);
        rs.setPort(port);
        rs.setAcceptor(acceptor);
        rs.setInvoker(invoker);
        rs.setProtocol(protocol);
        rs.setProcessor(processor);
        rs.setExecutorFactory(executorFactory);
        rs.setRegistry(registry);
        rs.init();
        return rs;
    }

}
