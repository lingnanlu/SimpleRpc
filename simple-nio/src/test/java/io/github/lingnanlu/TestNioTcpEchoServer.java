package io.github.lingnanlu;

import io.craft.atom.test.AvailablePortFinder;
import io.github.lingnanlu.api.NioConnectorBuilder;
import io.github.lingnanlu.api.NioTcpAcceptorBuilder;
import io.github.lingnanlu.channel.Channel;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * Created by rico on 2017/2/16.
 */
public class TestNioTcpEchoServer {

    public static final int PORT = AvailablePortFinder.getNextAvailable();

    private IoConnector connector;


    @Test
    public void testHelloMsg() {
        try {
            NioConnectorHandler handler = new NioConnectorHandler();
            connector = new NioConnectorBuilder(handler).build();
            String msg = "Hello\n";
//            System.out.println(Util.strToBytes(msg));
            test(msg, PORT);
            assertEquals(msg, handler.getRcv().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2048Msg() throws IOException {
        NioConnectorHandler handler = new NioConnectorHandler();
        connector = new NioConnectorBuilder(handler).build();
        String msg = build(2048);
        test(msg, PORT);
        assertEquals(msg, handler.getRcv().toString());
    }


    @Test
    @Ignore
    public void test5000Msg() throws IOException {
        NioConnectorHandler handler = new NioConnectorHandler();
        connector = new NioConnectorBuilder(handler).build();
        String msg = build(5000);
        test(msg, PORT);
        assertEquals(msg, handler.getRcv().toString());
    }

    @Test
    @Ignore
    public void test98304Msg() throws IOException {
        NioConnectorHandler handler = new NioConnectorHandler();
        connector = new NioConnectorBuilder(handler).build();
        String msg = build(98304);
        test(msg, PORT);
        assertEquals(msg, handler.getRcv().toString());
    }

    @Test
    @Ignore
    public void test200000Msg() throws IOException {
        NioConnectorHandler handler = new NioConnectorHandler();
        connector = new NioConnectorBuilder(handler).build();
        String msg = build(200000);
        test(msg, PORT);
        assertEquals(msg, handler.getRcv().toString());
    }


    private String build(int len) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < len - 1; i++) {
            sb.append("1");
        }

        sb.append("\n");
        return sb.toString();
    }

    private void test(String msg, int port) {
        try {
            IoAcceptor acceptor = new NioTcpAcceptorBuilder(new NioAcceptorHandler()).build();
            acceptor.bind(port);
            Future<Channel<byte[]>> future = connector.connect("127.0.0.1", port);

            Channel<byte[]> channel = future.get();

            synchronized (channel) {
                channel.write(msg.getBytes());
                channel.wait();     //等待processor从channel中取数据
                acceptor.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}
