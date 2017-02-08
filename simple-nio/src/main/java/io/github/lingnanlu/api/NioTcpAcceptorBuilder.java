package io.github.lingnanlu.api;

import io.github.lingnanlu.IoAcceptor;
import io.github.lingnanlu.IoHandler;
import io.github.lingnanlu.NioTcpAcceptor;
import io.github.lingnanlu.config.NioAcceptorConfig;

import java.io.IOException;

/**
 * Created by rico on 2017/1/16.
 */
public class NioTcpAcceptorBuilder extends NioBuilder<IoAcceptor> {

    private int backlog = 50;
    private boolean reuseAddress = true;

    public NioTcpAcceptorBuilder(IoHandler handler) {
        super(handler);
    }

    public NioTcpAcceptorBuilder backlog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public NioTcpAcceptorBuilder reuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
        return this;
    }

    @Override
    public IoAcceptor build() throws IOException {
        NioAcceptorConfig config = new NioAcceptorConfig();
        config.setBacklog(backlog);
        config.setReuseAddress(reuseAddress);
        set(config);
        return new NioTcpAcceptor(handler, config, dispatcher, predictorFactory);
    }
}
