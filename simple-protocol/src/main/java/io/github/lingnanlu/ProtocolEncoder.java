package io.github.lingnanlu;

/**
 * Created by rico on 2017/1/11.
 */
public interface ProtocolEncoder<P> {


    byte[] encode(P protocolObject);
}
