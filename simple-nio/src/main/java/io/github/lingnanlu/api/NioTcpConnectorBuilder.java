package io.github.lingnanlu.api;

import io.github.lingnanlu.IoConnector;
import io.github.lingnanlu.IoHandler;
import io.github.lingnanlu.NioTcpConnector;
import io.github.lingnanlu.config.NioConnectorConfig;

import java.io.IOException;

/**
 * Created by rico on 2017/1/18.
 */
public class NioTcpConnectorBuilder extends NioBuilder<IoConnector> {

    public NioTcpConnectorBuilder(IoHandler handler) {
        super(handler);
    }

    @Override
    public IoConnector build() throws IOException {
        NioConnectorConfig config = new NioConnectorConfig();
        set(config);
        return new NioTcpConnector(handler, config, dispatcher, predictorFactory);
    }
}
