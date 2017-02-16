package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;
import lombok.Getter;

/**
 * Created by rico on 2017/2/16.
 */
public class NioConnectorHandler extends AbstractIoHandler {


    public static final byte LF = 10;

    @Getter private StringBuilder buf = new StringBuilder();
    @Getter private String rcv = null;

    @Override
    public void channelRead(Channel<byte[]> channel, byte[] bytes) {
        System.out.println("Channel read bytes size = " + bytes.length);


        for (byte b : bytes) {
            buf.append((char) b);
        }

        if(bytes[bytes.length - 1] == LF) {
            rcv = buf.toString();
            byte[] echoBytes = buf.toString().getBytes();
            System.out.println("Echo received bytes size = " + echoBytes.length);

            buf = new StringBuilder();
            synchronized (channel) {
                channel.notifyAll();
            }
        }
    }
}
