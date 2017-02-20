package io.github.lingnanlu;

import io.github.lingnanlu.api.NioTcpAcceptorBuilder;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by rico on 2017/2/1.
 */
public class NioEchoServer {

    private int port;
    private IoAcceptor acceptor;

    public NioEchoServer(int port) {
        this.port = port;
    }

    public void shutdown() {
        if (acceptor != null) {
            acceptor.shutdown();
        }
    }
    public void start() {
        boolean success = false;
        try {
            acceptor = new NioTcpAcceptorBuilder(new NioEchoServerHandler()).build();
            acceptor.bind(port);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (success == false) {
                acceptor.shutdown();
            }
        }
    }

    public static void main(String[] args){

        NioEchoServer server = new NioEchoServer(1314);
        server.start();

        Scanner in = new Scanner(System.in);
        boolean shutdown = in.nextBoolean();

        if (shutdown == true) {
            server.shutdown();
        }
    }


}
