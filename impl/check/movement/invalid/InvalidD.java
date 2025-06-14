package dev.phoenixhaven.customac.impl.check.movement.invalid;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;

@CheckInfo(name = "Invalid", type = "D", checkType = CheckType.MOVEMENT)
public class InvalidD extends Check {
    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement()) {
            if (getUser().getPotionEffectProcessor().hasLevitation) {
                double levitation = (0.05D * (getUser().getPotionEffectProcessor().levitationMultiplier) - getUser().getMovementProcessor().getDeltaY()) * 0.2D;
                double pred = levitation * 0.98f;

                if (getUser().getMovementProcessor().getDeltaY() < pred && !shouldExempt(getUser())) {
                    if (increaseBuffer(1) >= 2) {
                        fail("Ignoring levitation", String.valueOf(getUser().getMovementProcessor().getDeltaY()), String.valueOf(levitation));
                    }
                } else {
                    decreaseBuffer(0.25);
                }
            }
        }
    }

    public boolean shouldExempt(User user) {
        return user.getPlayer().isFlying() || user.getCollisionProcessor().isBlockAbove() || user.getCollisionProcessor().getWebTicks() > 0 ||
                user.getCollisionProcessor().getLiquidTicks() > 0 || ExemptUtils.isNearRideableEntity(user, 1);
    }
}
