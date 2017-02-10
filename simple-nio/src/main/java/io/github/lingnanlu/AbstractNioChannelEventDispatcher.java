package io.github.lingnanlu;

import io.github.lingnanlu.spi.NioChannelEventDispatcher;

/**
 * Created by rico on 2017/1/16.
 */
abstract public class AbstractNioChannelEventDispatcher implements NioChannelEventDispatcher {

    @Override
    public void shutdown() {}
}
