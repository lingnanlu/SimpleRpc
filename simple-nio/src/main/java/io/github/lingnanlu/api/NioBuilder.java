package io.github.lingnanlu.api;

import io.github.lingnanlu.IoHandler;
import io.github.lingnanlu.NioAdaptiveBufferSizePredictorFactory;
import io.github.lingnanlu.NioOrderedDirectChannelEventDispatcher;
import io.github.lingnanlu.config.NioConfig;
import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;

/**
 * Created by rico on 2017/1/16.
 *
 * 学习Builder的写法
 *
 * 1. 类型参数为要构建的对象类型
 * 2. 有一个build方法，返回构建的对象
 * 3. 为对象的每个属性与组件设置默认值
 * 4. 为对象的每个属性与组件设置链式调用方法
 * 5. 无法设置默认值的作为Builder构造函数的参数
 * 6. 构建时只构建问题域内的对象与属性，也就是只构建最顶层的组件与属性，或者说构建系统视图的组件与属性
 */
abstract public class NioBuilder<T> {

    abstract public T build();

    protected final IoHandler handler;
    protected NioChannelEventDispatcher dispatcher = new NioOrderedDirectChannelEventDispatcher();
    protected NioBufferSizePredictorFactory predictorFactory = new NioAdaptiveBufferSizePredictorFactory();
    protected int readBufferSize = 2048;
    protected int minReadBufferSize = 64;
    protected int maxReadBufferSize = 65536;
    protected int ioTimeoutInMillis = 120 * 1000;
    protected int processorPoolSize = Runtime.getRuntime().availableProcessors();
    protected int executorSize = processorPoolSize << 3;
    protected int channelEventSize = Integer.MAX_VALUE;
    protected int totalEventSize = Integer.MAX_VALUE;

    public NioBuilder(IoHandler handler) {
        this.handler = handler;
    }

    public NioBuilder<T> minReadBufferSize(int size) {this.minReadBufferSize = size; return this;}
    public NioBuilder<T> maxReadBufferSize(int size) {this.maxReadBufferSize = size; return this;}
    public NioBuilder<T> readBufferSize(int size) {this.readBufferSize = size; return this;}
    public NioBuilder<T> processorPoolSize(int size) {this.processorPoolSize = size; return this;}
    public NioBuilder<T> executorSize(int size) {this.executorSize = size; return this;}
    public NioBuilder<T> channelEventSize(int size) {this.channelEventSize = size; return this;}
    public NioBuilder<T> totalEventSize(int size) {this.totalEventSize = size; return this;}
    public NioBuilder<T> ioTimeoutInMillis(int timeout) {this.ioTimeoutInMillis = timeout; return this;}
    public NioBuilder<T> dispatcher(NioChannelEventDispatcher dispatcher) {this.dispatcher = dispatcher; return this;}
    public NioBuilder<T> predictorFactory(NioBufferSizePredictorFactory factory) {this.predictorFactory = factory; return this;}


    protected void set(NioConfig config) {
        config.setTotalEventSize(totalEventSize);
        config.setChannelEventSize(channelEventSize);
        config.setExecutorSize(executorSize);
        config.setProcessorPoolSize(processorPoolSize);
        config.setDefaultReadBufferSize(readBufferSize);
        config.setMinReadBufferSize(minReadBufferSize);
    }

}
