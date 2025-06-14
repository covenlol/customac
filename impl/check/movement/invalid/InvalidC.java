package dev.phoenixhaven.customac.impl.check.movement.invalid;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.block.BlockUtil;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;
import dev.phoenixhaven.customac.utils.player.GroundUtils;
import org.bukkit.Material;

@CheckInfo(name = "Invalid", type = "C", checkType = CheckType.MOVEMENT)
public class InvalidC extends Check {
    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement()) {
            if (getUser().getCollisionProcessor().getClimbableTicks() > 10 && getUser().getCollisionProcessor().isOnClimable()) {
                if (!shouldExempt(getUser())) {
                    if (increaseBuffer(1) >= 2) {
                        if (getUser().getMovementProcessor().getDeltaXZ() > 0.151f &&
                                !(GroundUtils.getBlockUnder(getUser()).getY() > getUser().getMovementProcessor().getTo().getY() - 0.5)) {
                            fail("Invalid movement on climbable");
                        }
                        if (Math.abs(getUser().getMovementProcessor().getDeltaY()) > 0.151f && !(GroundUtils.getSolidBlockUnder(getUser()).getY() >
                                getUser().getMovementProcessor().getTo().getY() - 3)) {
                            fail("Invalid movement on climbable", String.valueOf(getUser().getMovementProcessor().getDeltaY()));
                        }
                    }
                }
            } else {
                decreaseBuffer(0.1);
            }
            if (getUser().getCollisionProcessor().isInWeb() && getUser().getCollisionProcessor().getWebTicks() > 3) {
                double limitXZ = getUser().getMovementProcessor().getGroundSpeed(getUser()) * (getUser().getMovementProcessor().getDeltaY() > 0 ? 0.305 : 0.1505);
                double limitY = getUser().getMovementProcessor().getJumpMotion(getUser()) * 0.05f;
                if (getUser().getMovementProcessor().getDeltaXZ() > limitXZ || getUser().getMovementProcessor().getDeltaY() > limitY) {
                    if (!shouldExempt2(getUser())) {
                        fail("Invalid movement in web", String.valueOf(getUser().getMovementProcessor().getDeltaXZ() - limitXZ),
                                String.valueOf(getUser().getMovementProcessor().getDeltaY() - limitY));
                    }
                }
            }
        }
    }

    private boolean shouldExempt(User user) {
        return ExemptUtils.canFly(user) || GroundUtils.getBlockUnder(getUser()).getBlock().getType() == Material.AIR &&
                BlockUtil.getBlock(getUser().getMovementProcessor().getTo().toLocation(getUser().getPlayer().getWorld()).add(0, 0.5, 0)).getType() == Material.AIR ||
                user.getTeleportProcessor().getTicksSinceTeleport() <= 10 || user.getPotionEffectProcessor().getLevitationTicks() > 0 ||
                BlockUtil.getBlock(user.getMovementProcessor().getFrom().toLocation(getUser().getPlayer().getWorld()).subtract(0, 0.05, 0)).getType() == Material.SCAFFOLDING &&
                        BlockUtil.getBlock(user.getMovementProcessor().getFrom().toLocation(getUser().getPlayer().getWorld()).add(0, 0.05, 0)).getType() != Material.SCAFFOLDING;
    }

    private boolean shouldExempt2(User user) {
        return ExemptUtils.canFly(user) || user.getTeleportProcessor().getTicksSinceTeleport() <= 5 ||
                getUser().getCollisionProcessor().isHalfBlock() || getUser().getCollisionProcessor().getSoulSandTicks() > 0 &&
                getUser().getMovementProcessor().getLastDeltaY() == 0.125;
    }
}
