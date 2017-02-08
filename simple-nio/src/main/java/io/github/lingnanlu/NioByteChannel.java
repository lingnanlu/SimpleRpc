package io.github.lingnanlu;

import io.github.lingnanlu.channel.AbstractIoByteChannel;
import io.github.lingnanlu.config.NioConfig;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

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
    protected NioProcessor processor;
    protected final NioChannelEventDispatcher dispatcher;
    protected final NioBufferSizePredictor predictor;
    protected final Queue<ByteBuffer> writeBufferQueue = new ConcurrentLinkedQueue<ByteBuffer>()          ;


    public NioByteChannel(NioConfig config, NioBufferSizePredictor predictor, NioChannelEventDispatcher dispatcher) {
        super(config.getMinReadBufferSize(), config.getDefaultReadBufferSize(),config.getMaxReadBufferSize());
        this.predictor = predictor;
        this.dispatcher = dispatcher;
    }


    @Override
    public boolean write(byte[] data) {
        writeBufferQueue.add(ByteBuffer.wrap(data));
        processor.flush(this);
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

    public void setProcessor(NioProcessor processor) {
        this.processor = processor;
    }



}
