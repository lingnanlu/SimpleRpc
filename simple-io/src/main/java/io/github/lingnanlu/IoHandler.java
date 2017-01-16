package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;

/**
 * Created by rico on 2017/1/13.
 */
public interface IoHandler {

    void channelOpened(Channel<byte[]> channel);

    void channelClosed(Channel<byte[]> channel);

    void channelIdle(Channel<byte[]> channel);

    void channelRead(Channel<byte[]> channel, byte[] bytes);

    void channelFlush(Channel<byte[]> channel, byte[] bytes);

    void channelWritten(Channel<byte[]> channel, byte[] bytes);
}
