package dev.phoenixhaven.customac.utils.java;

public class Timer {
    private long lastTime;

    public void Timer() {
        reset();
    }

    public double getMS() {
        return System.currentTimeMillis() - lastTime;
    }

    public void reset() {
        this.lastTime = System.currentTimeMillis();
    }

    public boolean hasReached(double milliseconds) {
        return ((System.currentTimeMillis() - lastTime) >= milliseconds);
    }

    public long getLastTime() {
        return lastTime;
    }
}
