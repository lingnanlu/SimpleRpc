package io.github.lingnanlu;

/**
 * Created by rico on 2017/1/13.
 */
public interface IoReactor {

    void shutdown();

    IoHandler getHandler();

}
