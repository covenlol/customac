package dev.phoenixhaven.customac.impl.check.movement.jesus;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.block.BlockUtil;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;
import dev.phoenixhaven.customac.utils.player.GroundUtils;

@CheckInfo(name = "Jesus", type = "A", checkType = CheckType.MOVEMENT)
public class JesusA extends Check {

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement() || packetEvent.isRotation()) {
            if (getUser().getCollisionProcessor().isInLiquid()) {
                final double limit = 0.33f;
                if (getUser().getMovementProcessor().getDeltaY() > limit && !shouldExempt(getUser())) {
                    fail("Too high MotionY in water", "Limit=" + limit, "MotionY=" + getUser().getMovementProcessor().getDeltaY());
                    if (getUser().getGhostBlockProcessor().getGroundLocation() != null) {
                        getUser().runTeleportSync(getUser().getGhostBlockProcessor().getGroundLocation());
                    }
                }
                if (!getUser().getCollisionProcessor().isServerGround()) {
                    if (MathUtils.isInBounds(getUser().getMovementProcessor().getDeltaY(), -1E-10, 1E-10) && !shouldExempt2(getUser())) {
                        if (increaseBuffer(1) >= 4) {
                            fail("Invalid MotionY in water", "MotionY=" + getUser().getMovementProcessor().getDeltaY());
                        }
                    } else {
                        decreaseBuffer(1);
                    }
                }
            }
        }
    }

    private boolean shouldExempt(User user) {
        return ExemptUtils.canFly(user) || !BlockUtil.getBlock(user.getPlayer().getLocation().subtract(0, 0.2, 0)).isLiquid() ||
                user.getCollisionProcessor().getSlimeTicks() > 0 || GroundUtils.isNearGround(user) ||
                user.getTeleportProcessor().getTicksSinceTeleport() <= 4 || user.getCollisionProcessor().getLiquidTicks() <= 15 &&
                !(getUser().getMovementProcessor().getDeltaY() > getUser().getMovementProcessor().getJumpMotion(user)) ||
                GroundUtils.getSolidBlockUnder(getUser()).getBlock().getType().name().contains("_SAND");
    }

    private boolean shouldExempt2(User user) {
        return ExemptUtils.canFly(user) || user.getCollisionProcessor().getBlockAboveTicks() > 0 || user.getCollisionProcessor().isInWeb() ||
                user.getCollisionProcessor().getCarpetTicks() > 0 || user.getCollisionProcessor().getLillyPadTicks() > 0 || user.getTeleportProcessor().getTicksSinceTeleport() <= 4 ||
                user.getPlayer().isDead() || user.getCollisionProcessor().isOnClimable() || user.getPlayer().isSwimming();
    }
}
