package io.github.lingnanlu.channel;

import lombok.Getter;

/**
 * Created by rico on 2017/1/16.
 */
public class AbstractChannelEvent {

    @Getter protected final ChannelEventType type;

    public AbstractChannelEvent(ChannelEventType type) {
        this.type = type;
    }
}
