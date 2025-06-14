package dev.phoenixhaven.customac.utils.math;


import dev.phoenixhaven.customac.base.user.User;
import lombok.Getter;

@Getter
public class EventTimer {
    private int tick;
    private final int max;
    private final User user;

    public EventTimer(int max, User user) {
        this.tick = 0;
        this.max = max;
        this.user = user;
    }

    public boolean hasNotPassed() {
        return ((this.user.getMovementProcessor().getTicks() - tick) <=
                (user.getTransactionProcessor().getPingTicks() + this.max));
    }

    public boolean passed() {
        return ((this.user.getMovementProcessor().getTicks() - tick) >=
                (this.max + user.getTransactionProcessor().getPingTicks()));
    }

    public void reset() {
        this.tick = this.user.getMovementProcessor().getTicks();
    }
}