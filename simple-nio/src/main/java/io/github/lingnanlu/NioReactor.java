package io.github.lingnanlu;

import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

/**
 * Created by rico on 2017/1/16.
 */
abstract public class NioReactor implements IoReactor{

    protected IoHandler handler;                                //事件处理器
    protected NioChannelEventDispatcher dispatcher;             //事件分发器，分发事件给处理器执行
    protected NioBufferSizePredictorFactory predictorFactory;

    public NioChannelEventDispatcher getDispatcher() {
        return dispatcher;
    }
    public NioBufferSizePredictorFactory getPredictorFactory() {
        return predictorFactory;
    }

    @Override
    public void shutdown() {
        dispatcher.shutdown();
    }

    @Override
    public IoHandler getHandler() {
        return handler;
    }
}
