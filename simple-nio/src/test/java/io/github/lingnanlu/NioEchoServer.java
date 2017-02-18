package io.github.lingnanlu;

import io.github.lingnanlu.api.NioTcpAcceptorBuilder;

/**
 * Created by rico on 2017/2/1.
 */
public class NioEchoServer {

    private int port;

    public NioEchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        IoAcceptor acceptor = new NioTcpAcceptorBuilder(new NioEchoServerHandler()).build();
        acceptor.bind(port);
    }

    public static void main(String[] args) throws Exception {
        new NioEchoServer(1314).start();
    }
}
