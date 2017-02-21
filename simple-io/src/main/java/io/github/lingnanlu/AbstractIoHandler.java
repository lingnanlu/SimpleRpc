package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;

/**
 * Created by rico on 2017/2/1.
 */
abstract public class AbstractIoHandler implements IoHandler {
    @Override
    public void channelOpened(Channel<byte[]> channel) {

    }

    @Override
    public void channelClosed(Channel<byte[]> channel) {

    }

    @Override
    public void channelIdle(Channel<byte[]> channel) {

    }

    @Override
    public void channelRead(Channel<byte[]> channel, byte[] bytes) {

    }

    @Override
    public void channelFlush(Channel<byte[]> channel, byte[] bytes) {

    }

    @Override
    public void channelWritten(Channel<byte[]> channel, byte[] bytes) {

    }

    @Override
    public void channelThrown(Channel<byte[]> channel, Exception e) {

    }
}
