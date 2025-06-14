package dev.phoenixhaven.customac.impl.check.combat.aim;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;

@CheckInfo(name = "Aim", type = "A", checkType = CheckType.COMBAT)
public class AimA extends Check {
    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isRotation()) {
            float pitch = getUser().getMovementProcessor().getTo().getPitch();
            float yaw = getUser().getMovementProcessor().getTo().getYaw();

            if (Math.round(pitch) == pitch || Math.round(yaw) == yaw) {
                if (Math.abs(pitch) != 90 && pitch != 0) {
                    if (increaseBuffer(1) >= 2) {
                        fail("Rounded rotations");
                    }
                }
            } else {
                decreaseBuffer(0.05);
            }
        }
    }
}
