package io.github.lingnanlu;

import io.craft.atom.util.schedule.ExpirationListener;
import io.craft.atom.util.schedule.TimingWheel;
import io.github.lingnanlu.channel.ChannelEventType;
import io.github.lingnanlu.spi.NioChannelEventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by rico on 2017/2/22.
 */
public class NioChannelIdleTimer  {

    private static final Logger LOG = LoggerFactory.getLogger(NioChannelIdleTimer.class);


    private TimingWheel<NioByteChannel> timingWheel;
    private NioChannelEventDispatcher dispatcher;
    private IoHandler handler;
    private int timeoutInMillis;


    public NioChannelIdleTimer(NioChannelEventDispatcher dispatcher, IoHandler handler, int timeoutInMillis) {
        this.dispatcher = dispatcher;
        this.handler = handler;
        this.timeoutInMillis = timeoutInMillis;


        if (timeoutInMillis > 0) {
            int tickDuration = (timeoutInMillis / 100 == 0 ? 10 : timeoutInMillis / 100);
            this.timingWheel = new TimingWheel<>(tickDuration, 100, TimeUnit.MILLISECONDS);
            this.timingWheel.addExpirationListener(new NioChannelIdleListener());
            this.timingWheel.start();
        }
    }


    void add(NioByteChannel channel) {
        timingWheel.add(channel);
    }

    void remove(NioByteChannel channel) {
        timingWheel.remove(channel);
    }

    Set<NioByteChannel> aliveChannels() {
        return timingWheel.elements();
    }

    private void fireChannelIdle(NioByteChannel channel) {
        dispatcher.dispatch(new NioByteChannelEvent(ChannelEventType.CHANNEL_IDLE, channel, handler));
    }

    private class NioChannelIdleListener implements ExpirationListener<NioByteChannel> {
        @Override
        public void expired(NioByteChannel channel) {
            long now = System.currentTimeMillis();
            long elapse = now - channel.getLastIoTime();

            if (elapse > timeoutInMillis) {
                fireChannelIdle(channel);
            }

            timingWheel.add(channel);

            LOG.info("[Simple-NIO] Nio active channel count is " + timingWheel.size());
        }


    }


}
