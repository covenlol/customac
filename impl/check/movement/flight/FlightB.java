package dev.phoenixhaven.customac.impl.check.movement.flight;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;

@CheckInfo(name = "Flight", type = "B", checkType = CheckType.MOVEMENT)
public class FlightB extends Check {
    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement() || packetEvent.isRotation()) {
            if (!shouldExempt(getUser())) {
                if (getUser().getMovementProcessor().getDeltaY() <= -0.2 && getUser().getMovementProcessor().isLastGround()) {
                    fail("VClip", String.valueOf(getUser().getMovementProcessor().getDeltaY()));
                }
            }
        }
    }

    private boolean shouldExempt(User user) {
        return user.getVelocityProcessor().getTicksSinceVelocity() <= 10 || user.getTeleportProcessor().getTicksSinceTeleport() <= 10
                || ExemptUtils.isNearRideableEntity(user);
    }
}