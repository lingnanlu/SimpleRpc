package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;

/**
 * Created by rico on 2017/2/1.
 */
public class NioEchoServerHandler extends AbstractIoHandler {

    @Override
    public void channelRead(Channel<byte[]> channel, byte[] bytes) {
        channel.write(bytes);
    }

}
