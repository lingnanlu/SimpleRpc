package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;

/**
 * Created by rico on 2017/2/1.
 */
public class NioEchoServerHandler extends AbstractIoHandler {

    @Override
    public void channelRead(Channel<byte[]> channel, byte[] bytes) {
        System.out.println("Server Read " + new String(bytes));
        channel.write(bytes);
    }

    @Override
    public void channelFlush(Channel<byte[]> channel, byte[] bytes) {
        System.out.println("Server Flush " + new String(bytes));
    }

    @Override
    public void channelWritten(Channel<byte[]> channel, byte[] bytes) {
        System.out.println("Server Written " + new String(bytes));
    }
}
