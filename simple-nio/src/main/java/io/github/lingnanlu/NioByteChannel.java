package io.github.lingnanlu;

import io.github.lingnanlu.channel.AbstractIoByteChannel;
import io.github.lingnanlu.channel.ChannelEvent;
import io.github.lingnanlu.channel.ChannelState;
import io.github.lingnanlu.config.NioConfig;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by rico on 2017/1/16.
 */

@ToString(callSuper = true, of = {"localAddress", "remoteAddress"})
abstract public class NioByteChannel extends AbstractIoByteChannel{

    protected final NioChannelEventDispatcher dispatcher;
    @Getter protected final NioBufferSizePredictor predictor;
    @Setter protected NioProcessor processor;                   //处理该channel的processor

    //该缓冲的目的是防止写入过快，导致channel写缓冲区已满，未缓冲的数据丢失
    @Getter protected final Queue<ByteBuffer> writeBufferQueue = new ConcurrentLinkedQueue<>();
    @Getter protected SocketAddress localAddress;
    @Getter @Setter protected SocketAddress remoteAddress;
    @Getter @Setter protected SelectionKey selectionKey;        //表示该channel在selector注册的Key

    protected final Object lock = new Object();

    public NioByteChannel(NioConfig config, NioBufferSizePredictor predictor, NioChannelEventDispatcher dispatcher) {
        super(config.getMinReadBufferSize(), config.getDefaultReadBufferSize(),config.getMaxReadBufferSize());
        this.predictor = predictor;
        this.dispatcher = dispatcher;
    }

    @Override
    public void close() {

        //同步操作是为了符合close的语义
        //参考JDK中Channel close方法的说明
        synchronized (lock) {
            if (isClosed() || isClosing()) {
                return;
            }
        }

        /*
        所谓的关闭channel需要做以下几件事
        1. 取消相关的selectionkey
        2. 关闭innerChannel
        这里channel的关闭实际上是交给了processor来处理的，而不是由其它任意线程来关闭, processor再回调close0()来进行真正的关闭操作
        也就是说close操作是在processor线程中执行的。
         */
        processor.remove(this);
    }

    //参考原代码，write操作也是委托给了NioProcessor，再回调writeTcp来进行实际的操作
    //该写操作只是把待写的内容写到缓冲队列中去，然后通知processor来进行flush
    @Override
    public boolean write(byte[] data) {

        if(isClosing()) { throw new IllegalChannelStateException("Channel is closing");}
        if(isClosed()) { throw new IllegalChannelStateException("Channel is closed");}
        if(isPaused()) { throw new IllegalChannelStateException("Channel is paused");}

        getWriteBufferQueue().add(ByteBuffer.wrap(data));
        processor.flush(this);
        return true;
    }

    //todo 该方法的作用？
    @Override
    public Queue<byte[]> getWriteQueue() {
        Queue<byte[]> q = new LinkedBlockingQueue<>();
        for (ByteBuffer buf : writeBufferQueue) {
            q.add(buf.array());
        }
        return q;
    }

    boolean isReadable() {

        return isOpen() && selectionKey.isValid() && selectionKey.isReadable();
    }
    boolean isWritable() {
        return (isOpen() || isPaused()) && selectionKey.isValid() && selectionKey.isWritable();
    }

    public void setClosing() { state = ChannelState.CLOSING; }
    public void setClosed() { state = ChannelState.CLOSED; }

    //--------------------------method to be override-------------------------//
    public abstract SelectableChannel innerChannel();
    protected int readTcp(ByteBuffer buf) throws IOException {return 0;}
    protected int writeTcp(ByteBuffer buf) throws IOException {return 0;}
    protected void close0() throws IOException {}

    public Queue<ChannelEvent<byte[]>> getEventQueue() {
        return null;
    }
}
