package dev.phoenixhaven.customac.impl.check.movement.speed;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;
import dev.phoenixhaven.customac.utils.player.GroundUtils;
import dev.phoenixhaven.customac.utils.player.PlayerUtils;

@CheckInfo(name = "Speed", type = "A", checkType = CheckType.MOVEMENT)
public class SpeedA extends Check {

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement()) {
            if (!getUser().getMovementProcessor().isGround()) {
                if (getUser().getMovementProcessor().getAirTicks() > 0) {
                    boolean elytraEquipped = PlayerUtils.isElytraEquipped(getUser()) && GroundUtils.isNearGround(getUser());
                    double angle = Math.abs(MathUtils.getAngleRotation(getUser().getMovementProcessor().getTo().clone(),
                            getUser().getMovementProcessor().getFrom().clone()));
                    boolean strafing = !MathUtils.isInBoundsEqual(MathUtils.angleDivisor(angle, 45), -5, 5);

                    double pred = getUser().getMovementProcessor().getAirTicks() == 1 ? ((getUser().getMovementProcessor().getLastDeltaXZ() - (0.66f *
                            (getUser().getMovementProcessor().getLastDeltaXZ() - getUser().getMovementProcessor().getGroundSpeed(getUser())))) * 2.11) :
                            (getUser().getMovementProcessor().getLastDeltaXZ() * 0.91 + (strafing ? 0.02605f : 0.025485f));
                    if (getUser().getMovementProcessor().getDeltaXZ() > pred + 0.003 && getUser().getMovementProcessor().getAirTicks() > 0) {
                        if (increaseBuffer(1) >= 4) {
                            if (!shouldExempt(getUser())) {
                                fail("friction", "predicted= " + pred, "motionXZ= " + getUser().getMovementProcessor().getDeltaXZ(),
                                        String.valueOf(strafing), String.valueOf(getUser().getMovementProcessor().getAirTicks()),
                                        String.valueOf(getUser().getMovementProcessor().getGroundSpeed(getUser())));
                            } else if (elytraEquipped) {
                                decreaseBuffer(0.1);
                            }
                        }
                    } else {
                        decreaseBuffer(elytraEquipped ? 0.5 : 0.25);
                    }
                }
            }
        }
    }

    private boolean shouldExempt(User user) {
        return ExemptUtils.canFly(user) || user.getVelocityProcessor().getTicksSinceVelocity() <= 2 ||
                user.getCollisionProcessor().getLiquidTicks() > 0 || user.getMovementProcessor().getDeltaXZ() <= 0.1 ||
                user.getTeleportProcessor().getTicksSinceTeleport() <= 10 || user.getCollisionProcessor().isInWeb() ||
                ExemptUtils.isNearRideableEntity(user) || user.getCollisionProcessor().checkAnvilNear(user) || user.getCollisionProcessor().isOnClimable() ||
                user.getCollisionProcessor().checkSlimeNear(user) && user.getCollisionProcessor().getIceTicks() > 0 ||
                user.getMovementProcessor().getElytraTicks() > 0 || getUser().getMovementProcessor().getDeltaXZ() <= 0.05;
    }
}
