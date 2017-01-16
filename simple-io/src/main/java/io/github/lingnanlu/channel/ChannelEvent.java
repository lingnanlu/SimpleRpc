package io.github.lingnanlu.channel;

/**
 * Created by rico on 2017/1/13.
 */
public interface ChannelEvent<D> {

    Channel<D> getChannel();

    ChannelEventType getType();

    void fire();
}
