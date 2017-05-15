package io.github.lingnanlu;

import io.github.lingnanlu.channel.ChannelEventType;
import io.github.lingnanlu.config.NioConfig;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static final Logger LOG = LoggerFactory.getLogger(NioProcessor.class);

    private final NioByteBufferAllocator allocator = new NioByteBufferAllocator();
    private final NioChannelIdleTimer idleTimer;

    private final NioConfig config;
    private boolean shutdown = false;
    private final String name;
    //这些组件只所以是并发的，是考虑到实体之间的协作问题
    //当一个组件是否需要选用并发的版本时，要考虑是否有多个实体在之上进行协作
    private final Queue<NioByteChannel> newChannels = new ConcurrentLinkedQueue<>();
    private final Queue<NioByteChannel> flushingChannels = new ConcurrentLinkedQueue<>();
    private final Queue<NioByteChannel> closingChannels = new ConcurrentLinkedQueue<>();
    private Selector selector;

    public NioProcessor(String name, NioConfig config, IoHandler handler, NioChannelEventDispatcher dispatcher, NioChannelIdleTimer idleTimer) throws IOException {

        this.name = name;
        this.config = config;
        this.handler = handler;
        this.dispatcher = dispatcher;
        this.idleTimer = idleTimer;
        LOG.info("[Simple-NIO] processor assemble");

        init();
        LOG.info("[Simple-NIO] processor init");
        startup();
        LOG.info("[Simple-NIO] processor start");
    }



    private void init() throws IOException {
        selector = Selector.open();
    }

    private void startup() {
        new ProcessorThread(name).start();
    }

    private class ProcessorThread extends Thread {

        public ProcessorThread(String name) {
            super(name);
        }

        @Override
        public void run() {

            while (!shutdown) {

                try {

                    int selected = selector.select();

                    register();

                    flush();
                    if (selected > 0) {
                        process();
                    }

                    close();
                } catch (IOException e) {
                    LOG.info("[Simple-NIO] process failed", e);
                }
            }

            try {
                shutdown0();
            } catch (IOException e) {
                LOG.info("[Simple-NIO] shutdown failed", e);
            }
        }
    }

    @Override
    public void shutdown() {
        this.shutdown = true;
        wakeUp();
    }

    @Override
    public IoHandler getHandler() {
        return null;
    }


    //--------------------------这些方法是提供给另一个实体来操作本实体的方法，所以这里要考虑实体之间的协作问题-------------------------//
    public void flush(NioByteChannel channel) {
        scheduleFlush(channel);
        wakeUp();
    }
    public void remove(NioByteChannel channel) {
        scheduleClose(channel);
        wakeUp();
    }

    public void add(NioByteChannel channel) {
        newChannels.add(channel);
        //因为processor可能因为selector上没且就绪的channel而阻塞，所以需要唤醒它
        wakeUp();
    }



    //--------------------------内部方法-------------------------//
    private void shutdown0() throws IOException {

        //关闭那些待注册的channel
        closingChannels.addAll(newChannels);
        newChannels.clear();

        close();

        if (selector != null) {
            selector.close();
        }
    }

    private void wakeUp() {
        selector.wakeup();
    }

    private void scheduleClose(NioByteChannel channel) {
        closingChannels.add(channel);
    }
    private void scheduleFlush(NioByteChannel channel) {
        flushingChannels.add(channel);
    }

    private void flush() {
        for(NioByteChannel channel = flushingChannels.poll(); channel != null; channel = flushingChannels.poll()) {
            if (channel.isClosed() || channel.isClosing()) {
                continue;
            } else {
                try {
                    flush0(channel);
                } catch (IOException e) {
                    LOG.info("[Simple-NIO] Catch flush exception and fire it " + e);

                    fireChannelThrown(channel, e);

                    scheduleClose(channel);
                }
            }
        }
    }

    private void flush0(NioByteChannel channel) throws IOException {

        Queue<ByteBuffer> writeBuffers = channel.getWriteBufferQueue();

        setInterestedInWrite(channel, false);

        //每次缓冲时，试着将所有的buffer写入到网络缓冲区
        Iterator<ByteBuffer> it = writeBuffers.iterator();
        while (it.hasNext()) {
            ByteBuffer buf = it.next();
            fireChannelFlush(channel, buf);

            //向网络缓冲区写入buf中剩余的元素，如果没写完，说明网络缓冲区已满
            boolean finish = write(channel, buf, buf.remaining());

            if (finish) {
                fireChannelWritten(channel, buf);
                it.remove();
            } else {
                //如果没写完，说明网络缓冲区已满， 这种做三件事
                //1. 注册OP_WRITE
                //2. 停止写入后序的缓冲
                //3. 当可写时，再scheduleFlush(channel)， 这项操作在process中进行
                setInterestedInWrite(channel, true);
                break;
            }
        }
    }

    private void setInterestedInWrite(NioByteChannel channel, boolean isInterested) {
        SelectionKey key = channel.getSelectionKey();

        int interestOps = key.interestOps();

        if (isInterested) {
            interestOps |= SelectionKey.OP_WRITE;
        } else {
            interestOps &= ~SelectionKey.OP_WRITE;
        }
        key.interestOps(interestOps);

    }

    private boolean write(NioByteChannel channel, ByteBuffer buf, int remainLength) throws IOException {

        int writtenBytes = channel.writeTcp(buf);
        if (writtenBytes < remainLength) {
            return false;
        }
        return true;
    }

    private void close() {
        for(NioByteChannel channel = closingChannels.poll(); channel != null; channel = closingChannels.poll()) {
            idleTimer.remove(channel);
            if (channel.isClosed()) {
                LOG.info("[Simple-NIO] skip close because it is already closed " + channel);
                continue;
            }
            channel.setClosing();
            LOG.info("[Simple-NIO] closing channel = " + channel);
            close(channel);
            channel.setClosed();
            fireChannelClosed(channel);
            LOG.info("[Simple-NIO] closed channel = " + channel);
        }
    }

    private void close(NioByteChannel channel) {
        try {
            channel.close0();
        } catch (IOException e) {
            LOG.info("[Simple-NIO] catch close exception and fire it");
            fireChannelThrown(channel, e);
        }
    }


    //register , flush , process, close等异常，除了做必要的资源清理外， 统一在run中报告
    private void register() throws ClosedChannelException {
        for(NioByteChannel channel = newChannels.poll(); channel != null; channel = newChannels.poll()) {
            SelectableChannel sc = channel.innerChannel();
            SelectionKey key = sc.register(selector, SelectionKey.OP_READ, channel);
            channel.setSelectionKey(key);
            idleTimer.add(channel);
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

        channel.setLastIoTime(System.currentTimeMillis());

        if (channel.isReadable()) {
            LOG.info("[Simple-NIO] read event process on channel = " + channel);
            read(channel);
        }

        if (channel.isWritable()) {
            LOG.info("[Simple-NIO] write event process on channel = " + channel);
            //如果可写，再scheduleFlush
            scheduleFlush(channel);
        }
    }

    private void read(NioByteChannel channel) {

        int bufferSize= channel.getPredictor().next();

        ByteBuffer buf = allocator.allocate(bufferSize);

        int readBytes = 0;
        try {
            readBytes = read(channel, buf);
        } catch (IOException e) {

            //当读channel异常时，触发事件
            LOG.info("[Simple-NIO] catch read exception and fire it");
            fireChannelThrown(channel, e);

            //when remote peer close, read operation will throw IOException, so here the channel should be closed
            scheduleClose(channel);
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
            LOG.info("[Simple-NIO] actual readbytes = " + readBytes);
        }

        // read end of stream , remote peer may close channel so close channel
        if(ret < 0) {
            scheduleClose(channel);
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

    private void fireChannelWritten(NioByteChannel channel, ByteBuffer buf) {
        dispatcher.dispatch(new NioByteChannelEvent(ChannelEventType.CHANNEL_WRITTEN, channel, handler, buf.array()));
    }

    private void fireChannelClosed(NioByteChannel channel) {
        dispatcher.dispatch(new NioByteChannelEvent(ChannelEventType.CHANNEL_CLOSED, channel, handler));
    }

    private void fireChannelFlush(NioByteChannel channel, ByteBuffer buf) {
        dispatcher.dispatch(new NioByteChannelEvent(ChannelEventType.CHANNEL_FLUSH, channel, handler, buf.array()));
    }

    private void fireChannelThrown(NioByteChannel channel, IOException e) {
        dispatcher.dispatch(new NioByteChannelEvent(ChannelEventType.CHANNEL_THROWN, channel, handler, e));
    }


}
