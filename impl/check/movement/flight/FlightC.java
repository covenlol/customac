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
import org.bukkit.Material;

@CheckInfo(name = "Flight", type = "C", checkType = CheckType.MOVEMENT)
public class FlightC extends Check {
    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement() || packetEvent.isRotation()) {
            if (!shouldExempt(getUser())) {
                if ((getUser().getMovementProcessor().getDeltaY() <= 0 && getUser().getMovementProcessor().getLastDeltaY() > 0.1084f ||
                        !MathUtils.isInBoundsEqual(Math.abs(getUser().getMovementProcessor().getLastDeltaY() - getUser().getMovementProcessor().getDeltaY()), -0.1085f, 0.1085f) &&
                                getUser().getMovementProcessor().getAirTicks() > 2 && getUser().getMovementProcessor().getDeltaY() <= 0 && !GroundUtils.isNearGround(getUser(), 0.25)
                        || getUser().getMovementProcessor().getDeltaY() < 0 &&
                        getUser().getMovementProcessor().getLastDeltaY() > 0.0831f) ||
                        getUser().getMovementProcessor().getLastDeltaY() == 0 && getUser().getMovementProcessor().getDeltaY() < -0.0784001 &&
                                !(getUser().getCollisionProcessor().getWebTicks() > 0) && !(getUser().getCollisionProcessor().checkSlimeNear(getUser())) &&
                                !(getUser().getTeleportProcessor().getTicksSinceTeleport() <= 3) || getUser().getMovementProcessor().getLastDeltaY() -
                        getUser().getMovementProcessor().getDeltaY() > 0.3 && !(getUser().getTeleportProcessor().getTicksSinceTeleport() <= 10)) {
                    fail("Yport", "DeltaY=" + getUser().getMovementProcessor().getDeltaY(), "Last DeltaY=" + getUser().getMovementProcessor().getLastDeltaY());
                }
            }
        }
    }

    public boolean shouldExempt(User user) {
        return ExemptUtils.canFly(user) || user.getCollisionProcessor().getHalfBlockTicks() > 0 || user.getCollisionProcessor().getBlockAboveTicks() > 0 ||
                user.getCollisionProcessor().getSlabTicks() > 0 || user.getCollisionProcessor().getSnowTicks() > 0 || user.getCollisionProcessor().isInWeb() ||
                ExemptUtils.isNearRideableEntity(user) || user.getMovementProcessor().isGround() &&
                (MathUtils.isInBoundsEqual(user.getMovementProcessor().getLastDeltaY(), 0.5926045976350593, 0.592604597635062) ||
                        MathUtils.isInBoundsEqual(user.getMovementProcessor().getLastDeltaY(), 0.445374469504106, 0.445374469504107)) ||
                user.getCollisionProcessor().getPistionTicks() > 0 || user.getCollisionProcessor().getSlimeTicks() > 0 ||
                user.getTeleportProcessor().getTicksSinceTeleport() <= 4 || user.getVelocityProcessor().getTicksSinceVelocity() <= 2 ||
                user.getCollisionProcessor().getCarpetTicks() > 0 || user.getCollisionProcessor().getLillyPadTicks() > 0 ||
                user.getCollisionProcessor().isOnClimable() || user.getCollisionProcessor().getWallTicks() > 0 ||
                user.getCollisionProcessor().getLiquidTicks() > 0 || user.getMovementProcessor().getLastDeltaY() == 0.20000004768371582 &&
                user.getMovementProcessor().getDeltaY() == -0.07840000152587834 && user.getMovementProcessor().getAirTicks() == 2 &&
                (!user.getCollisionProcessor().isBlockAbove() || user.getCollisionProcessor().isCollideHorizontal()) ||
                BlockUtil.getBlock(getUser().getMovementProcessor().getTo().toLocation(getUser().getPlayer().getWorld()).subtract(0, 1.2, 0)).getType()
                        == Material.SLIME_BLOCK || GroundUtils.getSolidBlockUnder(user).subtract(0, 0.2, 0).getBlock().getType() == Material.SLIME_BLOCK
                && user.getMovementProcessor().getDeltaY() > -0.16 || GroundUtils.getSolidBlockUnder(user).add(0, 0.2, 0).getBlock().getType() == Material.SLIME_BLOCK
                && user.getMovementProcessor().getDeltaY() > -0.16 || BlockUtil.getBlock(user.getMovementProcessor().getTo().
                toLocation(user.getPlayer().getWorld()).subtract(0, 1, 0)).getType() == Material.SLIME_BLOCK &&
                user.getMovementProcessor().getDeltaY() > -0.09 || getUser().getPotionEffectProcessor().getLevitationTicks() > 0
                || user.getCollisionProcessor().isHoney() || user.getMovementProcessor().isElytra() || user.getCollisionProcessor().getScaffoldingTicks() > 0;
    }
}
