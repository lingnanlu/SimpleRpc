package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.Future;

/**
 * Created by rico on 2017/1/13.
 */

public interface IoConnector extends IoReactor{

    //connect是异步的
    Future<Channel<byte[]>> connect(String ip, int port) throws IOException;
    Future<Channel<byte[]>> connect(SocketAddress remoteAddress) throws IOException;
    Future<Channel<byte[]>> connect(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException;

}
