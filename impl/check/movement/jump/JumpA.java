package dev.phoenixhaven.customac.impl.check.movement.jump;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;
import dev.phoenixhaven.customac.utils.player.GroundUtils;
import dev.phoenixhaven.customac.utils.player.PlayerUtils;

@CheckInfo(name = "Jump", type = "A", checkType = CheckType.MOVEMENT)
public class JumpA extends Check {
    private double lastGroundY;
    private double groundY;
    private int threshold;
    private int threshold2;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement() || packetEvent.isRotation()) {
            if (getUser().getMovementProcessor().isGround()) {
                groundY = getUser().getMovementProcessor().getFrom().getY();
            }
            threshold2 = Math.max(0, threshold2);
            threshold = Math.max(0, threshold);
            boolean elytraEquipped = PlayerUtils.isElytraEquipped(getUser()) && GroundUtils.isNearGround(getUser());
            if (getUser().getMovementProcessor().getAirTicks() == 1) {
                double height = getUser().getCollisionProcessor().getSoulSandTicks() > 0 && getUser().getCollisionProcessor().getBlockAboveTicks() > 0 ?
                        0.3250000476837158 : 0.42f + (getUser().getPotionEffectProcessor().getJumpPotionMultiplier() * 0.1);
                if (getUser().getMovementProcessor().getDeltaY() > 0) {
                    if (!MathUtils.isInBoundsEqual(getUser().getMovementProcessor().getDeltaY(), height - (getUser().getPotionEffectProcessor().hasJumpPotion ? 1E-7 : 1E-8), height + (getUser().getPotionEffectProcessor().hasJumpPotion ? 1E-7 : 1E-8)) &&
                            !shouldExemptFirstTick(getUser(), lastGroundY, groundY)) {
                        if (increaseBuffer(1) >= 2) {
                            fail("Invalid jump motion", "DeltaY=" + getUser().getMovementProcessor().getDeltaY(), "Predicted=" + height,
                                    String.valueOf(height - getUser().getMovementProcessor().getDeltaY()),
                                    String.valueOf(getUser().getMovementProcessor().getAirTicks()));
                        }
                    } else {
                        decreaseBuffer(elytraEquipped ? 0.5 : 0.1);
                    }
                } else {
                    decreaseBuffer(0.1);
                }
            }
            if (getUser().getMovementProcessor().getDeltaY() > Math.max(getUser().getMovementProcessor().getJumpMotion(getUser()), 0.5)) {
                if (threshold++ >= 2) {
                    if (getUser().getGhostBlockProcessor().getGroundLocation() != null) {
                        getUser().runTeleportSync(getUser().getGhostBlockProcessor().getGroundLocation());
                    }
                }
            } else {
                threshold--;
            }
            if (getUser().getMovementProcessor().getLastDeltaY() >= 0 && getUser().getMovementProcessor().getAirTicks() > 1 &&
                    getUser().getMovementProcessor().getDeltaY() > 0) {
                double offset = (getUser().getMovementProcessor().getDeltaY()) - ((getUser().getMovementProcessor().getLastDeltaY() - 0.08) * 0.98f);
                if (!MathUtils.isInBoundsEqual(offset, -9E-14, 9E-14)) {
                    if (!shouldExempt(getUser(), lastGroundY, groundY)) {
                        if (threshold2++ >= (elytraEquipped ? 3 : 1)) {
                            fail("Invalid jump motion", "DeltaY=" + getUser().getMovementProcessor().getDeltaY(), "Predicted=" +
                                            (getUser().getMovementProcessor().getLastDeltaY() - 0.08) * 0.98f, String.valueOf(offset),
                                    String.valueOf(getUser().getMovementProcessor().getAirTicks()));
                        }
                    } else {
                        threshold2--;
                    }
                } else {
                    threshold2--;
                }
                if (getUser().getMovementProcessor().getAirTicks() == 2) {
                    lastGroundY = groundY;
                }
            }
        }
    }

    private boolean shouldExempt(User user, double lastGroundY, double groundY) {
        return user.getVelocityProcessor().getTicksSinceVelocity() <= 2 || ExemptUtils.canFly(user) || ExemptUtils.isNearRideableEntity(user) || user.getCollisionProcessor().isInWeb() ||
                user.getCollisionProcessor().getLiquidTicks() > 0 || user.getCollisionProcessor().isOnClimable() || user.getTeleportProcessor().getTicksSinceTeleport() <= 8 ||
                user.getCollisionProcessor().getBlockAboveTicks() > 0 || user.getCollisionProcessor().checkLiquidNear(user, 2.5) || user.getCollisionProcessor().isOnClimable() ||
                MathUtils.isInBoundsEqual(user.getMovementProcessor().getDeltaY(), 0.33319999363422337, 0.33319999363422426) &&
                        MathUtils.isInBounds(user.getMovementProcessor().getLastDeltaY(), 0.395575, 0.395576)
                        && (MathUtils.isInBoundsEqual(groundY - lastGroundY, 0.9, 1.1) && !user.getPotionEffectProcessor().hasJumpPotion) ||
                user.getCollisionProcessor().getSlimeTicks() > 0 || user.getMovementProcessor().getElytraTicks() > 0 ||
                user.getPotionEffectProcessor().getLevitationTicks() > 0;
    }

    public boolean shouldExemptFirstTick(User user, double lastGroundY, double groundY) {
        return user.getCollisionProcessor().getSlimeTicks() > 0 || user.getCollisionProcessor().isInWeb() ||
                user.getCollisionProcessor().getHalfBlockTicks() > 0 || user.getCollisionProcessor().getBlockAboveTicks() > 0 ||
                user.getCollisionProcessor().getLiquidTicks() > 0 && user.getMovementProcessor().getDeltaY() <= 0.04 ||
                MathUtils.isInBoundsEqual(user.getMovementProcessor().getDeltaY(), 0.5926045976350593, 0.592604597635062)
                && user.getMovementProcessor().isGround() || ExemptUtils.isNearRideableEntity(user) ||
                user.getTeleportProcessor().getTicksSinceTeleport() <= 5 || user.getVelocityProcessor().getTicksSinceVelocity() <= 2 ||
                user.getCollisionProcessor().getPistionTicks() > 0 || ExemptUtils.canFly(user) || user.getCollisionProcessor().isOnClimable() ||
                user.getCollisionProcessor().checkAnvilNear(user) || user.getCollisionProcessor().getSnowTicks() > 0 ||
                MathUtils.isInBounds(user.getMovementProcessor().getDeltaY(), 0.395575, 0.395576)
                        && (MathUtils.isInBoundsEqual(groundY - lastGroundY, 0.9, 1.1) && !user.getPotionEffectProcessor().hasJumpPotion) ||
                user.getMovementProcessor().getDeltaY() == 0.20000004768371582 && user.getMovementProcessor().getAirTicks() == 1 &&
                        (!user.getCollisionProcessor().isBlockAbove() || user.getCollisionProcessor().isCollideHorizontal()) ||
                user.getMovementProcessor().getFlyingTicks() > 0 || user.getMovementProcessor().getElytraTicks() > 0 ||
                user.getPotionEffectProcessor().getLevitationTicks() > 0;
    }
}
