package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by rico on 2017/2/1.
 */
public class NioEchoClientHandler extends AbstractIoHandler {

    private AtomicInteger counter = new AtomicInteger();

    @Override
    public void channelOpened(Channel<byte[]> channel) {
        System.out.println("client Channel Opened");
        send(channel);
    }

    private void send(Channel<byte[]> channel) {
        String toSend = Integer.toString(counter.incrementAndGet());
        channel.write(toSend.getBytes());
        System.out.println("ECHO-SENT: " + toSend);
    }

    @Override
    public void channelRead(Channel<byte[]> channel, byte[] bytes) {
        System.out.println("ECHO-RECV: " + new String(bytes));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        send(channel);
    }

    @Override
    public void channelWritten(Channel<byte[]> channel, byte[] bytes) {
        System.out.println("Channel WRITE " + new String(bytes));
    }
}
