package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;

/**
 * Created by rico on 2017/2/1.
 */
public class NioEchoServerHandler extends AbstractIoHandler {

    @Override
    public void channelRead(Channel<byte[]> channel, byte[] bytes) {
//        channel.write(bytes);
        System.out.println(bytes);
    }

    @Override
    public void channelOpened(Channel<byte[]> channel) {
        System.out.println("Server Opened");
    }
}
