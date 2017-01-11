package io.github.lingnanlu;

import io.craft.atom.protocol.rpc.model.RpcMessage;
import io.craft.atom.protocol.rpc.model.RpcMethod;
import io.craft.atom.util.thread.MonitoringExecutorService;
import io.github.lingnanlu.spi.*;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by rico on 2017/1/11.
 */
public class DefaultRpcProcessor implements RpcProcessor {

    @Getter @Setter private RpcInvoker invoker;

    //封装了执行策略， 也是一种组件， 需要装配
    @Getter @Setter private RpcExecutorFactory executorFactory;
    @Getter @Setter private ExecutorService timeoutExecutor;

    public void process(RpcMessage req, RpcChannel channel) {

        RpcApi api = api(req);
        MonitoringExecutorService executor = executor(api);
        executor.execute(new ProcessTask(req, channel));
    }

    private MonitoringExecutorService executor(RpcApi api) {
        return executorFactory.getExecutor(api);
    }

    private RpcApi api(RpcMessage req) {
        String rpcId = req.getBody().getRpcId();
        RpcMethod rpcMethod = req.getBody().getRpcMethod();
        Class<?> rpcInterface = req.getBody().getRpcInterface();
        DefaultRpcApi api = new DefaultRpcApi(rpcId, rpcInterface, rpcMethod);
        return api;
    }

    public void close() {

    }

    private RpcMessage process0(RpcMessage req) {
        RpcMessage rsp = invoker.invoke(req);
        return rsp;
    }
    private class ProcessTask implements Runnable {

        private RpcMessage req;
        private RpcChannel channel;

        public ProcessTask(RpcMessage req, RpcChannel channel) {
            this.req = req;
            this.channel = channel;
        }

        public void run() {
            RpcMessage rsp = null;

            try {
                Future<RpcMessage> future = timeoutExecutor.submit(new Callable<RpcMessage>() {
                    public RpcMessage call() throws Exception {
                        return process0(req);
                    }
                });

                rsp = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            channel.write(rsp);
        }
    }
}
