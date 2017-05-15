package io.github.lingnanlu;

import io.craft.atom.test.AvailablePortFinder;
import io.github.lingnanlu.api.NioTcpAcceptorBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by rico on 2017/2/14.
 */
public class TestNioTcpAcceptor {

    public static final int PORT = AvailablePortFinder.getNextAvailable(33333);


    private IoAcceptor acceptor;

    @Before
    public void before() throws IOException {
        acceptor = new NioTcpAcceptorBuilder(new NioAcceptorHandler()).build();
       // acceptor.bind(PORT);
    }


    @After
    public void after() throws IOException {
        acceptor.shutdown();
    }

    @Test
    public void testDuplicateBind() {

        try {
            for(int i = 0; i < 10; i++) {
                acceptor.bind(PORT);
            }
            Assert.fail();
        }  catch (Exception e) {
        }
    }

    @Test
    //@Ignore
    public void testDuplicateUnbind() throws Exception {
        acceptor.bind(PORT);
        acceptor.unbind(PORT);
        acceptor.unbind(PORT);

        assertEquals(0, acceptor.getBoundAddresses().size());
    }


    @Test
    public void testBindAndUnbindManyTimes() throws Exception {

        for(int i = 0; i < 10; i++) {
            acceptor.bind(PORT);
            acceptor.unbind(PORT);

            assertEquals(0, acceptor.getBoundAddresses().size());
        }
        assertEquals(0, acceptor.getBoundAddresses().size());
    }

    @Test
    public void testNullPort() {

        try {
            acceptor.bind(null);
        } catch (Exception e) {
            assertEquals("the address should not be null, need a local address", e.getMessage());
        }

    }
}
