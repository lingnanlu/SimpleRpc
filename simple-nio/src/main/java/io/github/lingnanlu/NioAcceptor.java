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
 * 该Acceptor只能绑定一个port,并且不能unbind
 */
abstract public class NioAcceptor extends NioReactor implements IoAcceptor {

    protected final Set<SocketAddress> bindAddresses = new HashSet<>();
    protected final Map<SocketAddress, SelectableChannel> boundmap = new ConcurrentHashMap<>();
    protected final Object lock = new Object();
    protected boolean bindEnd = false;
    protected NioAcceptorConfig config;
    protected Selector selector;
    protected boolean shutdwon = false;
    protected NioProcessorPool pool;

    //不绑定到任何端口的构造器
    public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) throws IOException {

        this.handler = handler;
        this.config = (config == null ? new NioAcceptorConfig() : config);
        this.dispatcher = dispatcher;
        this.bufferSizePredictorFactory = predictorFactory;
        this.pool = new NioProcessorPool(config, handler, dispatcher);

        init();
    }

    private void init() throws IOException {
        selector = Selector.open();
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


    private void accept() {
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            it.remove();
            acceptByProtocol(key);
        }
    }

    protected abstract void acceptByProtocol(SelectionKey key);

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

        for (SocketAddress address : otherLocalAddresses) {
            localAddresses.add(address);
        }

        bindAddresses.addAll(localAddresses);


        synchronized (lock) {
            selector.wakeup();

            waitForAddressedBind();
        }


//        bindByProtocol(firstLocalAddress);
//        new AcceptorThread().start();

    }

    @Override
    public void unbind(int port) {

    }

    @Override
    public void unbind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {

    }

    @Override
    public Set<SocketAddress> getBoundAddresses() {
        return null;
    }

    private void waitForAddressedBind() {
        while (!bindEnd) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    protected abstract void bindByProtocol(SocketAddress address) throws IOException;

    private class AcceptorThread extends Thread {
        @Override
        public void run() {
            while (!shutdwon) {
                try {
                    int selected = selector.select();

                    if (selected > 0) {
                        accept();
                    }

                    bind0();
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
            bindEnd = true;
            lock.notifyAll();
        }
    }

    private void rollback() {

        Iterator<Map.Entry<SocketAddress, SelectableChannel>> it = boundmap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<SocketAddress, SelectableChannel> entry = it.next();

            try {
                close(entry.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                it.remove();
            }
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

    @Override
    public void shutdown() throws IOException {
        this.shutdwon = true;
    }
}
