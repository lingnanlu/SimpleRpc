package io.github.lingnanlu;

import io.craft.atom.util.ByteArrayBuffer;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rico on 2017/1/11.
 */
abstract public class AbstractProtocolDecoder {

    protected static final int START = 0;
    protected static final int END = -1;

    @Getter @Setter protected int splitIndex = 0;
    @Getter @Setter protected int searchIndex = 0;
    @Getter @Setter protected int stateIndex = 0;
    @Getter @Setter protected int state = START;
    @Getter @Setter protected int defaultBufferSize = 2048;
    @Getter @Setter protected int maxSize = defaultBufferSize * 1024;
    @Getter @Setter protected ByteArrayBuffer buf = new ByteArrayBuffer(defaultBufferSize);

    protected void removeHandledBytesInBuf() {
        if (splitIndex > 0) {
            byte[] unHandledBytes = buf.array(splitIndex, buf.length());
            buf.clear();
            buf.append(unHandledBytes);
            splitIndex = 0;
            searchIndex = buf.length();
        }
    }
}
