package io.github.lingnanlu;

import io.github.lingnanlu.config.NioConnectorConfig;
import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Selector;

/**
 * Created by rico on 2017/1/18.
 */
abstract public class NioConnector extends NioReactor implements IoConnector {

    protected final NioConnectorConfig config;

    protected Selector selector;

    public NioConnector(IoHandler handler) throws IOException {
        this(handler, new NioConnectorConfig());
    }
    public NioConnector(IoHandler handler, NioConnectorConfig config) throws IOException {
        this(handler, config, new NioOrderedDirectChannelEventDispatcher(), new NioAdaptiveBufferSizePredictorFactory());
    }
    public NioConnector(IoHandler handler, NioConnectorConfig config, NioChannelEventDispatcher dispatcher) throws IOException {
        this(handler, config, dispatcher, new NioAdaptiveBufferSizePredictorFactory());
    }
    public NioConnector(IoHandler handler, NioConnectorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) throws IOException {

        //构造
        this.config = (config == null) ? new NioConnectorConfig() : config;
        this.handler = handler;
        this.dispatcher = dispatcher;
        this.bufferSizePredictorFactory = predictorFactory;
        this.pool = new NioProcessorPool(config, handler, dispatcher);

        init();
    }

    /*
    之所以有这个函数是因为,将NioConnector的构造与初始化隔离开来
     */
    private void init() throws IOException {
        selector = Selector.open();
    }

    @Override
    public void connect(String ip, int port) throws IOException {
        SocketAddress remoteAddress = new InetSocketAddress(ip, port);
        connect(remoteAddress);
    }
    @Override
    public void connect(SocketAddress remoteAddress) throws IOException {
        connect(remoteAddress, null);
    }
    @Override
    public void connect(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException {
        connectByProtocol(remoteAddress, localAddress);
    }

    protected abstract void connectByProtocol(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException;

    @Override
    public void shutdown() throws IOException {
        selector.close();
        super.shutdown();
    }
}
