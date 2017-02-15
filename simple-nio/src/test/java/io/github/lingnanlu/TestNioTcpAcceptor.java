package io.github.lingnanlu;

import io.craft.atom.test.AvailablePortFinder;
import io.github.lingnanlu.api.NioTcpAcceptorBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by rico on 2017/2/14.
 */
public class TestNioTcpAcceptor {

    public static final int PORT = AvailablePortFinder.getNextAvailable(33333);

    private IoAcceptor acceptor;


    @Before
    public void before() throws IOException {
        acceptor = new NioTcpAcceptorBuilder(new NioAcceptorHandler()).build();
        acceptor.bind(PORT);
    }


    @After
    public void after() throws IOException {
        acceptor.shutdown();
    }

    @Test
    public void testDuplicateBind() {

        try {
            acceptor.bind(PORT);
            Assert.fail();
        } catch (IOException e) {
            System.out.println("hehe");
        }
    }
}
