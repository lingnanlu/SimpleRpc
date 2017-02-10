package io.github.lingnanlu;

/**
 * Created by rico on 2017/2/10.
 */
public class IllegalChannelStateException extends IllegalStateException {

    public IllegalChannelStateException() {
        super();
    }

    public IllegalChannelStateException(String s) {
        super(s);
    }

    public IllegalChannelStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalChannelStateException(Throwable cause) {
        super(cause);
    }
}
