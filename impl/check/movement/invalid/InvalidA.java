package dev.phoenixhaven.customac.impl.check.movement.invalid;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;

@CheckInfo(name = "Invalid", type = "A", checkType = CheckType.MOVEMENT)
public class InvalidA extends Check {
    private boolean exempt;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement() || packetEvent.isRotation()) {
            if (!shouldExempt(getUser())) {
                if (getUser().getMovementProcessor().isGround() && getUser().getCollisionProcessor().checkSlimeNear(getUser())) {
                    exempt = true;
                } else if (getUser().getMovementProcessor().isGround() && !getUser().getCollisionProcessor().checkSlimeNear(getUser())) {
                    exempt = false;
                }
                if ((MathUtils.isInBounds(getUser().getMovementProcessor().getDeltaY(), -1E-3, 1E-3) && getUser().getMovementProcessor().getDeltaY() != 0) ||
                        getUser().getMovementProcessor().getAirTicks() == 1 && getUser().getMovementProcessor().getDeltaY() < 0.3249f &&
                                getUser().getMovementProcessor().getDeltaY() > 0 && !(getUser().getCollisionProcessor().getSlimeTicks() > 0) &&
                                !(getUser().getCollisionProcessor().getBlockAboveTicks() > 0) || getUser().getMovementProcessor().getAirTicks() == 1
                        && getUser().getCollisionProcessor().getBlockAboveTicks() > 0 && getUser().getMovementProcessor().getDeltaY() <= 0.20000004768) {
                    if (!getUser().getPlayer().isSwimming()) {
                        if (increaseBuffer(1) >= 2) {
                            fail("Invalid motionY", String.valueOf(getUser().getMovementProcessor().getDeltaY()));
                        }
                    }
                } else {
                    decreaseBuffer(0.25);
                }
                if (getUser().getMovementProcessor().getDeltaY() > Math.max(getUser().getMovementProcessor().getJumpMotion(getUser()), 0.6f)
                        && !shouldExempt2(getUser())) {
                    fail("Invalid motionY", String.valueOf(getUser().getMovementProcessor().getDeltaY()));
                }

                if (getUser().getMovementProcessor().getDeltaY() > 0 && getUser().getMovementProcessor().getAirTicks() > 15 &&
                        !shouldExempt3(getUser())) {
                    fail("Going upwards for too long", String.valueOf(getUser().getMovementProcessor().getDeltaY()));
                }
            }
        }
    }

    private boolean shouldExempt(User user) {
        return user.getCollisionProcessor().getWebTicks() > 0 || user.getCollisionProcessor().checkAnvilNear(user) || user.getMovementProcessor().isGround() &&
                MathUtils.isInBoundsEqual(user.getMovementProcessor().getLastDeltaY(), 0.5926045976350593, 0.592604597635062) ||
                user.getCollisionProcessor().isInsideBlock(getUser()) || ExemptUtils.canFly(user) || user.getPotionEffectProcessor().getLevitationTicks() > 0 ||
                user.getPotionEffectProcessor().getSlowFallingTicks() > 0 || user.getMovementProcessor().isElytra() || user.getMovementProcessor().getRiptideTicks() > 0;
    }

    private boolean shouldExempt2(User user) {
        return user.getCollisionProcessor().getPistionTicks() > 0 || user.getTeleportProcessor().getTicksSinceTeleport() <= 5
                || user.getCollisionProcessor().getHalfBlockTicks() > 0 || exempt || user.getCollisionProcessor().getSnowTicks() > 0 ||
                ExemptUtils.canFly(user) || user.getVelocityProcessor().getTicksSinceVelocity() <= 20 &&
                user.getMovementProcessor().getDeltaY() <= user.getVelocityProcessor().getVelocityY() || user.getMovementProcessor().isElytra();
    }

    private boolean shouldExempt3(User user) {
        return ExemptUtils.canFly(user) || user.getCollisionProcessor().getLiquidTicks() > 0 || getUser().getPotionEffectProcessor().getJumpPotionMultiplier() > 3 ||
                ExemptUtils.isNearRideableEntity(user) || user.getPotionEffectProcessor().getLevitationTicks() > 0 || user.getCollisionProcessor().isOnClimable();
    }
}
