package io.github.lingnanlu;

/**
 * Created by rico on 2017/1/13.
 */
public interface IoReactor {

    //释放该Reactor分配的所有资源
    void shutdown();


    IoHandler getHandler();

}
