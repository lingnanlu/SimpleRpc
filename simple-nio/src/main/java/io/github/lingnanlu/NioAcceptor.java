package io.github.lingnanlu;

import io.github.lingnanlu.config.NioAcceptorConfig;
import io.github.lingnanlu.spi.NioBufferSizePredictorFactory;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rico on 2017/1/16.
 *
 * 要注意这里的协作问题
 *
 * Client与Acceptor进行协作
 *
 * bindAddresses 与 unbindAddresses是共享资源，要互斥访问
 *
 * 当Client对bindAddresses或unbindAddresses进行操作之后，修改条件，唤醒Acceptor进行操作
 */
abstract public class NioAcceptor extends NioReactor implements IoAcceptor {

    public static final Logger LOG = LoggerFactory.getLogger(NioAcceptor.class);

    //组件
    protected NioProcessorPool pool;

    //属性
    protected NioAcceptorConfig config;
    protected boolean shutdown = false;

    //成员
    protected final Set<SocketAddress> bindAddresses = new HashSet<>();
    protected final Set<SocketAddress> unbindAddresses = new HashSet<>();
    protected final Map<SocketAddress, SelectableChannel> boundmap = new ConcurrentHashMap<>();
    protected Selector selector;
    protected IOException exception;

    //辅助
    protected final Object lock = new Object();
    protected boolean endFlag = false;


    //只要对象构造成功，在运行过程中，不再申请新的资源
    public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) throws IOException {

        //组装
        this.handler = handler;
        this.config = (config == null ? new NioAcceptorConfig() : config);
        this.dispatcher = dispatcher;
        this.bufferSizePredictorFactory = predictorFactory;
        this.pool = new NioProcessorPool(config, handler, dispatcher);
        LOG.info("[Simple-NIO] acceptor assemble");


        //初始化,如果失败的话，只要告诉调用端失败即可，由调用端负责关闭资源
        init();
        LOG.info("[Simple-NIO] acceptor init");

        //启动
        startup();
        LOG.info("[Simple-NIO] acceptor start");
    }

    public NioAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher) throws IOException {
        this(handler, config, dispatcher, new NioAdaptiveBufferSizePredictorFactory());
    }

    public NioAcceptor(IoHandler handler, NioAcceptorConfig config) throws IOException {
        this(handler, config, new NioOrderedDirectChannelEventDispatcher(), new NioAdaptiveBufferSizePredictorFactory());
    }

    public NioAcceptor(IoHandler handler) throws IOException {
        this(handler, new NioAcceptorConfig(), new NioOrderedDirectChannelEventDispatcher(), new NioAdaptiveBufferSizePredictorFactory());
    }

    private void startup() {
        new AcceptorThread().start();
    }

    private void init() throws IOException {
        selector = Selector.open();
    }

    //工作流程
    private class AcceptorThread extends Thread {
        @Override
        public void run() {
            while (!shutdown) {
                try {
                    int selected = selector.select();
                    if (selected > 0) {
                        accept();
                    }
                    bind0();
                    unbind0();
                } catch (IOException e) {

                    //该异常不应该通知给调用端， 因为调用端也不知到如何处理
                    //当然也可以通知调用端，调用端所做的只是输出异常信息
                    LOG.info("[Simple-NIO] Unexpected exception", e);
                }
            }

            try {
                shutdown0();
            } catch (IOException e) {
                LOG.info("[Simple-NIO] shutdown exception", e);
            }
        }
    }

    @Override
    public void bind(int port) throws IOException {
        bind(new InetSocketAddress(port));
    }

    //当bind错误时，这里不做任何处理，仅仅通知用户即可
    @Override
    public void bind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) throws IOException {

        if (firstLocalAddress != null) {
            //这里是一个线程协作问题
            synchronized (lock) {
            /*
            bindAddresses是共享资源，所以先加锁，对共享资源进行操作
             */
                addToBindAddresses(firstLocalAddress, otherLocalAddresses);

                //操作完后，释放锁，等待另一线程操作
                if (!bindAddresses.isEmpty()) {
                    wakeUp();
                    wait0();
                }

            }
        }
    }

    private void addToBindAddresses(SocketAddress firstLocalAddress, SocketAddress[] otherLocalAddresses) {

        List<SocketAddress> localAddresses = new ArrayList<>();
        bindAddresses.add(firstLocalAddress);

        for (SocketAddress address : otherLocalAddresses) {
            localAddresses.add(address);
        }

        bindAddresses.addAll(localAddresses);
    }

    @Override
    public void unbind(int port) throws IOException {
        unbind(new InetSocketAddress(port));
    }

    //同bind， 当unbind异常时，通知调用者
    @Override
    public void unbind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) throws IOException {

        if (firstLocalAddress != null) {
            synchronized (lock) {
                addToUnbindAddresses(firstLocalAddress, otherLocalAddresses);
                if (!unbindAddresses.isEmpty()) {
                    wakeUp();
                    wait0();
                }
            }
        }
    }

    private void addToUnbindAddresses(SocketAddress firstLocalAddress, SocketAddress[] otherLocalAddresses) {

        List<SocketAddress> localAddresses = new ArrayList<>();

        if (boundmap.containsKey(firstLocalAddress)) {
            localAddresses.add(firstLocalAddress);
        }
        for (SocketAddress address : otherLocalAddresses) {
            if (boundmap.containsKey(address)) {
                localAddresses.add(address);
            }
        }
        unbindAddresses.addAll(localAddresses);
        return;
    }

    @Override
    public void shutdown(){
        //凡是客户端调用修改了Acceptor的某种状态的，都不要忘记selector.wakeup来唤醒acceptor
        this.shutdown = true;
        wakeUp();

    }

    @Override
    public Set<SocketAddress> getBoundAddresses() {
        return new HashSet<>(boundmap.keySet());
    }

    //这里是私有方法，所以异常不会传递到上一层，所以处理起来可以比较灵活
    //这里将所有的异常集中在run()中报告
    private void accept() throws IOException {
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            it.remove();
            acceptByProtocol(key);
        }
    }


    private void wait0() throws IOException {
        while (!endFlag) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
        }

        //reset endflag
        endFlag = false;

        if (exception != null) {
            IOException e = exception;
            exception = null;
            throw e;
        }
    }

    private void unbind0() {

        synchronized (lock) {
            if(!unbindAddresses.isEmpty()) {
                for (SocketAddress address : unbindAddresses) {
                    try{
                        if (boundmap.containsKey(address)) {
                            SelectableChannel ssc = boundmap.get(address);
                            close(ssc);
                            boundmap.remove(address);
                            LOG.info("[Simple-NIO] unbind address = " + address);
                        }

                    } catch (IOException e) {
                        exception = e;
                    }
                }

                unbindAddresses.clear();

                endFlag = true;
                lock.notifyAll();
            }

        }
    }

    //Acceptor进行bind操作的代码
    private void bind0() {

        synchronized (lock) {
            if (!bindAddresses.isEmpty()) {
                for (SocketAddress address : bindAddresses) {
                    boolean success = false;
                    try {
                        bindByProtocol(address);
                        success = true;
                        LOG.info("[Simple-NIO] bind successs " + address);
                    } catch (IOException e) {

                        //当绑定已经绑定过的port时，会抛出异常，但这里不能直接再向上抛出，因为
                        //acceptor执行在一个线程当中，而bind的调用者在另一个线程当中，所以无法传递给bind的调用者
                        //这时可利用一个exception，成员，通过调用者有异常
                        exception = e;
                    } finally {
                        if (!success) {
                            rollback();
                            break;
                        }
                    }
                }

                bindAddresses.clear();

                endFlag = true;
                lock.notifyAll();
            }
        }
    }

    private void rollback() {

        Set<SocketAddress> boundAddress = boundmap.keySet();

        for (SocketAddress address : boundAddress) {
            try {
                close(boundmap.get(address));
            } catch (IOException e) {
                LOG.info("[Simple-NIO] Rollback bind operation exception", e);
            }
            boundmap.remove(address);
        }
    }

    private void close(SelectableChannel ssc) throws IOException {

        SelectionKey key = ssc.keyFor(selector);
        key.cancel();
        ssc.close();
    }

    private void shutdown0() throws IOException {

        bindAddresses.clear();
        unbindAddresses.clear();

        for (SelectableChannel sc : boundmap.values()) {
            close(sc);
        }

        //关闭时要检查资源，因为构造过程中可能构造了一半就失败了，这里要关闭那些已打开的资源
        if (selector != null) {
            selector.close();
        }
        pool.shutdown();
        super.shutdown();
        LOG.info("[Simple-NIO] server acceptor shutdown success");
    }

    private void wakeUp() {
        selector.wakeup();
    }

    //抽象方法
    protected abstract void acceptByProtocol(SelectionKey key) throws IOException;
    protected abstract void bindByProtocol(SocketAddress address) throws IOException;
}
