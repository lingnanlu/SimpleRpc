package io.github.lingnanlu;

import io.github.lingnanlu.config.NioAcceptorConfig;
import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * Created by rico on 2017/1/16.
 */
abstract public class NioAcceptor extends NioReactor implements IoAcceptor{

    protected NioAcceptorConfig config;
    protected Selector selector;
    protected boolean selectable = false;


    @Override
    public void bind(int port) throws IOException {
        bind(new InetSocketAddress(port));
    }

    @Override
    public void bind(SocketAddress address) throws IOException {
        if (!this.selectable) {
            init();
        }
        bindByProtocol(address);
        start();
    }


    public void start() {
        new AcceptThread().start();
    }
    protected abstract void bindByProtocol(SocketAddress address) throws IOException;

    private class AcceptThread extends Thread {
        @Override
        public void run() {
            while (selectable) {
                try {
                    int selected = selector.select();

                    if (selected > 0) {
                        accept();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void accept() {
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            it.remove();
            acceptByProtocol(key);
        }
    }

    protected abstract void acceptByProtocol(SelectionKey key);

    private void init() throws IOException {
        selector = Selector.open();
        selectable = true;
        new AcceptThread().start();
    }
    @Override
    public void unbind(SocketAddress address) {

    }

    @Override
    public void unbind(int port) {

    }


    public NioAcceptor(IoHandler handler, NioAcceptorConfig config,
                       NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory, SocketAddress address) {

    }

    public NioAcceptor(IoHandler handler, NioAcceptorConfig config,
                       NioChannelEventDispatcher dispatcher, SocketAddress address) {
        this(handler, config, dispatcher, new NioAdaptiveBufferSizePredictorFactory(), address);
    }

    public NioAcceptor(IoHandler handler, NioAcceptorConfig config, SocketAddress address) {
        this(handler, config,  new NioOrderedDirectChannelEventDispatcher(config.getTotalEventSize()), new NioAdaptiveBufferSizePredictorFactory(), address);
    }

    public NioAcceptor(IoHandler handler, SocketAddress address) {
        this(handler, new NioAcceptorConfig(), address);
    }

    public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory, int port){

        this(handler, config, dispatcher, predictorFactory, new InetSocketAddress(port));

    }

    public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, int port) {
        this(handler, config, dispatcher, new InetSocketAddress(port));
    }

    public NioAcceptor(IoHandler handler, NioAcceptorConfig config, int port) {
        this(handler, config, new InetSocketAddress(port));
    }

    public NioAcceptor(IoHandler handler, int port) {
        this(handler, new NioAcceptorConfig(), port);
    }

    //不绑定到任何端口的构造器
    public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) {
    }

    public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher) {
        this(handler, config, dispatcher, new NioAdaptiveBufferSizePredictorFactory());
    }

    public NioAcceptor(IoHandler handler, NioAcceptorConfig config) {
        this(handler, config, new NioOrderedDirectChannelEventDispatcher(), new NioAdaptiveBufferSizePredictorFactory());
    }

    public NioAcceptor(IoHandler handler) {
        this(handler, new NioAcceptorConfig(), new NioOrderedDirectChannelEventDispatcher(), new NioAdaptiveBufferSizePredictorFactory());
    }



}
