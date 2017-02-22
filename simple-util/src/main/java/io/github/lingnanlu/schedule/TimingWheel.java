package io.github.lingnanlu.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by rico on 2017/2/22.
 * <p>
 * 这是一个固定时间定时器
 */
public class TimingWheel<E> {


    private final long tickDuration;
    private final int ticksPerWheel;
    private final ArrayList<Slot<E>> wheel;
    private int currentTickIndex = 0;
    private final ArrayList<ExpirationListener<E>> expirationListeners;
    private Thread workerThread;
    private int previousTickIndex;


    public TimingWheel(long tickDuration, int ticksPerWheel, TimeUnit timeUnit) {

        this.wheel = new ArrayList<>();
        this.tickDuration = TimeUnit.MILLISECONDS.convert(tickDuration, timeUnit);

        //注意这里的加一，这是为了修正放入previous slot的差一误差, 实际上是7个slot
        this.ticksPerWheel = ticksPerWheel + 1;

        expirationListeners = new ArrayList<>();
        for (int i = 0; i < this.ticksPerWheel; i++) {
            wheel.add(new Slot<E>());
        }

        workerThread = new Thread(new TickWorker(), "Timing-Wheel");
    }

    public void start() {
        if (!workerThread.isAlive()) {
            workerThread.start();
        }
    }

    public void addExpirationListener(ExpirationListener<E> listener) {
        expirationListeners.add(listener);
    }

    public void add(E e) {

        //必须放到前一个slot中
        //假如放在当前，可以正在处理当前的slot,即不等超时到就会被处理，放下一个同样道理
        //既然放在前一个，就要想办法满足规定的超时处理
        int previousTickIndex = getPreviousTickIndex();
        Slot<E> slot = wheel.get(previousTickIndex);
        slot.add(e);
    }

    public int getPreviousTickIndex() {

        if (currentTickIndex == 0) {
            return ticksPerWheel - 1;
        }

        return currentTickIndex - 1;
    }

    private class TickWorker implements Runnable {

        private long startTime;
        private long nextTick;

        @Override
        public void run() {

            startTime = System.currentTimeMillis();
            System.out.println("startTime " + startTime);
            while (true) {

                notifyExpired(currentTickIndex);

                currentTickIndex++;
                if (currentTickIndex == wheel.size()) {
                    currentTickIndex = 0;
                }
                waitForNextTick();
            }
        }

        private void waitForNextTick() {

            long currentTime = System.currentTimeMillis();

            //到达下一个Tick要休眠的时间，如果不清楚，画一个时间轴图即可
            long sleepTime = tickDuration * nextTick - (currentTime - startTime);

            //这说明notifyExpired执行时间过长，已经超过下一个tick的时间，此时在此之前的slot中的都应该做超时处理
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);

                    System.out.println("Tick " + System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            nextTick++;
        }
    }

    private void notifyExpired(int currentTickIndex) {
        Slot<E> slot = wheel.get(currentTickIndex);
        List<E> elements = slot.elements();
        for (E e : elements) {
            for (ExpirationListener<E> listener : expirationListeners) {
                listener.expired(e);
            }
        }
    }

    private static class Slot<E> {

        List<E> elements = new ArrayList<>();
        public void add(E e) {
            elements.add(e);
        }

        public List<E> elements() {
            return elements;
        }
    }

    public static void main(String[] args) {
        TimingWheel<String> timingWheel = new TimingWheel<String>(1, 4, TimeUnit.SECONDS);
        timingWheel.add("Hello World");

        timingWheel.addExpirationListener(new ExpirationListener<String>() {
            @Override
            public void expired(String expiredObject) {
                System.out.println(expiredObject);
            }
        });

        timingWheel.start();
    }
}
