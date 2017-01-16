package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;

import java.net.SocketAddress;
import java.util.concurrent.Future;

/**
 * Created by rico on 2017/1/13.
 */
public interface IoConnector {

    Future<Channel<byte[]>> connect(String ip, int port);

    Future<Channel<byte[]>> connect(SocketAddress remoteAddress);

    Future<Channel<byte[]>> connect(SocketAddress remoteAddress, SocketAddress localAddress);


}
