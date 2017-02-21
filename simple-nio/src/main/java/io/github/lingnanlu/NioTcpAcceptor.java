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

    //protected方法的异常也不会传递的客户端，所以，这里是在run中集中报告错误
    @Override
    protected void acceptByProtocol(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = null;
        try {
            sc = ssc.accept();
            sc.configureBlocking(false);
            dispatchToProcessor(sc);
        } catch (IOException e) {
            //在这一层出现问题时，该层可做的就是关闭socketchannel,然后通知给上层，由上层将错误打的Log中
            //但这里也可以直接打Log，我感觉两种方法都可以
            close(sc);
            throw e;
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

    private void close(SocketChannel sc) {
        if(sc == null) return;
        try {
            sc.close();
        } catch (IOException e) {
            LOG.info("[Simple-NIO] close exception", e);
        }
    }


}
