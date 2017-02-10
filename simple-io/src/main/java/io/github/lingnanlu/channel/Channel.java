package io.github.lingnanlu.channel;

import java.net.SocketAddress;
import java.util.Queue;

/**
 * Created by rico on 2017/1/13.
 */
public interface Channel<D> {

    long getId();

    void close();
    void pause();
    void resume();

    boolean write(D data);

    boolean isOpen();
    boolean isClosing();
    boolean isClosed();
    boolean isPaused();


    Object setAttribute(Object key, Object value);
    Object getAttribute(Object key);
    Object removeAttribute(Object key);
    boolean containsAttribute(Object key);

    SocketAddress getRemoteAddress();
    SocketAddress getLocalAddress();
    Queue<D> getWriteQueue();

}
