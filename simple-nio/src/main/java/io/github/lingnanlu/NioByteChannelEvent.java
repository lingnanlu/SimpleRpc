package io.github.lingnanlu;

import io.github.lingnanlu.channel.ChannelEventType;

/**
 * Created by rico on 2017/1/18.
 */
public class NioByteChannelEvent extends AbstractNioByteChannelEvent {

    private final Object parameter;
    private final IoHandler handler;

    NioByteChannelEvent(ChannelEventType type, NioByteChannel channel, IoHandler handler, Object parameter) {
        super(type, channel);
        this.handler = handler;
        this.parameter = parameter;
    }

    public NioByteChannelEvent(ChannelEventType type, NioByteChannel channel, IoHandler handler) {
        this(type, channel, handler, null);
    }


    @Override
    public void fire() {
        switch (type) {
            case CHANNEL_READ:
                handler.channelRead(channel,(byte[]) parameter);
                break;
            case CHANNEL_OPENED:
                handler.channelOpened(channel);
        }
    }
}
