package io.github.lingnanlu;

/**
 * Created by rico on 2017/1/16.
 */
public interface NioBufferSizePredictor {

    int next();

    void previous(int previousSize);
}
