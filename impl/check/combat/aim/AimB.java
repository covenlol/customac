package dev.phoenixhaven.customac.impl.check.combat.aim;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;

@CheckInfo(name = "Aim", type = "B", checkType = CheckType.COMBAT)
public class AimB extends Check {
    private float lastV;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isRotation()) {
            float lastYaw = getUser().getMovementProcessor().getFrom().getYaw();
            float deltaYaw = getUser().getMovementProcessor().getTo().getYaw();

            float smaller = Math.min(deltaYaw, lastYaw);
            float bigger = Math.max(deltaYaw, lastYaw);

            float v = Math.abs(bigger) - Math.abs(smaller);
            if (Math.abs(v) > 100) {
                if (Math.abs(lastV) > 50 && Math.abs(v) > 50) {
                    decreaseBuffer(1);
                } else {
                    if (increaseBuffer(1) >= 10) {
                        fail("Too high Yaw difference while slowing down", String.valueOf(lastV), String.valueOf(v));
                    }
                }
            } else {
                decreaseBuffer(0.1);
            }
            lastV = v;
        }
    }
}
