package io.github.lingnanlu.channel;

import lombok.ToString;

/**
 * Created by rico on 2017/1/13.
 *
 * 只是特例化为Byte Channel
 */

@ToString(callSuper = true, of = {})
abstract public class AbstractIoByteChannel extends AbstractIoChannel implements Channel<byte[]> {

    public AbstractIoByteChannel() {
        super();
    }
    public AbstractIoByteChannel(int minReadBufferSize) {
        super(minReadBufferSize);
    }
    public AbstractIoByteChannel(int minReadBufferSize, int defaultReadBufferSize) {
        super(minReadBufferSize, defaultReadBufferSize);
    }
    public AbstractIoByteChannel(int minReadBufferSize, int defaultReadBufferSize, int maxReadBufferSize) {
        super(minReadBufferSize, defaultReadBufferSize, maxReadBufferSize);
    }
}
