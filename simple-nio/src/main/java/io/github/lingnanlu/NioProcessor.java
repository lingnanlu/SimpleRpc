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

    private final NioByteBufferAllocator allocator = new NioByteBufferAllocator();

    private final NioConfig config;
    private boolean shutdown = false;

    private final Queue<NioByteChannel> newChannels = new ConcurrentLinkedQueue<>();
    private final Queue<NioByteChannel> closingChannels = new ConcurrentLinkedQueue<>()    ;
    private Selector selector;

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

    private void startup() {
        new ProcessorThread().start();
    }




    private class ProcessorThread extends Thread {

        @Override
        public void run() {

            while (!shutdown) {

                try {
                    //这里,相对于原来的代码,做了简化处理。为channel注册OP_WRITE & OP_READ
                    //processor只做两件事
                    //1. 注册新的channel
                    //2. 处理就绪channel
                    //这里channel会先将内容写入到自己的queue中，然后处理写channel时，将queue中的所有东西一并写入
                    register();

                    int selected = selector.select();

                    if (selected > 0) {
                        process();
                    }

                    closeChannels();
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

    @Override
    public void shutdown() {
        this.shutdown = true;
        selector.wakeup();
    }

    @Override
    public IoHandler getHandler() {
        return null;
    }

    public void remove(NioByteChannel channel) {
        scheduleClose(channel);
        selector.wakeup();
    }

    public void add(NioByteChannel channel) {
        newChannels.add(channel);

        //因为processor可能因为selector上没且就绪的channel而阻塞，所以需要唤醒它
        selector.wakeup();
    }



    //--------------------------内部方法-------------------------//
    private void shutdown0() throws IOException {

        //关闭那些待注册的channel
        closingChannels.addAll(newChannels);
        newChannels.clear();

        closeChannels();
        selector.close();
    }

    private void scheduleClose(NioByteChannel channel) {
        closingChannels.add(channel);
    }

    private void closeChannels() {
        for(NioByteChannel channel = closingChannels.poll(); channel != null; channel = closingChannels.poll()) {
            channel.setClosing();
            close(channel);

            channel.setClosed();
            fireChannelClosed(channel);
        }
    }

    private void close(NioByteChannel channel) {
        try {
            channel.close0();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void register()  {
        for(NioByteChannel channel = newChannels.poll(); channel != null; channel = newChannels.poll()) {
            SelectableChannel sc = channel.innerChannel();
            SelectionKey key = null;
            try {
                key = sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE , channel);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
            channel.setSelectionKey(key);
            fireChannelOpened(channel);
        }
    }

    private void process() throws IOException {
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            NioByteChannel channel = (NioByteChannel) it.next().attachment();
            processEach(channel);
            it.remove();
        }
    }

    private void processEach(NioByteChannel channel) throws IOException {
        if (channel.isReadable()) {
            read(channel);
        }

        if (channel.isWritable()) {
            write(channel);
        }
    }

    private void write(NioByteChannel channel) throws IOException {
        Queue<ByteBuffer> writeBuffers = channel.getWriteBufferQueue();
        for(ByteBuffer buf = writeBuffers.poll(); buf != null; buf = writeBuffers.poll()) {
            channel.writeTcp(buf);
            fireChannelWritten(channel, buf);
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


    //事件触发方法
    private void fireChannelRead(NioByteChannel channel, ByteBuffer buf, int length) {
        byte[] barr = new byte[length];
        System.arraycopy(buf.array(), 0, barr, 0, length);
        //交给Dispatcher来分发事件给IoHandler来处理， 至于在哪个线程中去处理， processor不关心， 由Dispatcher来决定
        dispatcher.dispatch(new NioByteChannelEvent(ChannelEventType.CHANNEL_READ, channel, handler, barr));
    }

    private void fireChannelOpened(NioByteChannel channel) {
        dispatcher.dispatch(new NioByteChannelEvent(ChannelEventType.CHANNEL_OPENED, channel, handler));
    }

    private void fireChannelWritten(NioByteChannel channel, ByteBuffer buf) {
        dispatcher.dispatch(new NioByteChannelEvent(ChannelEventType.CHANNEL_WRITTEN, channel, handler, buf.array()));
    }

    private void fireChannelClosed(NioByteChannel channel) {
        dispatcher.dispatch(new NioByteChannelEvent(ChannelEventType.CHANNEL_CLOSED, channel, handler));
    }

}
