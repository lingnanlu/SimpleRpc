package io.github.lingnanlu;

import io.github.lingnanlu.channel.AbstractIoByteChannel;
import io.github.lingnanlu.channel.ChannelEvent;
import io.github.lingnanlu.config.NioConfig;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;
import lombok.Setter;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by rico on 2017/1/16.
 */
abstract public class NioByteChannel extends AbstractIoByteChannel{

    protected SocketAddress localAddress;
    protected SocketAddress remoteAddress;
    protected SelectionKey selectionKey;
    @Setter protected NioProcessor processor;
    protected final NioChannelEventDispatcher dispatcher;
    protected final NioBufferSizePredictor predictor;
    protected final Queue<ChannelEvent<byte[]>> eventQueue = new ConcurrentLinkedQueue<>();


    public NioByteChannel(NioConfig config, NioBufferSizePredictor predictor, NioChannelEventDispatcher dispatcher) {

        super(config.getMinReadBufferSize(), config.getDefaultReadBufferSize(),config.getMaxReadBufferSize());
        this.predictor = predictor;
        this.dispatcher = dispatcher;
    }


    boolean isReadable() {
        return selectionKey.isValid() && selectionKey.isReadable();
    }

    NioBufferSizePredictor getPredictor() {
        return predictor;
    }

    protected int readTcp(ByteBuffer buf) throws IOException {
        return 0;
    }
    void setSelectionKey(SelectionKey key) {
        this.selectionKey = key;
    }
    public abstract SelectableChannel innerChannel();

    void add(ChannelEvent<byte[]> event) {
        eventQueue.offer(event);
    }

    Queue<ChannelEvent<byte[]>> getEventQueue() {return eventQueue;}
}
