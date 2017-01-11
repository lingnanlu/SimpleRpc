package io.github.lingnanlu;

import io.craft.atom.io.Channel;
import io.craft.atom.io.IoHandler;

/**
 * Created by rico on 2017/1/10.
 */
public abstract class RpcIoHandler implements IoHandler {

    static final String RPC_CHANNEL = "rpc.channel";
    public void channelOpened(Channel<byte[]> channel) {

    }

    public void channelClosed(Channel<byte[]> channel) {

    }

    public void channelIdle(Channel<byte[]> channel) {

    }

    public void channelRead(Channel<byte[]> channel, byte[] bytes) {

    }

    public void channelFlush(Channel<byte[]> channel, byte[] bytes) {

    }

    public void channelWritten(Channel<byte[]> channel, byte[] bytes) {

    }

    public void channelThrown(Channel<byte[]> channel, Exception e) {

    }
}
