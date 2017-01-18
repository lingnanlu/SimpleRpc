package io.github.lingnanlu;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Created by rico on 2017/1/13.
 */
public interface IoAcceptor extends IoReactor{

    void bind(int port) throws IOException;

    void bind(SocketAddress address) throws IOException;

    void unbind(SocketAddress address);
    void unbind(int port);

}
