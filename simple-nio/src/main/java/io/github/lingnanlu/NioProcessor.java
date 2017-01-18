package io.github.lingnanlu;

import io.github.lingnanlu.channel.ChannelEventType;
import io.github.lingnanlu.config.NioConfig;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by rico on 2017/1/16.
 */
public class NioProcessor extends NioReactor implements IoProcessor{


    private final Queue<NioByteChannel> newChannels = new ConcurrentLinkedQueue<>();
    private final Queue<NioByteChannel> flushingChannels = new ConcurrentLinkedQueue<>();
    private final Queue<NioByteChannel> closingChannels = new ConcurrentLinkedQueue<>();
    private final NioByteBufferAllocator allocator = new NioByteBufferAllocator();
    private ProcessThread pt;

    private IoProtocol  protocol;
    private final NioConfig config;
    private final Executor executor;
    private Selector selector;
    private boolean shutdown = false;


    public NioProcessor(NioConfig config, IoHandler handler, NioChannelEventDispatcher dispatcher) {
        this.config = config;

        //todo 这里的线程池是用来执行NioProcessor的，为什么需要一个线程池来执行呢
        this.executor = Executors.newCachedThreadPool();
        this.handler = handler;

        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public IoHandler getHandler() {
        return null;
    }

    public void add(NioByteChannel channel) {
        newChannels.add(channel);
        startup();
    }


    private void startup() {

        if (pt == null) {
            pt = new ProcessThread();

            executor.execute(pt);
        }
    }

    /*
    Processor运行在一个单独的线程当中, 由executor来执行
    这样写的好处是， 在使用者看来， Processor就是一个处理者，而不考虑其在哪个线程中执行，由Processor自己决定怎样执行
     */
    private class ProcessThread implements Runnable {

        @Override
        public void run() {
            while (!shutdown) {

                try {

                    int selected = selector.select();

                    register();

                    if (selected > 0) {
                        process();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private void register() throws ClosedChannelException {
        for(NioByteChannel channel = newChannels.poll(); channel != null; channel = newChannels.poll()) {
            SelectableChannel sc = channel.innerChannel();
            SelectionKey key = sc.register(selector, SelectionKey.OP_READ, channel);
            channel.setSelectionKey(key);

            fireChannelOpened(channel);
        }
    }



    private void process() {
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            NioByteChannel channel = (NioByteChannel) it.next().attachment();
            process0(channel);
            it.remove();
        }
    }

    private void process0(NioByteChannel channel) {

        if (channel.isReadable()) {
            read(channel);
        }


    }

    private void read(NioByteChannel channel) {

        int bufferSize= channel.getPredictor().next();

        ByteBuffer buf = allocator.allocate(bufferSize);

        int readBytes = 0;
        try {
            if (protocol.equals(IoProtocol.TCP)) {
                readBytes = readTcp(channel, buf);
            }

        } catch (IOException e) {

        } finally {
            if (readBytes > 0) {
                buf.clear();
            }
        }
    }

    private int readTcp(NioByteChannel channel, ByteBuffer buf) throws IOException {
        int readBytes = 0;
        int ret;
        while ((ret = channel.readTcp(buf)) > 0) {
            readBytes += ret;
            if (!buf.hasRemaining()) {
                break;
            }
        }

        if (readBytes > 0) {
            channel.getPredictor().previous(readBytes);
            fireChannelRead(channel, buf, readBytes);
        }

        return readBytes;
    }

    private void fireChannelRead(NioByteChannel channel, ByteBuffer buf, int length) {

        byte[] barr = new byte[length];
        System.arraycopy(buf.array(), 0, barr, 0, length);

        //交给Dispatcher来分发事件给IoHandler来处理， 至于在哪个线程中去处理， processor不关心， 由Dispatcher来决定
        dispatcher.dispatch(new NioByteChannelEvent(ChannelEventType.CHANNEL_READ, channel, handler, barr));

    }

    private void fireChannelOpened(NioByteChannel channel) {

        dispatcher.dispatch(new NioByteChannelEvent(ChannelEventType.CHANNEL_OPENED, channel, handler));
    }
}
