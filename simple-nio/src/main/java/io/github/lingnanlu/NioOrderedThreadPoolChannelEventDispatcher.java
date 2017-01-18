package io.github.lingnanlu;

import io.github.lingnanlu.channel.ChannelEvent;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by rico on 2017/1/16.
 */
public class NioOrderedThreadPoolChannelEventDispatcher extends AbstractNioChannelEventDispatcher {

    private final BlockingQueue<NioByteChannel> channelQueue;
    private final ExecutorService executor;


    public NioOrderedThreadPoolChannelEventDispatcher() {
        this(Runtime.getRuntime().availableProcessors() * 8, Integer.MAX_VALUE);
    }
    public NioOrderedThreadPoolChannelEventDispatcher(int totalEventSize, int executorSize) {
        super(totalEventSize);

        if (executorSize <= 0) {
            executorSize = Runtime.getRuntime().availableProcessors() * 8;
        }

        this.channelQueue = new LinkedBlockingQueue<>();
        this.executor = Executors.newFixedThreadPool(executorSize);


        //启动多个工作者线程来分发事件
        for(int i = 0; i < executorSize; i++) {
            executor.execute(new Worker());
        }
    }

    @Override
    public void dispatch(ChannelEvent<byte[]> event) {

    }


    private class Worker implements Runnable{

        private void fire(NioByteChannel channel) {

            Queue<ChannelEvent<byte[]>> q = channel.getEventQueue();

            for(ChannelEvent<byte[]> event = q.poll(); event != null; event = q.poll()) {
                event.fire();
            }
        }

        @Override
        public void run() {

            try {
                for(NioByteChannel channel = channelQueue.take(); channel != null; channel = channelQueue.take()) {
                    fire(channel);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
