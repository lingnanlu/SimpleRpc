package io.github.lingnanlu;

import io.github.lingnanlu.channel.Channel;
import io.github.lingnanlu.config.NioConnectorConfig;
import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by rico on 2017/1/18.
 */
public class NioConnector extends NioReactor implements IoConnector {

    protected NioProcessorPool pool;

    protected final NioConnectorConfig config;
    protected boolean shutdown = false;

    private final Queue<SocketChannel> connectQueue = new ConcurrentLinkedQueue<>();
    private final Queue<SocketChannel> cancelQueue = new ConcurrentLinkedQueue<>();
    //该成员是为了执行FutureTask，最终是为了将connect变为异步方法
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<SocketChannel, FutureTask<Channel<byte[]>>> socketToTaskMap = new HashMap<>();
    protected Selector selector;

    public NioConnector(IoHandler handler) throws IOException {
        this(handler, new NioConnectorConfig());
    }
    public NioConnector(IoHandler handler, NioConnectorConfig config) throws IOException {
        this(handler, config, new NioOrderedDirectChannelEventDispatcher(), new NioAdaptiveBufferSizePredictorFactory());
    }
    public NioConnector(IoHandler handler, NioConnectorConfig config, NioChannelEventDispatcher dispatcher) throws IOException {
        this(handler, config, dispatcher, new NioAdaptiveBufferSizePredictorFactory());
    }
    public NioConnector(IoHandler handler, NioConnectorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) throws IOException {

        //构造
        this.config = (config == null) ? new NioConnectorConfig() : config;
        this.handler = handler;
        this.dispatcher = dispatcher;
        this.bufferSizePredictorFactory = predictorFactory;
        this.pool = new NioProcessorPool(config, handler, dispatcher);

        init();
        startup();
    }

    /*
    之所以有这个函数是因为,将NioConnector的构造与初始化隔离开来
     */
    private void init() throws IOException {
        selector = Selector.open();
    }

    private void startup() {
        new ConnectorThread().start();
    }

    private class ConnectorThread extends Thread {

        @Override
        public void run() {
            while (!shutdown) {
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

            try {
                shutdow0();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public Future<Channel<byte[]>> connect(String ip, int port) throws IOException {
        SocketAddress remoteAddress = new InetSocketAddress(ip, port);
        return connect(remoteAddress);
    }

    @Override
    public Future<Channel<byte[]>> connect(SocketAddress remoteAddress) throws IOException {
        return connect(remoteAddress, null);
    }

    @Override
    public Future<Channel<byte[]>> connect(SocketAddress remoteAddress, SocketAddress localAddress) throws IOException {
        SocketChannel sc = newSocketChannel(localAddress);

        //表示一个异步任务
        FutureTask<Channel<byte[]>> futureTask = new FutureTask<>(new DeliverToProcessorTask(sc));

        //本地
        if (sc.connect(remoteAddress)) {
            executorService.submit(futureTask);
        } else {
            //非本地, 注册OP_CONNECT事件
            socketToTaskMap.put(sc, futureTask);
            connectQueue.add(sc);
            wakeUp();
        }
        return futureTask;
    }

    /*
           注意这里的关闭方式

           NioReactor负责关闭它所申请的资源
           NioConnector负责关闭它所申请的资源
           NioTcpConnector负责关闭它所申请的资源

           因为NioTcpConnector运行在一个线程当中，而shutdown是由另一个线程调用的。

           所以不能直接在shutdown中直接关闭资源，因为可能另一个线程中正在关闭组件，而NioTcpConnector在使用组件。

           所以利用shutdown标志，标明要关闭，再在shutdown0中进行真实的组件关闭操作
     */
    @Override
    public void shutdown() throws IOException {
        shutdown = true;
        wakeUp();

    }

    private void wakeUp() {
        selector.wakeup();
    }

    private SocketChannel newSocketChannel(SocketAddress localAddress) throws IOException {

        SocketChannel sc = SocketChannel.open();

        if (localAddress != null) {
            sc.socket().bind(localAddress);
        }

        sc.configureBlocking(false);
        return sc;
    }

    private void registerNewConnectChannel() throws ClosedChannelException {
       for(SocketChannel sc = connectQueue.poll(); sc != null; sc = connectQueue.poll()) {
           sc.register(selector, SelectionKey.OP_CONNECT);
       }
    }

    private void processConnectedChannels() throws IOException {

        Iterator<SelectionKey> it = selector.selectedKeys().iterator();

        while (it.hasNext()) {
            SelectionKey key = it.next();
            SocketChannel sc = (SocketChannel) key.channel();
            it.remove();

            boolean success = false;
            try {
                if (sc.finishConnect()) {
                    key.cancel();
                    FutureTask<Channel<byte[]>> futureTask = socketToTaskMap.get(sc);
                    socketToTaskMap.remove(sc);
                    executorService.submit(futureTask);
                    success = true;
                }
            } finally {
                if (!success) {
                    cancelQueue.offer(sc);
                }
            }

        }
    }

    private int connectReadyChannels() throws IOException {
        return selector.select();
    }

    //这里才是真正的清理资源的地方
    private void shutdow0() throws IOException {
        cancelQueue.clear();
        connectQueue.clear();
        selector.close();
        pool.shutdown();
        super.shutdown();
    }

    private void closeConnectFailedChannels() throws IOException {
        for(SocketChannel sc = cancelQueue.poll(); sc != null; sc  = cancelQueue.poll()) {
            SelectionKey key = sc.keyFor(selector);
            key.cancel();
            sc.close();
        }
    }

    private class DeliverToProcessorTask implements Callable<Channel<byte[]>> {

        private SocketChannel socketChannel;

        public DeliverToProcessorTask(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public Channel<byte[]> call() throws Exception {
            NioByteChannel channel = new NioTcpByteChannel(socketChannel, config, bufferSizePredictorFactory.newPredictor(config.getMinReadBufferSize(), config.getDefaultReadBufferSize(), config.getMaxReadBufferSize()), dispatcher);
            NioProcessor processor = pool.pick(channel);
            channel.setProcessor(processor);
            processor.add(channel);
            return channel;
        }
    }

}
