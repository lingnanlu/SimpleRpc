package io.github.lingnanlu.schedule;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by rico on 2017/5/15.
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String name;
    private final boolean daemon;

    public NamedThreadFactory(String prefix, boolean daemon) {
        this.name = prefix + "-pool-thread-";
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, name + threadNumber.getAndIncrement());
        t.setDaemon(daemon);
        return t;
    }
}
