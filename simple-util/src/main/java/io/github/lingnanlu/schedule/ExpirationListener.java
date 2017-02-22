package io.github.lingnanlu.schedule;

/**
 * Created by rico on 2017/2/22.
 */
public interface ExpirationListener<E> {

    void expired(E expiredObject);
}
