package dev.phoenixhaven.customac.impl.check.combat.aim;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;

@CheckInfo(name = "Aim", type = "D", checkType = CheckType.COMBAT)
public class AimD extends Check {
    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isRotation()) {
            if (getUser().getMovementProcessor().getLastDeltaYaw() >= 500 && getUser().getMovementProcessor().getDeltaYaw() <= 3) {
                if (!shouldExempt(getUser())) {
                    fail("Invalid rotation change", String.valueOf(getUser().getMovementProcessor().getDeltaYaw()));
                }
            }
        }
    }

    public boolean shouldExempt(User user) {
        return user.getTeleportProcessor().getTicksSinceTeleport() <= 5;
    }
}
