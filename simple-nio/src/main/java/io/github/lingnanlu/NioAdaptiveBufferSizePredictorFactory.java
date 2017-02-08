package io.github.lingnanlu;

import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;

/**
 * Created by rico on 2017/1/16.
 */
public class NioAdaptiveBufferSizePredictorFactory implements NioBufferSizePredictorFactory {
    @Override
    public NioBufferSizePredictor newPredictor(int mininum, int initial, int maximum) {
        return new NioAdaptiveBufferSizePredictor(mininum, initial, maximum);
    }
}
