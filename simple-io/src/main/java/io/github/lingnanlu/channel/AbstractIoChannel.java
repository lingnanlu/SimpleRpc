package io.github.lingnanlu.channel;

import io.github.lingnanlu.IoConfig;
import lombok.Getter;

/**
 * Created by rico on 2017/1/13.
 */
public abstract class AbstractIoChannel extends AbstractChannel {


    @Getter protected int minReadBufferSize = IoConfig.MIN_READ_BUFFER_SIZE;
    @Getter protected int defaultReadBufferSize = IoConfig.DEFAULT_READ_BUFFER_SIZE;
    @Getter protected int maxReadBufferSize = IoConfig.MAX_READ_BUFFER_SIZE;
    @Getter protected int maxWriteBufferSize = maxReadBufferSize + (maxReadBufferSize >>> 1);

    public AbstractIoChannel() {
        super();
    }

    public AbstractIoChannel(int minReadBufferSize) {
        super();
        this.minReadBufferSize = minReadBufferSize;
    }

    public AbstractIoChannel(int minReadBufferSize, int defaultReadBufferSize) {
        super();
        this.minReadBufferSize     = minReadBufferSize    ;
        this.defaultReadBufferSize = defaultReadBufferSize;
    }

    public AbstractIoChannel(int minReadBufferSize, int defaultReadBufferSize, int maxReadBufferSize) {
        super();
        this.minReadBufferSize     = minReadBufferSize    ;
        this.defaultReadBufferSize = defaultReadBufferSize;
        this.maxReadBufferSize     = maxReadBufferSize    ;
    }

    public void setMinReadBufferSize(int minReadBufferSize) {
        if (minReadBufferSize <= 0) {
            throw new IllegalArgumentException("minReadBufferSize: " + minReadBufferSize + " (expected: 1+)");
        }

        if (minReadBufferSize > defaultReadBufferSize ) {
            throw new IllegalArgumentException("minReadBufferSize: " + minReadBufferSize + " (expected: smaller than " + defaultReadBufferSize + ')');
        }

        this.minReadBufferSize = minReadBufferSize;
    }

    public void setDefaultReadBufferSize(int defaultReadBufferSize) {
        if (defaultReadBufferSize < minReadBufferSize) {
            defaultReadBufferSize = this.minReadBufferSize;
        }

        if (defaultReadBufferSize > maxReadBufferSize) {
            defaultReadBufferSize = this.maxReadBufferSize;
        }

        this.defaultReadBufferSize = defaultReadBufferSize;
    }

    public void setMaxReadBufferSize(int maxReadBufferSize) {
        if (maxReadBufferSize <= 0) {
            throw new IllegalArgumentException("maxReadBufferSize: " + maxReadBufferSize + " (expected: > 1)");
        }

        if (maxReadBufferSize < defaultReadBufferSize) {
            throw new IllegalArgumentException("maxReadBufferSize: " + maxReadBufferSize + " (expected: greater than " + defaultReadBufferSize + ')');
        }

        this.maxReadBufferSize = maxReadBufferSize;
    }

    public void setMaxWriteBufferSize(int maxWriteBufferSize) {
        if (maxWriteBufferSize < maxReadBufferSize) {
            maxWriteBufferSize = maxReadBufferSize;
        }

        this.maxWriteBufferSize = maxWriteBufferSize;
    }

}
