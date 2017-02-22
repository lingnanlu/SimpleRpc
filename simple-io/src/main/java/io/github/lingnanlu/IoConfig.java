package io.github.lingnanlu;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rico on 2017/1/13.
 */
public abstract class IoConfig {

    public static final int MIN_READ_BUFFER_SIZE = 64;
    public static final int DEFAULT_READ_BUFFER_SIZE = 2048;
    public static final int MAX_READ_BUFFER_SIZE = 65536;

    @Getter @Setter int minReadBufferSize = MIN_READ_BUFFER_SIZE;
    @Getter @Setter int defaultReadBufferSize = DEFAULT_READ_BUFFER_SIZE;
    @Getter @Setter int maxReadBufferSize = MAX_READ_BUFFER_SIZE;
    @Getter @Setter int ioTimeoutInMillis = 5 * 1000;

}
