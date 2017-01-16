package io.github.lingnanlu;

/**
 * Created by rico on 2017/1/13.
 */
public abstract class NioReactor implements IoReactor {


    protected IoHandler handler;
    protected NioChannelEventDispatcher dispatcher;
    protected NioProcessorPool pool;

    public void shutdown() {
        dispatcher.shutdown();
        pool.shutdown();
    }

    public IoHandler getHandler() {
        return handler;
    }


    public NioChannelEventDispatcher getDispatcher() {
        return dispatcher;
    }

}
