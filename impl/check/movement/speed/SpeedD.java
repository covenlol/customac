package dev.phoenixhaven.customac.impl.check.movement.speed;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;

@CheckInfo(name = "Speed", type = "D", checkType = CheckType.MOVEMENT)
public class SpeedD extends Check {
    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement()) {
            double deltaXZ = Math.abs(getUser().getMovementProcessor().getDeltaXZ());
            double lastDeltaXZ = Math.abs(getUser().getMovementProcessor().getLastDeltaXZ());

            double deltaYaw = getUser().getMovementProcessor().getDeltaYaw();

            final double accel = Math.abs(deltaXZ - lastDeltaXZ);

            if (deltaYaw > 0 && deltaXZ > 0.1) {
                if (accel * 100 <= 1E-7 && !shouldExempt(getUser())) {
                    fail("Invalid Acceleration", "Acceleration=" + accel, String.valueOf(getUser().getTeleportProcessor().getTicksSinceTeleport()));
                }
            }
        }
    }

    public boolean shouldExempt(User user) {
        return user.getCollisionProcessor().isOnClimable() || user.getCollisionProcessor().isInsideBlock(user) ||
                user.getTeleportProcessor().getTicksSinceTeleport() == 1 || user.getMovementProcessor().getElytraTicks() > 0;
    }
}
