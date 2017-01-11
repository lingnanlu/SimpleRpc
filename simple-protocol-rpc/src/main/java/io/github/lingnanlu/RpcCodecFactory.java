package io.github.lingnanlu;

import io.github.lingnanlu.model.RpcMessage;

/**
 * Created by rico on 2017/1/11.
 */
public class RpcCodecFactory {


    public static ProtocolEncoder<RpcMessage> newRpcEncoder() {return new RpcEncoder();}
    public static ProtocolDecoder<RpcMessage> newRpcDecoder() {return new RpcDecoder();}

}
