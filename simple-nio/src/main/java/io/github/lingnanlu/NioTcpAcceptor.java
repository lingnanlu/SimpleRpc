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

    @Override
    protected void bindByProtocol(SocketAddress address) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ServerSocket ss = ssc.socket();
        ss.setReuseAddress(config.isReuseAddress());
        ss.bind(address, config.getBacklog());
        ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    protected void acceptByProtocol(SelectionKey key) {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = null;
        try {
            sc = ssc.accept();

            ssc.configureBlocking(false);

            NioByteChannel channel = new NioTcpByteChannel(sc, config,
                    predictorFactory.newPredictor(config.getMinReadBufferSize(), config.getDefaultReadBufferSize(), config.getMaxReadBufferSize()),
                    dispatcher);

            NioProcessor processor = pool.pick(channel);
            channel.setProcessor(processor);
            processor.add(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public NioTcpAcceptor(IoHandler handler, int port) {
        super(handler, port);
    }

    public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, int port) {
        super(handler, config, port);
    }

    public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, int port) {
        super(handler, config, dispatcher, port);
    }

    public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory, int port) {
        super(handler, config, dispatcher, predictorFactory, port);
    }

    public NioTcpAcceptor(IoHandler handler, SocketAddress address) {
        super(handler, address);
    }

    public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, SocketAddress address) {
        super(handler, config, address);
    }

    public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, SocketAddress address) {
        super(handler, config, dispatcher, address);
    }

    public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory, SocketAddress address) {
        super(handler, config, dispatcher, predictorFactory, address);
    }

    public NioTcpAcceptor(IoHandler handler) {
        super(handler);
    }

    public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config) {
        super(handler, config);
    }

    public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher) {
        super(handler, config, dispatcher);
    }

    public NioTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) {
        super(handler, config, dispatcher, predictorFactory);
    }
}
