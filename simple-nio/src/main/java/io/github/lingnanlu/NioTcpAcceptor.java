package io.github.lingnanlu;

import io.github.lingnanlu.config.NioAcceptorConfig;
import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by rico on 2017/1/16.
 */
public class NioTcpAcceptor extends NioAcceptor {

    public NioTcpAcceptor(IoHandler handler) throws IOException {
        super(handler);
    }

    public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config) throws IOException {
        super(handler, config);
    }

    public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher) throws IOException {
        super(handler, config, dispatcher);
    }

    public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) throws IOException {
        super(handler, config, dispatcher, predictorFactory);
    }

    @Override
    protected void bindByProtocol(SocketAddress address) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ServerSocket ss = ssc.socket();
        ss.setReuseAddress(config.isReuseAddress());
        ss.bind(address, config.getBacklog());
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        boundmap.put(address, ssc);
    }

    @Override
    protected void acceptByProtocol(SelectionKey key) {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = null;
        try {
            sc = ssc.accept();
            sc.configureBlocking(false);

            dispatchToProcessor(sc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatchToProcessor(SocketChannel sc) {
        NioByteChannel channel = new NioTcpByteChannel(sc, config,
                bufferSizePredictorFactory.newPredictor(config.getMinReadBufferSize(), config.getDefaultReadBufferSize(), config.getMaxReadBufferSize()),
                dispatcher);
        NioProcessor processor = pool.pick(channel);
        channel.setProcessor(processor);
        processor.add(channel);
    }


}
