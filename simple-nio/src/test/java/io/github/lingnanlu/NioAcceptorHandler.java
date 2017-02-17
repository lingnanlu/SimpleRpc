package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;

/**
 * Created by rico on 2017/2/14.
 */
public class NioAcceptorHandler extends AbstractIoHandler {


    public static final byte LF = 10;

    private StringBuilder buf = new StringBuilder();

    @Override
    public void channelRead(Channel<byte[]> channel, byte[] bytes) {

        for (byte b : bytes) {
            buf.append((char)b);
        }

        if (bytes[bytes.length - 1] == LF) {
            byte[] echoBytes = buf.toString().getBytes();

            channel.write(echoBytes);
            buf = new StringBuilder();
        }
    }
}
