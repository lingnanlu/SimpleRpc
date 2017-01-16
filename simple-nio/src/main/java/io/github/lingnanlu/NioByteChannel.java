package io.github.lingnanlu;

import io.github.lingnanlu.channel.AbstractIoByteChannel;

import java.net.SocketAddress;

/**
 * Created by rico on 2017/1/13.
 */
abstract public class NioByteChannel extends AbstractIoByteChannel{

    protected SocketAddress localAddress;
    protected SocketAddress remoteAddress;
    protected NioProcessor processor;
    protected  NioChannelEventDispatcher dispatcher;

    public void setProcessor(NioProcessor processor) {
        this.processor = processor;
    }
}
