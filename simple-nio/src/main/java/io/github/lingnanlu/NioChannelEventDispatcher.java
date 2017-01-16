package io.github.lingnanlu;

import io.github.lingnanlu.channel.ChannelEvent;

/**
 * Created by rico on 2017/1/13.
 */
public interface NioChannelEventDispatcher {

    void dispatch(ChannelEvent<byte[]> event);

    void shutdown();
}
