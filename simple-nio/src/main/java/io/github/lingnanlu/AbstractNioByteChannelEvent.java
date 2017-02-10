package io.github.lingnanlu;

import io.github.lingnanlu.channel.AbstractChannelEvent;
import io.github.lingnanlu.channel.Channel;
import io.github.lingnanlu.channel.ChannelEvent;
import io.github.lingnanlu.channel.ChannelEventType;

/**
 * Created by rico on 2017/1/16.
 */
abstract public class AbstractNioByteChannelEvent extends AbstractChannelEvent implements ChannelEvent<byte[]> {

    protected final NioByteChannel channel;

    AbstractNioByteChannelEvent(ChannelEventType type, NioByteChannel channel) {
        super(type);
        this.channel = channel;
    }

    @Override
    public Channel<byte[]> getChannel() {
        return channel;
    }


}
