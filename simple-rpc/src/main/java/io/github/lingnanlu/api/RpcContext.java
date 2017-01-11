package io.github.lingnanlu.api;

/**
 * Created by rico on 2017/1/10.
 *
 * RPC context is a thread local context
 *
 * Each rpc invocation bind a context instance to current thread.
 *
 * 主要用来为RpcMessage添加一些meta信息， 如是否是oneway, 是否是同步等等
 */
public class RpcContext {
    public static RpcContext getContext() {
        return null;
    }

    public boolean isAsync() {
        return false;
    }
}
