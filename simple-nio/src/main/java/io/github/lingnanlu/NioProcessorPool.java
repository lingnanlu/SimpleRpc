package io.github.lingnanlu;

import io.github.lingnanlu.config.NioConfig;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;
import lombok.Getter;

/**
 * Created by rico on 2017/1/16.
 */
public class NioProcessorPool {

    @Getter private final NioProcessor[] pool;
    @Getter private final NioConfig config;
    @Getter private final NioChannelEventDispatcher dispatcher;
    @Getter private final IoHandler handler;

    public NioProcessorPool(NioConfig config, IoHandler handler, NioChannelEventDispatcher dispatcher) {

        this.pool = new NioProcessor[config.getProcessorPoolSize()];
        this.config = config;
        this.dispatcher = dispatcher;
        this.handler = handler;
    }

    public NioProcessor pick(NioByteChannel channel) {
        return pool[Math.abs((int) (channel.getId() % pool.length))];
    }

}
