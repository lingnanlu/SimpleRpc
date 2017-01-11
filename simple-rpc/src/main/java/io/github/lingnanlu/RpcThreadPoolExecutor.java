package io.github.lingnanlu;

import io.craft.atom.util.thread.MonitoringExecutorService;
import io.craft.atom.util.thread.MonitoringThreadPoolExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by rico on 2017/1/11.
 */
public class RpcThreadPoolExecutor extends MonitoringThreadPoolExecutor{


    public RpcThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    @Override
    public String toString() {
        return String
                .format("RpcThreadPoolExecutor(" +
                                "corePoolSize=%s, " +
                                "maximumPoolSize=%s, " +
                                "keepAliveTime=%s, " +
                                "workQueueSize=%s, " +
                                "poolSize=%s, " +
                                "activeCount=%s, " +
                                "largestPoolSize=%s, " +
                                "taskCount=%s, " +
                                "completedTaskCount=%s)",
                        getCorePoolSize(),
                        getMaximumPoolSize(),
                        getKeepAliveTime(TimeUnit.SECONDS),
                        getQueue().size(),
                        getPoolSize(),
                        getActiveCount(),
                        getLargestPoolSize(),
                        getTaskCount(),
                        getCompletedTaskCount());
    }
}
