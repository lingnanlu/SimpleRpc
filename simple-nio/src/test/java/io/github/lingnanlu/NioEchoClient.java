package io.github.lingnanlu;

import io.github.lingnanlu.api.NioTcpConnectorBuilder;

import java.io.IOException;

/**
 * Created by rico on 2017/2/1.
 */
public class NioEchoClient {

    private String ip;
    private int port;

    public NioEchoClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start() throws IOException {
        IoConnector connector = new NioTcpConnectorBuilder(new NioEchoClientHandler()).build();

        connector.connect(ip, port);
    }

    public static void main(String[] args) {
        try {
            new NioEchoClient("127.0.0.1", 1314).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
