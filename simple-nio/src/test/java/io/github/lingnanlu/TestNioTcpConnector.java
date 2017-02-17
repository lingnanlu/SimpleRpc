package io.github.lingnanlu;

import io.craft.atom.test.AvailablePortFinder;
import io.github.lingnanlu.api.NioConnectorBuilder;
import io.github.lingnanlu.channel.Channel;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by rico on 2017/2/17.
 */
public class TestNioTcpConnector {


    @Test
    public void testTimeout() throws IOException {

        IoConnector connector = new NioConnectorBuilder(new NioConnectorHandler()).build();

        Future<Channel<byte[]>> future = connector.connect("127.0.0.1", AvailablePortFinder.getNextAvailable());

        try {
            future.get(200, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Assert.assertTrue(true);
        }
    }
}
