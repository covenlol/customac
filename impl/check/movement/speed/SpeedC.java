package dev.phoenixhaven.customac.impl.check.movement.speed;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import net.minecraft.server.v1_16_R3.ItemElytra;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@CheckInfo(name = "Speed", type = "C", checkType = CheckType.MOVEMENT)
public class SpeedC extends Check {
    private double lastGroundY;
    private double groundY;
    private boolean lastMounted;
    private int lastBuffer;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isRotation()) {
            lastMounted = getUser().getPlayer().getVehicle() != null;
        }
        if (packetEvent.isMovement()) {
            ItemStack chestplate = getUser().getPlayer().getInventory().getChestplate();
            int buffer = chestplate != null && chestplate.getType().name().equals("ELYTRA") ? 8 : 4;
            if (getUser().getMovementProcessor().isGround()) {
                groundY = getUser().getMovementProcessor().getTo().getY();
            }
            if (lastBuffer != buffer) {
                setBuffer(1);
            }
            if (getUser().getMovementProcessor().getAirTicks() > 0) {
                double max = ((0.3598 + (getUser().getCollisionProcessor().getIceTicks() > 0 ? 0.25 : 0) +
                        (getUser().getPotionEffectProcessor().speedPotionEffectLevel * 0.019) +
                        (getUser().getCollisionProcessor().getBlockAboveTicks() > 0 ? 0.02 : 0)) *
                        (getUser().getMovementProcessor().getAirTicks() == 1 ? 1.725 : 1)) -
                        (getUser().getMovementProcessor().getAirTicks() >= 3 && getUser().getMovementProcessor().getAirTicks() <= 11 ?
                        0.002 * getUser().getMovementProcessor().getAirTicks() : 0) +
                        (MathUtils.isInBoundsEqual(groundY - lastGroundY, 0.9, 1.1) ? 0.02 : 0);

                if (getUser().getPlayer().getInventory().getBoots() != null) {
                    ItemStack boots = getUser().getPlayer().getInventory().getBoots();

                    if (boots.containsEnchantment(Enchantment.SOUL_SPEED) && getUser().getCollisionProcessor().getSoulSandTicks() > 0) {
                        max += boots.getEnchantmentLevel(Enchantment.SOUL_SPEED) * 0.1f;
                    }
                }

                if (getUser().getMovementProcessor().getDeltaXZ() > max * 2 &&
                        !(getUser().getTeleportProcessor().getTicksSinceTeleport() <= 4)) {
                    increaseBuffer(1);
                }
                if (getUser().getMovementProcessor().getDeltaXZ() > max) {
                    if (!shouldExempt(getUser())) {
                        if (increaseBuffer(1) >= buffer) {
                            fail("Moving too fast in air", "DeltaXZ=" + getUser().getMovementProcessor().getDeltaXZ(), "Max= " + max, String.valueOf(getUser().getMovementProcessor().getAirTicks()));
                        }
                    }
                } else {
                    decreaseBuffer(1);
                }
            } else {
                decreaseBuffer(0.5);
            }
            if (getUser().getMovementProcessor().getAirTicks() == 1) {
                lastGroundY = groundY;
            }
            lastBuffer = buffer;
        }
    }

    public boolean shouldExempt(User user) {
        return user.getMovementProcessor().getFlyingTicks() > 0 || getUser().getCollisionProcessor().getSlimeTicks() > 0 ||
                user.getTeleportProcessor().getTicksSinceTeleport() <= 2 && !(user.getMovementProcessor().getDeltaXZ() > 1) ||
                getUser().getVelocityProcessor().getTicksSinceVelocity() <= 10 && !(user.getMovementProcessor().getDeltaXZ() > 1) ||
                user.getCollisionProcessor().getHalfBlockTicks() > 0 || lastMounted || user.getMovementProcessor().getElytraTicks() > 0;
    }
}
