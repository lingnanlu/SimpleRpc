package io.github.lingnanlu;

import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;
import lombok.Getter;

import java.io.IOException;

/**
 * Created by rico on 2017/1/16.
 */
abstract public class NioReactor implements IoReactor{

    @Getter protected IoHandler handler;                                //事件处理器
    @Getter protected NioChannelEventDispatcher dispatcher;             //事件分发器，分发事件给处理器执行
    @Getter protected NioBufferSizePredictorFactory bufferSizePredictorFactory;
    protected NioProcessorPool pool;

    @Override
    public void shutdown() throws IOException {

        //之所以shutdown这两个，是因为这两个组件需要关闭，而predictorFactory，
        //todo 所以要分清楚两类组件 ，一个是需要关闭的，另一类是不需要关闭的
        dispatcher.shutdown();
        pool.shutdown();
    }

}
