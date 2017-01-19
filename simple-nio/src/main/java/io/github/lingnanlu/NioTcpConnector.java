package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;
import io.github.lingnanlu.config.NioConnectorConfig;
import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Created by rico on 2017/1/18.
 */
public class NioTcpConnector extends NioConnector{


    private final Queue<SocketChannel> connectQueue = new ConcurrentLinkedQueue<>();
    private final Queue<SocketChannel> cancelQueue = new ConcurrentLinkedQueue<>();

    private ConnectThread ct;

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

    //异步方法，返回一个已建立连接的Channel
    @Override
    protected Future<Channel<byte[]>> connectByProtocol(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException {

        SocketChannel sc = newSocketChannel(localAddress);
        connectQueue.add(sc);

        DeliverToProcessorTask cc = new DeliverToProcessorTask(sc);
        FutureTask<Channel<byte[]>> futureTask = new FutureTask<>(cc);
//        cc.setFutureTask(futureTask);
        return futureTask;
    }

    private void startup() {

        if (ct == null) {
            ct = new ConnectThread();
            ct.start();
            //executorService.execute(ct);
        }

    }

    private SocketChannel newSocketChannel(SocketAddress localAddress) throws IOException {
        SocketChannel sc = SocketChannel.open();

        if (localAddress != null) {
            sc.socket().bind(localAddress);
        }

        sc.configureBlocking(false);
        return sc;
    }


    private class DeliverToProcessorTask implements Callable<Channel<byte[]>> {

        @Getter @Setter private FutureTask<Channel<byte[]>> futureTask;
        @Getter private SocketChannel socketChannel;

        public DeliverToProcessorTask(SocketChannel sc) {
            super();
            this.socketChannel = sc;
        }

        @Override
        public Channel<byte[]> call() throws Exception {
            NioByteChannel channel = new NioTcpByteChannel(socketChannel, config, predictorFactory.newPredictor(config.getMinReadBufferSize(), config.getDefaultReadBufferSize(), config.getMaxReadBufferSize()), dispatcher);
            NioProcessor processor = pool.pick(channel);
            channel.setProcessor(processor);
            processor.add(channel);

            return channel;
        }
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

    private void processConnectedChannel() {

        Iterator<SelectionKey> it = selector.selectedKeys().iterator();

        while (it.hasNext()) {
            SelectionKey key = it.next();
            DeliverToProcessorTask cc = (DeliverToProcessorTask) key.attachment();

            it.remove();

            try {
                if (cc.getSocketChannel().finishConnect()) {
                    key.cancel();
                    executorService.execute(cc.getFutureTask());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                        processConnectedChannel();
                    }
                    closeConnectFailedChannel();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (shutdown) {
                shutdow0();
            }

        }
    }

    private void shutdow0() {

    }

    private void closeConnectFailedChannel() {

    }
}
