package io.github.lingnanlu.channel;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by rico on 2017/1/13.
 *
 * todo 为什么这里不直接实现Channel
 */
@ToString(of = {"id", "state"})
abstract public class AbstractChannel {

    @Getter protected long id;

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    protected Map<Object, Object> attributes = new ConcurrentHashMap<>();

    protected ChannelState state = ChannelState.OPEN;

    public AbstractChannel() {
        id = ID_GENERATOR.incrementAndGet();
    }
    public AbstractChannel(long id) {
        this.id = id;
    }

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

    public Object getAttribute(Object key) {return attributes.get(key);}
    public Object setAttribute(Object key, Object value) {return attributes.put(key, value);}
    public boolean containsAttribute(Object key) {return attributes.containsKey(key);}
    public Object removeAttribute(Object key) {return attributes.remove(key);}

}
