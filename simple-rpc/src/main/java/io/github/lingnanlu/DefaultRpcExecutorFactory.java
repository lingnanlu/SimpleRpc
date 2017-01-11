package io.github.lingnanlu;

import io.craft.atom.util.thread.MonitoringExecutorService;
import io.craft.atom.util.thread.NamedThreadFactory;
import io.github.lingnanlu.api.RpcParameter;
import io.github.lingnanlu.spi.RpcApi;
import io.github.lingnanlu.spi.RpcExecutorFactory;
import io.github.lingnanlu.spi.RpcRegistry;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by rico on 2017/1/11.
 */
public class DefaultRpcExecutorFactory implements RpcExecutorFactory{

    @Getter @Setter private RpcRegistry registry;
    @Getter @Setter private Map<String, MonitoringExecutorService> pool;

    public MonitoringExecutorService getExecutor(RpcApi api) {
        return getExecutor0(api);
    }

    private MonitoringExecutorService getExecutor0(RpcApi queryApi) {

        String key = queryApi.getKey();
        MonitoringExecutorService es = pool.get(key);

        if (es == null) {
            RpcApi api = registry.lookup(queryApi);

            RpcParameter parameter = api.getRpcParameter();
            int threads = parameter.getRpcThreads() == 0 ? 1 : parameter.getRpcThreads();
            int queues = parameter.getRpcThreads() == 0 ? 1 : parameter.getRpcQueues();

            RpcThreadPoolExecutor tpe =
                    new RpcThreadPoolExecutor(threads, threads, 60, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(queues), new NamedThreadFactory("craft-atom-rpc"));

            es = tpe;

            pool.put(key, tpe);
        }
        return es;
    }
}
