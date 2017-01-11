package io.github.lingnanlu;

import io.craft.atom.io.Channel;
import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.github.lingnanlu.spi.RpcProcessor;
import io.github.lingnanlu.spi.RpcProtocol;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by rico on 2017/1/10.
 */
public class RpcServerIoHandler extends RpcIoHandler {

    private RpcProtocol protocol;
    private RpcProcessor processor;

    public RpcServerIoHandler(RpcProtocol protocol, RpcProcessor processor) {
        this.protocol = protocol;
        this.processor = processor;
    }


    @Override
    public void channelOpened(Channel<byte[]> channel) {
        DefaultRpcChannel rpcChannel = new DefaultRpcChannel(protocol.getRpcEncoder(), protocol.getRpcDecoder(), channel);
        channel.setAttribute(RpcIoHandler.RPC_CHANNEL, rpcChannel);
    }

    @Override
    public void channelClosed(Channel<byte[]> channel) {
        super.channelClosed(channel);
    }

    @Override
    public void channelRead(Channel<byte[]> channel, byte[] bytes) {
        DefaultRpcChannel rpcChannel = (DefaultRpcChannel) channel.getAttribute(RpcIoHandler.RPC_CHANNEL);
        List<RpcMessage> reqs = rpcChannel.read(bytes);

        for (RpcMessage req : reqs) {
            req.setServerAddress((InetSocketAddress) channel.getLocalAddress());
            req.setClientAddress((InetSocketAddress) channel.getRemoteAddress());

            //处理的时候交由其它线程进行处理
            processor.process(req, rpcChannel);
        }


    }
}
