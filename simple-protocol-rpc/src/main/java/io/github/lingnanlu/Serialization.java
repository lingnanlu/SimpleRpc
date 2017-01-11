package io.github.lingnanlu;

/**
 * Created by rico on 2017/1/11.
 */
public interface Serialization<T> {

    byte[] serialize(T object);

    T deserialize(byte[] bytes);

    T deserialize(byte[] bytes, int offset);
}
