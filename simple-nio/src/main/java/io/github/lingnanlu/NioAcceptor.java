package io.github.lingnanlu;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

/**
 * Created by rico on 2017/1/13.
 */
public abstract class NioAcceptor extends NioReactor implements IoAcceptor {

    protected final Set<SocketAddress> bindAddresses   = new HashSet<SocketAddress>();
    protected Selector selector;
    protected boolean selectable = false;

    /* TODO
    这两个成员用来对绑定端口操作进行同步，源代码中， 当AcceptThread正在进行绑定操作时， 当前线程似乎可以进行
    bind操作，这应该是有问题的， 暂时先按照源代码中的做。
     */
    protected final Object lock = new Object();
    protected boolean endFlag = false;

    public void bind(int port) {

    }

    public void bind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddress) throws IOException {
        if(!selectable){
            init();
        }

        List<SocketAddress> localAddresses = new ArrayList<SocketAddress>(2);
        localAddresses.add(firstLocalAddress);

        if (otherLocalAddress != null) {
            for (SocketAddress address : otherLocalAddress) {
                localAddresses.add(address);
            }
        }

        bindAddresses.addAll(localAddresses);

        if (!bindAddresses.isEmpty()) {
            synchronized (lock) {
                //唤醒selector， 让selector执行绑定操作
                selector.wakeup();


                //等待绑定完成
                wait0();
            }
        }
    }

    private void wait0() {

        while (!this.endFlag) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.endFlag = false;
    }

    private void init() throws IOException {
        selector = Selector.open();
        selectable = true;
        new AcceptThread().start();
    }

    public void unbind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddress) {

    }

    public void unbind(int port) {

    }

    public Set<SocketAddress> getBoundAddresses() {
        return null;
    }


    //acceptor单独运行在一个线程当中
    //该线程负责接收新到的连接以及绑定新的端口
    private class AcceptThread extends Thread {

        @Override
        public void run() {
            while (selectable) {

                try {

                    int selected = selector.select();

                    //如果有客户端接入， 就accept
                    if (selected > 0) {
                        accept();
                    }

                    //说明在运行过程中,可以绑定新的端口
                    bind0();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void bind0() {
        if (!bindAddresses.isEmpty()) {
            for (SocketAddress address : bindAddresses) {
                //这里演示了一种回滚的方法bindAddresses可以看出所有的操作
                boolean sucess = false;
                try {
                    bindByProtocol(address);
                    sucess = true;
                } finally {
                    if (!sucess) {
                        rollback();
                        break;
                    }
                }

                //不管有没有成功绑定所有address，这里都清空
                bindAddresses.clear();

                synchronized (lock) {
                    endFlag = true;
                    lock.notifyAll();
                }


            }
        }
    }

    // rollback already bound address
    protected void rollback() {

    }

    protected abstract void bindByProtocol(SocketAddress address);

    private void accept() {
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            it.remove();
            acceptByProtocol(key);
        }
    }

    protected abstract NioByteChannel acceptByProtocol(SelectionKey key);
}
