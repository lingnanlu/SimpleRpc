package io.github.lingnanlu.channel;

import java.net.SocketAddress;

/**
 * Created by rico on 2017/1/13.
 */
public interface Channel<D> {

    long getId();

    void close();

    boolean write(D data);

    Object setAttribute(Object key, Object value);

    Object getAttribute(Object key);

    SocketAddress getRemoteAddress();
    SocketAddress getLocalAddress();

}
