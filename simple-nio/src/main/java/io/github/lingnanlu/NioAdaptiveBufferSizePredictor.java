package io.github.lingnanlu;

/**
 * Created by rico on 2017/1/16.
 */
public class NioAdaptiveBufferSizePredictor implements NioBufferSizePredictor {
    @Override
    public int next() {
        return 0;
    }

    @Override
    public void previous(int readBytes) {

    }
}
