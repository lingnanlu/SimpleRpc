package io.github.lingnanlu;

import java.util.List;

/**
 * Created by rico on 2017/1/11.
 */
public interface ProtocolDecoder<P> {

    List<P> decode(byte[] bytes);
}
