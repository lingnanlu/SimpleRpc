package io.github.lingnanlu;

import io.github.lingnanlu.spi.RpcApi;
import io.github.lingnanlu.spi.RpcRegistry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rico on 2017/1/11.
 */
public class DefaultRpcRegistry implements RpcRegistry {

    private Map<String, RpcApi> registry = new ConcurrentHashMap<String, RpcApi>();

    public void register(RpcApi api) {
        registry.put(api.getKey(), api);
    }

    public void unregister(RpcApi api) {
        registry.remove(api.getKey());
    }

    public RpcApi lookup(RpcApi api) {
        return registry.get(api.getKey());
    }

    public Set<RpcApi> apis() {
        return null;
    }
}
