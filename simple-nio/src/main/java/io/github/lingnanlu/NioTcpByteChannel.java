package io.github.lingnanlu;

import io.github.lingnanlu.config.NioConfig;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by rico on 2017/1/16.
 */
public class NioTcpByteChannel extends NioByteChannel {

    private SocketChannel socketChannel;

    public NioTcpByteChannel(SocketChannel socketChannel, NioConfig config, NioBufferSizePredictor predictor, NioChannelEventDispatcher dispatcher) {
        super(config, predictor, dispatcher);

        this.socketChannel = socketChannel;
        this.localAddress = socketChannel.socket().getLocalSocketAddress();
        this.remoteAddress = socketChannel.socket().getRemoteSocketAddress();
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

    @Override
    protected int readTcp(ByteBuffer buf) throws IOException {
        return socketChannel.read(buf);
    }

    @Override
    public SelectableChannel innerChannel() {
        return socketChannel;
    }
}
