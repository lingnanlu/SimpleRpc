package io.github.lingnanlu.spi;

import io.craft.atom.protocol.ProtocolDecoder;
import io.craft.atom.protocol.ProtocolEncoder;
import io.craft.atom.protocol.rpc.model.RpcMessage;

/**
 * Created by rico on 2017/1/10.
 */
public interface RpcProtocol {

    ProtocolEncoder<RpcMessage> getRpcEncoder();
    ProtocolDecoder<RpcMessage> getRpcDecoder();
}
