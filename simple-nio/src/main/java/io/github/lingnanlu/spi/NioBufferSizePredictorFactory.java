package io.github.lingnanlu.spi;

import io.github.lingnanlu.NioBufferSizePredictor;

/**
 * Created by rico on 2017/1/16.
 */
public interface NioBufferSizePredictorFactory {

    NioBufferSizePredictor newPredictor(int mininum, int initial, int maximum);
}
