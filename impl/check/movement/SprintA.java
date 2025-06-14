package dev.phoenixhaven.customac.impl.check.movement;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@CheckInfo(name = "Sprint", type = "A", checkType = CheckType.MOVEMENT)
public class SprintA extends Check {
    private boolean attack;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement()) {
            double angle = Math.abs(MathUtils.getAngleRotation(getUser().getMovementProcessor().getTo().clone(), getUser().getMovementProcessor().getFrom().clone()));

            if (angle < 30 || !getUser().getPlayer().isSprinting() && getUser().getMovementProcessor().getGroundTicks() > 10) {
                if (getUser().getMovementProcessor().getDeltaXZ() > getUser().getMovementProcessor().getGroundSpeed(getUser()) * 0.75f && !shouldExempt(getUser())) {
                    if (increaseBuffer(1) >= 2) {
                        getUser().runTeleportSync(getUser().getMovementProcessor().getFrom().toLocation(getUser().getPlayer().getWorld()));
                        fail("Invalid Sprint", String.valueOf(getUser().getMovementProcessor().getDeltaXZ()), String.valueOf(getUser().getMovementProcessor().getGroundSpeed(getUser()) * 0.75f));
                        decreaseBuffer(2);
                    }
                }
            } else {
                decreaseBuffer(0.5);
            }
            if (attack && !soulspeed()) {
                double calc = getUser().getMovementProcessor().getLastDeltaXZ() * 0.925f +
                        (!getUser().getPlayer().isSprinting() ? 0.02 : 0);
                if (getUser().getMovementProcessor().getDeltaXZ() > calc && calc > 0.15 &&
                        getUser().getMovementProcessor().getLastDeltaXZ() != 0.5625) {
                    if (increaseBuffer(1) >= 2) {
                        fail("Keepsprint", String.valueOf(getUser().getMovementProcessor().getDeltaXZ()),
                                String.valueOf(calc),
                                String.valueOf(getUser().getMovementProcessor().getDeltaXZ() - calc));
                    }
                } else {
                    decreaseBuffer(0.5);
                }
                attack = false;
            }
        }
        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_USE_ENTITY) {
            WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(packetEvent.getPacket(), getUser().getPlayer());

            if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                attack = true;
            }
        }
    }

    private boolean shouldExempt(User user) {
        return user.getTeleportProcessor().getTicksSinceTeleport() <= 20 || user.getMovementProcessor().getGroundTicks() < 5 ||
                user.getVelocityProcessor().getTicksSinceVelocity() <= 10 || user.getCollisionProcessor().getIceTicks() > 0 ||
                soulspeed();
    }

    private boolean soulspeed() {
        if (getUser().getPlayer().getInventory().getBoots() != null) {
            ItemStack boots = getUser().getPlayer().getInventory().getBoots();

            return boots.containsEnchantment(Enchantment.SOUL_SPEED) && getUser().getCollisionProcessor().getSoulSandTicks() > 0;
        }
        return false;
    }
}
