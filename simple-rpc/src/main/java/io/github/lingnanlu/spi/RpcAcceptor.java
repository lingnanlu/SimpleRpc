package io.github.lingnanlu.spi;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Created by rico on 2017/1/10.
 */
public interface RpcAcceptor {

    void bind() throws IOException;
    void close();

    void setProcessor(RpcProcessor processor);

    void setProtocol(RpcProtocol protocol);

    void setAddress(SocketAddress address);

    void setConnections(int connections);


}
