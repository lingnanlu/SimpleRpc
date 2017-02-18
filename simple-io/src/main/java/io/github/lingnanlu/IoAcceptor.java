package io.github.lingnanlu;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Set;

/**
 * Created by rico on 2017/1/13.
 */
public interface IoAcceptor extends IoReactor{

    void bind(int port) throws Exception;
    void bind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) throws Exception;

    void unbind(int port) throws IOException;

    void unbind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) throws IOException;

    Set<SocketAddress> getBoundAddresses();

}
