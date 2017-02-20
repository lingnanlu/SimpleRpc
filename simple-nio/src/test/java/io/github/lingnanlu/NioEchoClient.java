package io.github.lingnanlu;

import io.github.lingnanlu.api.NioConnectorBuilder;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by rico on 2017/2/1.
 */
public class NioEchoClient {

    private String ip;
    private int port;
    private IoConnector connector;

    public NioEchoClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void shutdown() {
        connector.shutdown();
    }
    public void start(){
        boolean success = false;
        try {
            connector = new NioConnectorBuilder(new NioEchoClientHandler()).build();
            connector.connect(ip, port);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!success) {
                connector.shutdown();
            }
        }
    }

    public static void main(String[] args) {
        NioEchoClient client = new NioEchoClient("127.0.0.1", 1314);
        client.start();

        Scanner in = new Scanner(System.in);
        boolean shutdown = in.nextBoolean();
        if (shutdown){
            client.shutdown();
        }
    }

}
