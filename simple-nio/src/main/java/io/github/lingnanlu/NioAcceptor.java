package io.github.lingnanlu;

import io.github.lingnanlu.config.NioAcceptorConfig;
import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rico on 2017/1/16.
 */
abstract public class NioAcceptor extends NioReactor implements IoAcceptor {

    //组件
    protected NioProcessorPool pool;

    //属性
    protected NioAcceptorConfig config;
    protected boolean shutdown = false;

    //成员
    protected final Set<SocketAddress> bindAddresses = new HashSet<>();
    protected final Set<SocketAddress> unbindAddresses = new HashSet<>();
    protected final Map<SocketAddress, SelectableChannel> boundmap = new ConcurrentHashMap<>();
    protected Selector selector;

    //辅助
    protected final Object lock = new Object();
    protected boolean endFlag = false;

    public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) throws IOException {

        //组装
        this.handler = handler;
        this.config = (config == null ? new NioAcceptorConfig() : config);
        this.dispatcher = dispatcher;
        this.bufferSizePredictorFactory = predictorFactory;
        this.pool = new NioProcessorPool(config, handler, dispatcher);

        //初始化
        init();

        //启动
        startup();
    }

    public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher) throws IOException {
        this(handler, config, dispatcher, new NioAdaptiveBufferSizePredictorFactory());
    }

    public NioAcceptor(IoHandler handler, NioAcceptorConfig config) throws IOException {
        this(handler, config, new NioOrderedDirectChannelEventDispatcher(), new NioAdaptiveBufferSizePredictorFactory());
    }

    public NioAcceptor(IoHandler handler) throws IOException {
        this(handler, new NioAcceptorConfig(), new NioOrderedDirectChannelEventDispatcher(), new NioAdaptiveBufferSizePredictorFactory());
    }

    private void startup() {
        new AcceptorThread().start();
    }

    private void init() throws IOException {
        selector = Selector.open();
    }

    //工作流程
    private class AcceptorThread extends Thread {
        @Override
        public void run() {
            while (!shutdown) {
                try {
                    int selected = selector.select();

                    if (selected > 0) {
                        accept();
                    }

                    bind0();

                    unbind0();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                shutdown0();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //接口
    @Override
    public void bind(int port) throws IOException {
        bind(new InetSocketAddress(port));
    }

    @Override
    public void bind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) throws IOException {

        if (firstLocalAddress == null) {
            throw new IllegalArgumentException("Need a local address to bound");
        }

        List<SocketAddress> localAddresses = new ArrayList<>();
        bindAddresses.add(firstLocalAddress);

        for (SocketAddress address : otherLocalAddresses) {
            localAddresses.add(address);
        }

        bindAddresses.addAll(localAddresses);

        synchronized (lock) {
            selector.wakeup();
            wait0();
        }

    }

    @Override
    public void unbind(int port) {
        unbind(new InetSocketAddress(port));
    }

    @Override
    public void unbind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {

        if (firstLocalAddress == null) {
            return;
        }

        List<SocketAddress> localAddresses = new ArrayList<>();

        if (boundmap.containsKey(firstLocalAddress)) {
            localAddresses.add(firstLocalAddress);
        }

        for (SocketAddress address : otherLocalAddresses) {
            if (boundmap.containsKey(address)) {
                localAddresses.add(address);
            }
        }

        unbindAddresses.addAll(localAddresses);

        synchronized (lock) {
            selector.wakeup();
            wait0();
        }


    }

    @Override
    public void shutdown() throws IOException {
        this.shutdown = true;
    }

    @Override
    public Set<SocketAddress> getBoundAddresses() {
        return new HashSet<>(boundmap.keySet());
    }

    private void accept() {
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            it.remove();
            acceptByProtocol(key);
        }
    }


    private void wait0() {
        while (!endFlag) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //reset endflag
        endFlag = false;
    }


    private void unbind0() {
        for (SocketAddress address : unbindAddresses) {
            SelectableChannel ssc = boundmap.get(address);
            try {
                close(ssc);
            } catch (IOException e) {
                e.printStackTrace();
            }

            boundmap.remove(address);
        }

        unbindAddresses.clear();

        synchronized (lock) {
            endFlag = true;
            lock.notifyAll();
        }
    }

    private void bind0() {

        for (SocketAddress address : bindAddresses) {
            boolean success = false;
            try {
                bindByProtocol(address);
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (!success) {
                    rollback();
                    break;
                }
            }
        }

        bindAddresses.clear();

        synchronized (lock) {
            endFlag = true;
            lock.notifyAll();
        }
    }

    private void rollback() {

        Set<SocketAddress> boundAddress = boundmap.keySet();

        for (SocketAddress address : boundAddress) {
            try {
                close(boundmap.get(address));
            } catch (IOException e) {
                e.printStackTrace();
            }
            boundmap.remove(address);
        }
    }

    private void close(SelectableChannel ssc) throws IOException {

        SelectionKey key = ssc.keyFor(selector);
        key.cancel();

        ssc.close();
    }

    private void shutdown0() throws IOException {
        selector.close();
        pool.shutdown();
        super.shutdown();
    }


    //抽象方法
    protected abstract void acceptByProtocol(SelectionKey key);
    protected abstract void bindByProtocol(SocketAddress address) throws IOException;
}
