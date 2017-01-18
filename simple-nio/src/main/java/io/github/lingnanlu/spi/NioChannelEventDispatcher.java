package io.github.lingnanlu.spi;

import io.github.lingnanlu.channel.ChannelEvent;

/**
 * Created by rico on 2017/1/16.
 */
public interface NioChannelEventDispatcher {

    void dispatch(ChannelEvent<byte[]> event);
    void shutdown();
}
