package io.github.lingnanlu.spi;

import java.util.Set;

/**
 * Created by rico on 2017/1/11.
 */
public interface RpcRegistry {

    void register(RpcApi api);

    void unregister(RpcApi api);

    RpcApi lookup(RpcApi api);

    Set<RpcApi> apis();
}
