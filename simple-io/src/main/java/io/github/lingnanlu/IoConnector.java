package io.github.lingnanlu;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * Created by rico on 2017/1/13.
 */
public interface IoConnector extends IoReactor{


    void connect(String ip, int port) throws IOException;

    void connect(SocketAddress remoteAddress) throws IOException;

    /*
    返回Future说明该方法是异步的， 调用connect后不一定连接成功， 调用者可以通过Future来查询任务是否完成
     */
    void connect(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException;


}
