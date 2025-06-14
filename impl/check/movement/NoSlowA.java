package dev.phoenixhaven.customac.impl.check.movement;

import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInHeldItemSlotPacket;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutHeldItemSlot;
import cc.funkemunky.api.utils.MiscUtils;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.block.BlockUtil;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@CheckInfo(name = "NoSlow", type = "A", checkType = CheckType.MOVEMENT)
public class NoSlowA extends Check {
    private int useTicks;
    private boolean sneaking;
    private int lastSlot;
    @Setter
    private boolean blocking;
    private final String[] items = {"APPLE", "COOKED", "RAW", "POTION", "MILK", "BOW", "SHIELD", "HONEY_BOTTLE"};

    public void handleConsume() {
        blocking = false;
    }

    public void handleUseItem(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            for (String item : items) {
                if (event.getItem().getType().name().contains(item)) {
                    if (event.getItem().getType() == Material.SPLASH_POTION || event.getItem().getType() == Material.LINGERING_POTION) {
                        return;
                    }
                    blocking = true;
                    if (getUser().getMovementProcessor().getDeltaXZ() >= getUser().getMovementProcessor().getGroundSpeed(getUser()) && getUser().getMovementProcessor().getGroundTicks() > 3) {
                        if (increaseBuffer(1) >= 2) {
                            fail("Didn't slow down fast enough");
                        }
                    } else {
                        decreaseBuffer(1);
                    }
                    break;
                }
            }
        } else {
            blocking = false;
        }
    }

    @Override
    public void processPacket(PacketEvent packetEvent) {
        switch (packetEvent.getPacketType()) {
            case CLIENT_ENTITY_ACTION:
                WrappedInEntityActionPacket entityActionPacket = new WrappedInEntityActionPacket(packetEvent.getPacket(), getUser().getPlayer());
                if (entityActionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.START_SNEAKING) {
                    sneaking = true;
                }
                if (entityActionPacket.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SNEAKING) {
                    sneaking = false;
                }
                break;
            case CLIENT_BLOCK_DIG:
            case SERVER_OPEN_WINDOW:
                blocking = false;
                useTicks = 0;
                break;
            case CLIENT_HELD_ITEM_SLOT:
                WrappedInHeldItemSlotPacket heldItemSlotPacket = new WrappedInHeldItemSlotPacket(packetEvent.getPacket(), getUser().getPlayer());

                if (lastSlot != heldItemSlotPacket.getSlot()) {
                    blocking = false;
                    useTicks = 2;
                    lastSlot = heldItemSlotPacket.getSlot();
                }
                break;
        }
        if (packetEvent.isMovement()) {
            if (shouldExempt(getUser())) return;
            useTicks = (blocking || sneaking) ? useTicks + 1 : 0;

            double angle = Math.abs(MathUtils.getAngleRotation(getUser().getMovementProcessor().getTo().clone(),
                    getUser().getMovementProcessor().getFrom().clone()));
            double speed = (getUser().getMovementProcessor().getGroundSpeed(getUser()) * (sneaking ? 0.4f : 0.2375f +
                            (MathUtils.angleDivisor(angle, 180) < -2 ? 0.023 : 0) +
                            (getUser().getPotionEffectProcessor().speedPotionEffectLevel * 0.05) +
                    (0.311 / getUser().getMovementProcessor().getGroundTicks()) + (0.311 / getUser().getMovementProcessor().getAirTicks())) *
                    (getUser().getPotionEffectProcessor().hasSlowness && getUser().getMovementProcessor().getGroundTicks() == 1 ? 2 : 1)); //fixes some stupid false with high slowness

            if (getUser().getPlayer().getInventory().getBoots() != null) {
                ItemStack boots = getUser().getPlayer().getInventory().getBoots();

                if (boots.containsEnchantment(Enchantment.SOUL_SPEED) && getUser().getCollisionProcessor().getSoulSandTicks() > 0) {
                    speed += boots.getEnchantmentLevel(Enchantment.SOUL_SPEED) * 0.1f;
                }
            }

            if (sneaking && useTicks >= 2 && useTicks < 5) {
                if (getUser().getMovementProcessor().getLastDeltaXZ() * 0.98f < getUser().getMovementProcessor().getDeltaXZ() &&
                        getUser().getMovementProcessor().getGroundTicks() != 1 && getUser().getMovementProcessor().getDeltaXZ() > getUser().getMovementProcessor().getGroundSpeed(getUser()) * 0.5f) {
                    if (increaseBuffer(1) >= 2) {
                        fail("Moving too fast while sneaking", String.valueOf(useTicks), String.valueOf(getUser().getMovementProcessor().getDeltaXZ()),
                                String.valueOf(speed), String.valueOf(getUser().getMovementProcessor().getGroundTicks()),
                                String.valueOf(getUser().getMovementProcessor().getAirTicks()));
                        getUser().getPlayer().teleport(getUser().getMovementProcessor().getFrom().toLocation(getUser().getPlayer().getWorld()));
                    }
                } else {
                    decreaseBuffer(1);
                }
            }
            if (useTicks > (getUser().getMovementProcessor().getAirTicks() > 4 ? 10 : 8) && getUser().getMovementProcessor().getDeltaXZ() > speed) {
                if (blocking) {
                    if (increaseBuffer(1) >= 3) {
                        fail("Moving too fast while using an item", String.valueOf(useTicks), String.valueOf(getUser().getMovementProcessor().getDeltaXZ()),
                                String.valueOf(speed), String.valueOf(getUser().getMovementProcessor().getGroundTicks()),
                                String.valueOf(getUser().getMovementProcessor().getAirTicks()), String.valueOf(angle));
                        TinyProtocolHandler.sendPacket(getUser().getPlayer(), new WrappedOutHeldItemSlot(lastSlot == 8 ? 0 : lastSlot + 1));
                        blocking = false;
                        sneaking = false;
                    }
                } else if (sneaking) {
                    if (increaseBuffer(1) >= 2) {
                        fail("Moving too fast while sneaking", String.valueOf(useTicks), String.valueOf(getUser().getMovementProcessor().getDeltaXZ()),
                                String.valueOf(speed), String.valueOf(getUser().getMovementProcessor().getGroundTicks()),
                                String.valueOf(getUser().getMovementProcessor().getAirTicks()));
                        TinyProtocolHandler.sendPacket(getUser().getPlayer(), new WrappedOutHeldItemSlot(lastSlot == 8 ? 0 : lastSlot + 1));
                    }
                } else {
                    decreaseBuffer(0.125);
                }
            }
        }
    }

    private boolean shouldExempt(User user) {
        return BlockUtil.getBlock(user.getPlayer().getLocation().subtract(0, 0.1, 0)).getType().toString().toLowerCase().contains("ice") ||
                getUser().getVelocityProcessor().getTicksSinceVelocity() <= 10 || ExemptUtils.canFly(getUser()) || user.getPlayer().isSwimming() ||
                !MiscUtils.getNearbyEntities(user.getPlayer(), 0.3, 0.3).isEmpty() || user.getMovementProcessor().getElytraTicks() > 0;
    }
}
