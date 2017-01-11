package io.github.lingnanlu.spi;

import io.craft.atom.protocol.rpc.model.RpcMessage;

import java.util.List;

/**
 * Created by rico on 2017/1/10.
 */
public interface RpcChannel {

    void write(RpcMessage msg);

    List<RpcMessage> read(byte[] bytes);

}
