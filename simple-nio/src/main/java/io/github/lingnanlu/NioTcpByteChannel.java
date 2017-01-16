package io.github.lingnanlu;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by rico on 2017/1/16.
 */
public class NioTcpByteChannel extends NioByteChannel {

    private SocketChannel socketChannel;

    public NioTcpByteChannel(SocketChannel sc, NioChannelEventDispatcher dispatcher) {
        this.socketChannel = sc;
        this.dispatcher = dispatcher;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public boolean write(byte[] data) {
        return false;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public SocketAddress getLocalAddress() {
        return null;
    }
}
