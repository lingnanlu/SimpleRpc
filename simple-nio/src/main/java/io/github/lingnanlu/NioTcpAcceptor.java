package io.github.lingnanlu;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by rico on 2017/1/13.
 */
public class NioTcpAcceptor extends NioAcceptor {


    protected void bindByProtocol(SocketAddress address) {

    }

    protected NioByteChannel acceptByProtocol(SelectionKey key) {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();


        SocketChannel sc = null;
        try {
            sc = ssc.accept();

            sc.configureBlocking(false);
            NioByteChannel channel = new NioTcpByteChannel(sc, dispatcher);
            NioProcessor processor = pool.pick(channel);
            channel.setProcessor(processor);
            processor.add(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
