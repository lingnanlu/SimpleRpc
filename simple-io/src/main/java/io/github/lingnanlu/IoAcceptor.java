package io.github.lingnanlu;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Set;

/**
 * Created by rico on 2017/1/13.
 */
public interface IoAcceptor extends IoReactor{


    void bind(int port);

    void bind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddress) throws IOException;

    void unbind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddress);

    void unbind(int port);

    Set<SocketAddress> getBoundAddresses();

}
