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

/**
 * Created by rico on 2017/1/16.
 */
public class NioProcessor extends NioReactor implements IoProcessor{

    private final Queue<NioByteChannel> newChannels = new ConcurrentLinkedQueue<>();
    private final NioByteBufferAllocator allocator = new NioByteBufferAllocator();
    private final NioConfig config;
    private Selector selector;
    private boolean shutdown = false;

    public NioProcessor(NioConfig config, IoHandler handler, NioChannelEventDispatcher dispatcher) throws IOException {

        this.config = config;
        this.handler = handler;
        this.dispatcher = dispatcher;
        init();
        startup();

    }

    private void init() throws IOException {
        selector = Selector.open();
    }

    @Override
    public void shutdown() {
        this.shutdown = true;
    }

    @Override
    public IoHandler getHandler() {
        return null;
    }

    public void add(NioByteChannel channel) {
        newChannels.add(channel);

        //因为processor可能因为selector上没且就绪的channel而阻塞，所以需要唤醒它
        selector.wakeup();
    }

    private void startup() {
        new ProcessorThread().start();
    }

    public void flush(NioByteChannel channel) {
    }

    private class ProcessorThread extends Thread {

        @Override
        public void run() {

            while (!shutdown) {

                try {

                    register();

                    int selected = selector.select();

                    if (selected > 0) {
                        process();
                    }
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

    private void shutdown0() throws IOException {
        selector.close();
        newChannels.clear();

        //todo 在这里关闭所有的channel
    }


    private void register()  {
        for(NioByteChannel channel = newChannels.poll(); channel != null; channel = newChannels.poll()) {
            SelectableChannel sc = channel.innerChannel();
            SelectionKey key = null;
            try {
                key = sc.register(selector, SelectionKey.OP_READ, channel);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
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
            readBytes = read(channel, buf);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (readBytes > 0) {
                buf.clear();
            }
        }
    }

    private int read(NioByteChannel channel, ByteBuffer buf) throws IOException {
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
