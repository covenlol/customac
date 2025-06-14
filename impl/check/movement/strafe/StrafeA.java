package dev.phoenixhaven.customac.impl.check.movement.strafe;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;

@CheckInfo(name = "Strafe", type = "A", checkType = CheckType.MOVEMENT)
public class StrafeA extends Check {
    private double lastAngle;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement()) {
            double angle = Math.abs(MathUtils.getAngleRotation(getUser().getMovementProcessor().getTo().clone(),
                    getUser().getMovementProcessor().getFrom().clone()));

            if (angle == lastAngle && getUser().getMovementProcessor().getDeltaXZ() > getUser().getMovementProcessor().getGroundSpeed(getUser()) &&
                    !MathUtils.isInBoundsEqual(MathUtils.angleDivisor(angle), -5, 5) && (angle < 100 || angle > 110)) {
                if (!shouldExempt(getUser())) {
                    if (increaseBuffer(1) >= 2) {
                        fail("Strafe...", String.valueOf(angle));
                    }
                }
            } else {
                decreaseBuffer(0.1);
            }
            lastAngle = angle;
        }
    }

    private boolean shouldExempt(User user) {
        return user.getTeleportProcessor().getTicksSinceTeleport() <= 20 || user.getCollisionProcessor().isInWeb() ||
                user.getMovementProcessor().getGroundTicks() > 20 || user.getMovementProcessor().getAirTicks() > 30 ||
                ExemptUtils.canFly(getUser()) || user.getCollisionProcessor().isCollideHorizontal() ||
                user.getVelocityProcessor().getTicksSinceVelocity() <= 10 || user.getMovementProcessor().getElytraTicks() > 0;
    }
}
