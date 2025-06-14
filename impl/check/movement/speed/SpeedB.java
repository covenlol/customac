package dev.phoenixhaven.customac.impl.check.movement.speed;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@CheckInfo(name = "Speed", type = "B", checkType = CheckType.MOVEMENT)
public class SpeedB extends Check {
    private double lastGroundY;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement() || packetEvent.isRotation()) {
            if (getUser().getMovementProcessor().isGround()) {
                double deltaXZ = this.getUser().getMovementProcessor().getDeltaXZ();
                int groundTicks = getUser().getMovementProcessor().getGroundTicks();
                double max = getUser().getMovementProcessor().getGroundSpeed(getUser())
                        + (getUser().getCollisionProcessor().getIceTicks() > 0 ? 0.2 : 0) + (getUser().getCollisionProcessor().getSlimeTicks() > 0 ? 0.02 : 0) +
                        (getUser().getVelocityProcessor().getTicksSinceVelocity() <= 10 ? 0.025 : 0) +
                        (getUser().getCollisionProcessor().isHalfBlock() ? 0.04 : 0) +
                        (getUser().getCollisionProcessor().getBlockAboveTicks() > 0 && getUser().getCollisionProcessor().getIceTicks() > 0 ? 0.3 : 0) +
                        (getUser().getCollisionProcessor().getSnowTicks() > 0 ? 0.03 : 0) +
                        (getUser().getCollisionProcessor().checkSlimeNear(getUser()) && getUser().getCollisionProcessor().getIceTicks() > 0 ? 0.3 : 0);
                if (getUser().getPlayer().getInventory().getBoots() != null) {
                    ItemStack boots = getUser().getPlayer().getInventory().getBoots();

                    if (boots.containsEnchantment(Enchantment.SOUL_SPEED) && getUser().getCollisionProcessor().getSoulSandTicks() > 0) {
                        max += boots.getEnchantmentLevel(Enchantment.SOUL_SPEED) * 0.1f;
                    }
                }
                if (getUser().getPotionEffectProcessor().hasSlowness) {
                    max += 0.16 / groundTicks;
                }
                switch (groundTicks) {
                    case 1:
                        max += 0.0281;
                        if (getUser().getCollisionProcessor().getBlockAboveTicks() > 0) {
                            max += 0.07405;
                        }
                        break;
                    case 2:
                        max += 0.1178;
                        if (getUser().getCollisionProcessor().getBlockAboveTicks() > 0) {
                            max += 0.034;
                        }
                        if (getUser().getMovementProcessor().getTo().getY() - lastGroundY >= 0.5) {
                            max += 0.0043;
                        }
                        lastGroundY = getUser().getMovementProcessor().getTo().getY();
                        break;
                    case 3:
                        max += 0.075;
                        break;
                    case 4:
                        max += 0.065;
                        break;
                    case 5:
                        max += 0.05;
                        break;
                }

                if (deltaXZ > max && !shouldExempt(getUser())) {
                    fail("Moving too fast on ground", "DeltaXZ=" + deltaXZ, "Max DeltaXZ=" + max,
                            String.valueOf(getUser().getPotionEffectProcessor().getSpeedPotionEffectLevel()),
                            String.valueOf(getUser().getMovementProcessor().getGroundTicks()),
                            String.valueOf(deltaXZ - max));
                    if (deltaXZ > 20) {
                        getUser().runKickSync("java.lang.NullPointerException");
                    }
                }
            }
        }
    }

    private boolean shouldExempt(User user) {
        return ExemptUtils.canFly(user) || user.getMovementProcessor().getElytraTicks() > 0 || user.getMovementProcessor().getRiptideTicks() > 0;
    }
}
