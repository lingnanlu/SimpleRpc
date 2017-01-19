package io.github.lingnanlu.api;

import io.github.lingnanlu.IoConnector;
import io.github.lingnanlu.IoHandler;
import io.github.lingnanlu.NioTcpConnector;
import io.github.lingnanlu.config.NioConnectorConfig;

/**
 * Created by rico on 2017/1/18.
 */
public class NioTcpConnectorBuilder extends NioBuilder<IoConnector> {

    private int connectTimeoutInMillis = 2000;

    public NioTcpConnectorBuilder(IoHandler handler) {
        super(handler);
    }

    public NioTcpConnectorBuilder connectTimeoutInMillis(int timeout) {
        this.connectTimeoutInMillis = timeout;
        return this;
    }

    @Override
    public IoConnector build() {
        NioConnectorConfig config = new NioConnectorConfig();
        config.setConnectTimeoutInMillis(connectTimeoutInMillis);
        set(config);
        return new NioTcpConnector(handler, config, dispatcher, predictorFactory);
    }
}
