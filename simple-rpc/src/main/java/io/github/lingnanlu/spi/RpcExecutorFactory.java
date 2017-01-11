package io.github.lingnanlu.spi;

import io.craft.atom.util.thread.MonitoringExecutorService;

/**
 * Created by rico on 2017/1/11.
 */
public interface RpcExecutorFactory {

    MonitoringExecutorService getExecutor(RpcApi api);

    void setRegistry(RpcRegistry registry);
}
