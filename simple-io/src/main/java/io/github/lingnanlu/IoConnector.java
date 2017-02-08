package io.github.lingnanlu;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Created by rico on 2017/1/13.
 */
public interface IoConnector extends IoReactor{

    void connect(String ip, int port) throws IOException;
    void connect(SocketAddress remoteAddress) throws IOException;
    void connect(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException;

}
