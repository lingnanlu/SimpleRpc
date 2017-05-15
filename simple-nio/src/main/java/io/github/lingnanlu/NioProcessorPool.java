package io.github.lingnanlu;

import io.github.lingnanlu.config.NioConfig;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by rico on 2017/1/16.
 */
public class NioProcessorPool {

    public static final Logger LOG = LoggerFactory.getLogger(NioProcessorPool.class);

    @Getter private final NioProcessor[] pool;
    @Getter private final NioConfig config;
    @Getter private final NioChannelEventDispatcher dispatcher;
    @Getter private final IoHandler handler;
    @Getter private final NioChannelIdleTimer idleTimer;

    public NioProcessorPool(NioConfig config, IoHandler handler, NioChannelEventDispatcher dispatcher) {

        this.pool = new NioProcessor[config.getProcessorPoolSize()];
        this.config = config;
        this.dispatcher = dispatcher;
        this.handler = handler;
        this.idleTimer = new NioChannelIdleTimer(dispatcher, handler, config.getIoTimeoutInMillis());

        fill(pool);


    }

    public NioProcessor pick(NioByteChannel channel) {
        return pool[Math.abs((int) (channel.getId() % pool.length))];
    }

    public void shutdown() {
        for (NioProcessor processor : pool) {
            processor.shutdown();
        }
    }

    private void fill(NioProcessor[] pool)  {
        if (pool == null) {
            return;
        }

        for (int i = 0; i < pool.length; i++) {
            try {
                pool[i] = new NioProcessor("NioProcessor_" + i, config, handler, dispatcher, idleTimer);
            } catch (IOException e) {
                LOG.info("[Simple-NIO] processor created failed");
            }
        }
    }
}
