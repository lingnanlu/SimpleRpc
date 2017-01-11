package io.github.lingnanlu;

import io.craft.atom.io.IoAcceptor;
import io.craft.atom.io.IoHandler;
import io.craft.atom.nio.api.NioFactory;
import io.github.lingnanlu.spi.RpcAcceptor;
import io.github.lingnanlu.spi.RpcProcessor;
import io.github.lingnanlu.spi.RpcProtocol;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Created by rico on 2017/1/10.
 */
public class DefaultRpcAcceptor implements RpcAcceptor {

    @Getter @Setter private int connections;
    @Getter @Setter private SocketAddress address;
    @Getter @Setter private RpcProcessor processor;
    @Getter @Setter private RpcProtocol protocol;
    @Getter @Setter private IoHandler ioHandler;
    @Getter @Setter private IoAcceptor ioAcceptor;

    public void bind() throws IOException {
        ioHandler = new RpcServerIoHandler(protocol, processor);

        ioAcceptor = NioFactory.newTcpAcceptorBuilder(ioHandler).build();

        ioAcceptor.bind(address);
    }

    public void close() {
        ioAcceptor.shutdown();
    }

}
