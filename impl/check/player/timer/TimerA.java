package dev.phoenixhaven.customac.impl.check.player.timer;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;

@CheckInfo(name = "Timer", type = "A", checkType = CheckType.PACKET)
public class TimerA extends Check {
    private double balance;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement() || packetEvent.isRotation()) {
            balance += 0.5;
            if (balance >= 3) {
                if (getUser().getMovementProcessor().getDeltaXZ() != 0 && getUser().getMovementProcessor().getDeltaY() != 0) {
                    fail("Balance");
                }
                balance = 0;
            }
        }
        if (packetEvent.getType().equalsIgnoreCase(Packet.Server.POSITION)) {
            balance -= 0.5;
        }
    }

    public void onTransaction() {
        if (getUser().getMovementProcessor().isMoving() || getUser().getMovementProcessor().getTicks() % 20 == 0 &&
                !getUser().getMovementProcessor().isMoving()) {
            balance -= balance <= -40 ? 0 : 0.5;
        }
    }
}
