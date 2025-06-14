package dev.phoenixhaven.customac.impl.check.movement.elytraflight;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import dev.phoenixhaven.customac.utils.player.GroundUtils;

@CheckInfo(name = "Elytra Flight", type = "A", checkType = CheckType.MOVEMENT)
public class ElytraFlightA extends Check {
    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement() || packetEvent.isRotation()) {
            if (getUser().getPlayer().isGliding()) {
                if (MathUtils.isInBoundsEqual(getUser().getMovementProcessor().getDeltaY(), -0.01, 0.01) &&
                !MathUtils.isInBoundsEqual(getUser().getMovementProcessor().getTo().getPitch(), -1, 1) &&
                        !shouldExempt(getUser())) {
                    if (increaseBuffer(1) >= 3) {
                        fail("Too little DeltaY change for pitch", String.valueOf(getUser().getMovementProcessor().getDeltaY()),
                                String.valueOf(getUser().getMovementProcessor().getTo().getPitch()));
                    }
                } else {
                    decreaseBuffer(0.75);
                }
            }
        }
    }

    public boolean shouldExempt(User user) {
        return GroundUtils.isNearGround(user) && !user.getCollisionProcessor().isBlockAbove() || user.getPlayer().isSwimming() ||
                user.getMovementProcessor().getElytraTicks() < 5 && user.getPlayer().isGliding();
    }
}
