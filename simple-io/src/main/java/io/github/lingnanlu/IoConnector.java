package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.Future;

/**
 * Created by rico on 2017/1/13.
 */
public interface IoConnector extends IoReactor{


    Future<Channel<byte[]>> connect(String ip, int port);

    Future<Channel<byte[]>> connect(SocketAddress remoteAddress);

    /*
    返回Future说明该方法是异步的， 调用connect后不一定连接成功， 调用者可以通过Future来查询任务是否完成
     */
    Future<Channel<byte[]>> connect(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException;


}
