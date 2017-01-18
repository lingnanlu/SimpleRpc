package io.github.lingnanlu;

import io.github.lingnanlu.channel.ChannelEvent;

/**
 * Created by rico on 2017/1/16.
 *
 * 直接在IO process 线程中分发事件
 */
public class NioOrderedDirectChannelEventDispatcher extends AbstractNioChannelEventDispatcher {


    public NioOrderedDirectChannelEventDispatcher() {
        super();
    }

    public NioOrderedDirectChannelEventDispatcher(int totalEventSize) {
        super(totalEventSize);
    }
    @Override
    public void dispatch(ChannelEvent<byte[]> event) {
        event.fire();
    }
}
