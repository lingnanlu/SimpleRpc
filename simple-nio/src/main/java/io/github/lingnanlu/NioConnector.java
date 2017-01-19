package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;
import io.github.lingnanlu.config.NioConnectorConfig;
import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by rico on 2017/1/18.
 */
abstract public class NioConnector extends NioReactor implements IoConnector {

    protected final NioConnectorConfig config;
    protected final ExecutorService executorService = Executors.newCachedThreadPool();
    protected boolean selectable = false;
    protected boolean shutdown = false;
    protected Selector selector;

    public NioConnector(IoHandler handler) {
        this(handler, new NioConnectorConfig());
    }

    public NioConnector(IoHandler handler, NioConnectorConfig config) {
        this(handler, config, new NioOrderedDirectChannelEventDispatcher(config.getTotalEventSize()), new NioAdaptiveBufferSizePredictorFactory());
    }
    public NioConnector(IoHandler handler, NioConnectorConfig config, NioChannelEventDispatcher dispatcher) {
        this(handler, config, dispatcher, new NioAdaptiveBufferSizePredictorFactory());
    }
    public NioConnector(IoHandler handler, NioConnectorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) {

        if (handler == null) {
            throw new IllegalArgumentException("Handler should not be null!");
        }

        this.config = (config == null) ? new NioConnectorConfig() : config;
        this.handler = handler;
        this.dispatcher = dispatcher;
        this.predictorFactory = predictorFactory;

        try {
            init();
        } catch (IOException e) {
            throw new RuntimeException("Failed to construct", e);
        } finally {
            if (selector != null && !selectable) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void init() throws IOException {
        selector = Selector.open();
        selectable = true;
    }

    @Override
    public Future<Channel<byte[]>> connect(String ip, int port) {

        SocketAddress remoteAddress = new InetSocketAddress(ip, port);
        return connect(remoteAddress);
    }

    @Override
    public Future<Channel<byte[]>> connect(SocketAddress remoteAddress) {
        return connect(remoteAddress, null);
    }

    @Override
    public Future<Channel<byte[]>> connect(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException {

        if (!this.selectable) {
            throw new IllegalStateException("The connector is already shutdown.");
        }

        if (remoteAddress == null) {
            throw new IllegalArgumentException("Remote address is null.");
        }

        if (handler == null) {
            throw new IllegalStateException("Handler is not be set!");
        }

        return connectByProtocol(remoteAddress, localAddress);
    }

    protected abstract Future<Channel<byte[]>> connectByProtocol(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException;

}