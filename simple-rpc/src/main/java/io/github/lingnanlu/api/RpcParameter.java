package io.github.lingnanlu.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by rico on 2017/1/10.
 */
@ToString
public class RpcParameter {

    @Getter @Setter private int rpcThreads = 1;
    @Getter @Setter private int rpcQueues = 10;

    public RpcParameter() {
    }

    public RpcParameter(int rpcThreads, int rpcQueues) {
        this.rpcThreads = rpcThreads;
        this.rpcQueues = rpcQueues;
    }


}
