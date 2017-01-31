package io.github.lingnanlu;

import io.github.lingnanlu.config.NioConnectorConfig;
import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by rico on 2017/1/18.
 */
public class NioTcpConnector extends NioConnector{

    private final Queue<SocketChannel> connectQueue = new ConcurrentLinkedQueue<>();
    private final Queue<SocketChannel> cancelQueue = new ConcurrentLinkedQueue<>();
    private ConnectThread connectThread;

    public NioTcpConnector(IoHandler handler) {
        super(handler);
    }

    public NioTcpConnector(IoHandler handler, NioConnectorConfig config) {
        super(handler, config);
    }

    public NioTcpConnector(IoHandler handler, NioConnectorConfig config, NioChannelEventDispatcher dispatcher) {
        super(handler, config, dispatcher);
    }

    public NioTcpConnector(IoHandler handler, NioConnectorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) {
        super(handler, config, dispatcher, predictorFactory);
        startup();
    }

    @Override
    protected void connectByProtocol(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException {

        SocketChannel sc = newSocketChannel(localAddress);
        connectQueue.add(sc);

    }

    private void startup() {

        if (connectThread == null) {
            connectThread = new ConnectThread();
        }
        connectThread.start();

    }

    private SocketChannel newSocketChannel(SocketAddress localAddress) throws IOException {
        SocketChannel sc = SocketChannel.open();

        if (localAddress != null) {
            sc.socket().bind(localAddress);
        }

        sc.configureBlocking(false);
        return sc;
    }

    private void registerNewConnectChannel() {
       for(SocketChannel sc = connectQueue.poll(); sc != null; sc = connectQueue.poll()) {
           try {
               sc.register(selector, SelectionKey.OP_CONNECT);
           } catch (ClosedChannelException e) {
               e.printStackTrace();
           }
       }
    }

    private void processConnectedChannels() {

        Iterator<SelectionKey> it = selector.selectedKeys().iterator();

        while (it.hasNext()) {
            SelectionKey key = it.next();
            SocketChannel sc = (SocketChannel) key.channel();
            it.remove();

            boolean success = false;
            try {
                if (sc.finishConnect()) {
                    key.cancel();
                    processConnectedChannel(sc);
                }
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (!success) {
                    cancelQueue.offer(sc);
                }
            }

        }
    }

    private void processConnectedChannel(SocketChannel channel) {
        NioByteChannel wrapedChannel = new NioTcpByteChannel(channel, config, predictorFactory.newPredictor(config.getMinReadBufferSize(), config.getDefaultReadBufferSize(), config.getMaxReadBufferSize()), dispatcher);
        NioProcessor processor = pool.pick(wrapedChannel);
        wrapedChannel.setProcessor(processor);
        processor.add(wrapedChannel);
    }

    private int connectReadyChannels() throws IOException {
        return selector.select();
    }

    //Connector， 这里是Connector线程所做的工作， 理解这里的工作就理解了Connector的职责
    private class ConnectThread extends Thread {

        @Override
        public void run() {
            while (selectable) {
                try {
                    registerNewConnectChannel();

                    int connectReadyCount = connectReadyChannels();

                    if (connectReadyCount > 0) {
                        processConnectedChannels();
                    }
                    closeConnectFailedChannels();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (shutdown) {
                shutdow0();
            }

        }
    }


    //只关闭该实体所负责的资源
    private void shutdow0() {

        cancelQueue.clear();
        connectQueue.clear();

        try {
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnectFailedChannels() {
        for(SocketChannel sc = cancelQueue.poll(); sc != null; sc  = cancelQueue.poll()) {
            SelectionKey key = sc.keyFor(selector);
            key.cancel();
            try {
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
