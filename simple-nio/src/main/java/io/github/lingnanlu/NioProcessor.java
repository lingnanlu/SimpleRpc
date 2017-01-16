package io.github.lingnanlu;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;

/**
 * Created by rico on 2017/1/16.
 *
 * 关键在这里， Processor是怎样处理Channel的， 以及与Handler的不同
 */
public class NioProcessor extends NioReactor implements IoProcessor{

    private final Queue<NioByteChannel> newChannels = new ConcurrentLinkedDeque<>();
    private ProcessThread processThread;
    private Executor executor;
    private boolean shutdown = false;
    public void add(NioByteChannel channel) {

        newChannels.add(channel);

        startup();
    }

    private void wakeup() {

    }

    private void startup() {

        if (processThread == null) {
            processThread = new ProcessThread();
        }

        executor.execute(processThread);

    }

    private class ProcessThread implements Runnable {

        @Override
        public void run() {

        }
    }
}
