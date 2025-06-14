package dev.phoenixhaven.customac.impl.check.movement.flight;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.block.BlockUtil;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;
import dev.phoenixhaven.customac.utils.player.GroundUtils;
import dev.phoenixhaven.customac.utils.player.PlayerUtils;
import org.bukkit.Material;

@CheckInfo(name = "Flight", type = "A", checkType = CheckType.MOVEMENT)
public class FlightA extends Check {
    private double lastGroundY;
    private double groundY;

    @Override
    public void processPacket(PacketEvent event) {
        if (event.isMovement()) {
            if (getUser().getMovementProcessor().isGround()) {
                groundY = getUser().getMovementProcessor().getFrom().getY();
            }
            double deltaY = getUser().getMovementProcessor().getDeltaY();
            double lastDeltaY = getUser().getMovementProcessor().getLastDeltaY();

            double minus = getUser().getPotionEffectProcessor().hasSlowFalling && getUser().getMovementProcessor().getDeltaY() < 0 ? 0.01 : 0.08;
            double prediction = (lastDeltaY - minus) * 0.98f;
            if (prediction >= 0 && prediction < 0.003) prediction = -0.07242780368044066;
            if (MathUtils.isInBoundsEqual(lastDeltaY, -0.0155550727022007, -0.015555072702199800) ||
                    getUser().getTeleportProcessor().getTicksSinceTeleport() <= 4 && getUser().getMovementProcessor().getLastDeltaY() == 0) {
                prediction = -0.07840000152587923;
            }
            double offset = deltaY - prediction;
            boolean elytraEquipped = PlayerUtils.isElytraEquipped(getUser()) && GroundUtils.isNearGround(getUser());
            if (!MathUtils.isInBoundsEqual(offset, -0.00302, 0.00302)) {
                if (!shouldExempt(getUser(), groundY, lastGroundY)) {
                    if (increaseBuffer(1) >= 5) {
                        if (getUser().getMovementProcessor().getAirTicks() > 10) {
                            increaseBuffer(offsetDivisor(offset));
                        }
                        fail("Gravity", "DeltaY=" + deltaY, "Last DeltaY=" + lastDeltaY, "Predicted=" + prediction, "Offset= " + offset);
                    }
                } else {
                    decreaseBuffer(elytraEquipped ? 0.5 : 0.25);
                }
            } else {
                decreaseBuffer(0.2);
            }
            if (getUser().getMovementProcessor().getAirTicks() == 2) {
                lastGroundY = groundY;
            }
        }
    }

    private int offsetDivisor(double offset) {
        int amount = 0;
        while (offset > 0.03) {
            offset -= 0.03;
            amount++;
        }
        return amount;
    }

    private boolean shouldExempt(User user, double groundY, double lastGroundY) {
        return user.getCollisionProcessor().getHalfBlockTicks() > 0 ||
                user.getCollisionProcessor().getLiquidTicks() > 0 && !(getUser().getMovementProcessor().getDeltaY() >
                        getUser().getMovementProcessor().getJumpMotion(getUser())) || user.getCollisionProcessor().getWebTicks() > 0 ||
                ExemptUtils.canFly(user) || user.getCollisionProcessor().isOnClimable() || GroundUtils.isNearGround(user, 0.075) ||
                user.getMovementProcessor().isGround() && getUser().getMovementProcessor().getDeltaY() <= 0 &&
                        user.getMovementProcessor().getTo().getY() % 0.015625 == 0 || user.getTeleportProcessor().getTicksSinceTeleport() <= 2 ||
                user.getMovementProcessor().getTo().getY() < 0 || user.getVelocityProcessor().getTicksSinceVelocity() <= 1 ||
                user.getMovementProcessor().getTicks() <= 40 && getUser().getMovementProcessor().getDeltaY() == -0.09800000190734881 ||
                user.getCollisionProcessor().getWallTicks() > 0 && !(getUser().getMovementProcessor().getDeltaY() >= getUser().getMovementProcessor().getLastDeltaY()) &&
                        !(getUser().getMovementProcessor().getDeltaY() > 0.42f) || getUser().getCollisionProcessor().isOnWall() ||
                user.getMovementProcessor().getDeltaY() == 0.5f && getUser().getMovementProcessor().getLastDeltaY() == 0 && user.getCollisionProcessor().isHalfBlock() ||
                user.getMovementProcessor().getDeltaY() == 0.42f && getUser().getMovementProcessor().getLastDeltaY() <= 0 && user.getMovementProcessor().isLastGround() ||
                getUser().getCollisionProcessor().isBlockAbove() || user.getMovementProcessor().isGround() &&
                (MathUtils.isInBoundsEqual(user.getMovementProcessor().getDeltaY(), 0.5926045976350593, 0.592604597635062) ||
                        MathUtils.isInBoundsEqual(user.getMovementProcessor().getDeltaY(), 0.445374469504106, 0.445374469504107)) ||
                user.getMovementProcessor().getLastDeltaY() == 0.20000004768371582 &&
                user.getMovementProcessor().getDeltaY() == -0.07840000152587834 && user.getMovementProcessor().getAirTicks() == 2 &&
                (!user.getCollisionProcessor().isBlockAbove() || user.getCollisionProcessor().isCollideHorizontal()) || MathUtils.isInBoundsEqual(user.getMovementProcessor().getDeltaY(), 0.33319999363422337, 0.33319999363422426) &&
                MathUtils.isInBounds(user.getMovementProcessor().getLastDeltaY(), 0.40444491418477, 0.4044449141848)
                && (MathUtils.isInBoundsEqual(groundY - lastGroundY, 0.9, 1.1) && !user.getPotionEffectProcessor().hasJumpPotion) ||
                BlockUtil.getBlock(getUser().getMovementProcessor().getTo().toLocation(getUser().getPlayer().getWorld()).subtract(0, 1.2, 0)).getType()
                        == Material.SLIME_BLOCK || GroundUtils.getSolidBlockUnder(user).subtract(0, 0.2, 0).getBlock().getType() == Material.SLIME_BLOCK
                && user.getMovementProcessor().getDeltaY() <= 1f || GroundUtils.getSolidBlockUnder(user).add(0, 0.2, 0).getBlock().getType() == Material.SLIME_BLOCK
                && user.getMovementProcessor().getDeltaY() <= 1f || user.getCollisionProcessor().getSnowTicks() > 10 || user.getMovementProcessor().getElytraTicks() > 0 ||
                user.getPotionEffectProcessor().getLevitationTicks() > 0 || user.getCollisionProcessor().isHoney();
    }
}