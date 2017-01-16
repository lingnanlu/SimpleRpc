package io.github.lingnanlu.channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rico on 2017/1/13.
 */
abstract public class AbstractChannel {

    protected Map<Object, Object> attributes = new ConcurrentHashMap<>();

    protected ChannelState state = ChannelState.OPEN;

    public boolean isOpen() {
        return state == ChannelState.OPEN;
    }

    public boolean isClosing() {
        return state == ChannelState.CLOSING;
    }

    public boolean isClosed() {
        return state == ChannelState.CLOSED;
    }

    public boolean isPaused() {
        return state == ChannelState.PAUSED;
    }

    public void pause() {
        state = ChannelState.PAUSED;
    }

    public void resume() {
        state = ChannelState.OPEN;
    }

    public void close() {
        state = ChannelState.CLOSED;
    }

    public Object getAttribute(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }

        return attributes.get(key);
    }

    public Object setAttribute(Object key, Object value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("key & value can not be null");
        }

        return attributes.put(key, value);
    }

    public boolean containsAttribute(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }

        return attributes.containsKey(key);
    }

    public Object removeAttribute(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }

        return attributes.remove(key);
    }

}
