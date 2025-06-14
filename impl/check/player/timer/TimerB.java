package dev.phoenixhaven.customac.impl.check.player.timer;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.java.Timer;

@CheckInfo(name = "Timer", type = "B", checkType = CheckType.MOVEMENT)
public class TimerB extends Check {
    private int limit = 20;
    private int packets;
    private final Timer timer = new Timer();

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement() || packetEvent.isRotation()) {
            if (timer.hasReached(1000)) {
                limit += Math.ceil(getUser().getTransactionProcessor().getPingTicks());
                if (!shouldExempt(getUser())) {
                    if (packets > limit) {
                        if (increaseBuffer(1) >= 5) {
                            fail("Too many packets in 1 second", String.valueOf(packets), String.valueOf(limit),
                                    String.valueOf(Math.ceil((timer.getMS() - 1000) / 50)));
                        }
                    } else {
                        decreaseBuffer(0.5);
                    }
                }
                packets = 0;
                limit = 20;
                timer.reset();
            }
            this.packets++;
        }
        if (packetEvent.getType().equalsIgnoreCase(Packet.Server.POSITION)) {
            limit++;
        }
    }

    private boolean shouldExempt(User user) {
        return !user.getMovementProcessor().isMoving();
    }
}
