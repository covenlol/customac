package dev.phoenixhaven.customac.impl.check.combat.aim;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;

@CheckInfo(name = "Aim", type = "C", checkType = CheckType.COMBAT)
public class AimC extends Check {
    private int threshold;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isRotation()) {
            float lastYaw = getUser().getMovementProcessor().getFrom().getYaw();
            float lastPitch = getUser().getMovementProcessor().getFrom().getPitch();

            float yaw = getUser().getMovementProcessor().getTo().getYaw();
            float pitch = getUser().getMovementProcessor().getTo().getPitch();

            if (Math.abs(pitch) != 90) {
                if (Math.abs(lastYaw - yaw) >= 10) {
                    if (lastPitch == pitch) {
                        if (increaseBuffer(1) > 5) {
                            fail("Too low pitch change", "Yaw=" + (lastYaw - yaw), "Pitch=" + (lastPitch - pitch));
                        }
                    } else {
                        decreaseBuffer(2);
                    }
                }

                if (Math.abs(lastYaw - yaw) >= 10) {
                    if (Math.abs(lastPitch - pitch) <= 1E-5) {
                        if (threshold++ >= 4) {
                            fail("Too low pitch change", "Yaw=" + (lastYaw - yaw), "Pitch=" + (lastPitch - pitch));
                        }
                    } else {
                        threshold= 0;
                    }
                } else {
                    threshold = 0;
                }
            }
        }
    }
}
