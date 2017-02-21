package io.github.lingnanlu;

import io.github.lingnanlu.config.NioConfig;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by rico on 2017/1/16.
 */
public class NioTcpByteChannel extends NioByteChannel {

    private SocketChannel socketChannel;

    public NioTcpByteChannel(SocketChannel socketChannel, NioConfig config, NioBufferSizePredictor predictor, NioChannelEventDispatcher dispatcher) {
        super(config, predictor, dispatcher);

        this.socketChannel = socketChannel;
        this.localAddress = socketChannel.socket().getLocalSocketAddress();
        this.remoteAddress = socketChannel.socket().getRemoteSocketAddress();
    }

    //这两个方法最终也是由NioProcess调用，并不是直接调用
    //当对端Channel关闭时，会抛出异常
    //在这里不处理异常，向上报告
    @Override
    protected int readTcp(ByteBuffer buf) throws IOException {
        return socketChannel.read(buf);
    }

    @Override
    protected int writeTcp(ByteBuffer buf) throws IOException {
        return socketChannel.write(buf);
    }

    @Override
    public SelectableChannel innerChannel() {
        return socketChannel;
    }

    //这里是真正关闭Channel的地方，该方法不对外暴露，是由processor来调用的。
    //这里的异常要向Processor报告，应该Processor要处理该异常，分发异常事件
    @Override
    protected void close0() throws IOException {
        SelectionKey key = getSelectionKey();

        //关闭channel所要做的两件事
        key.cancel();
        socketChannel.close();
    }
}
