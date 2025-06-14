package dev.phoenixhaven.customac.impl.check.combat.autoblock;

import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInHeldItemSlotPacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutHeldItemSlot;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.impl.check.movement.NoSlowA;
import lombok.Setter;

@CheckInfo(name = "Autoblock", type = "A", checkType = CheckType.COMBAT)
public class AutoBlockA extends Check {
    @Setter
    private boolean blocked;
    private int lastSlot;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        switch (packetEvent.getPacketType()) {
            case CLIENT_BLOCK_PLACE:
                if (getUser().getPlayer().getInventory().getItemInOffHand().getType().name().contains("Shield")) {
                    blocked = true;
                }
                break;
            case CLIENT_BLOCK_DIG:
            case SERVER_OPEN_WINDOW:
                blocked = false;
                break;
            case CLIENT_HELD_ITEM_SLOT:
                WrappedInHeldItemSlotPacket heldItemSlotPacket = new WrappedInHeldItemSlotPacket(packetEvent.getPacket(), getUser().getPlayer());

                if (lastSlot != heldItemSlotPacket.getSlot()) {
                    blocked = false;
                    lastSlot = heldItemSlotPacket.getSlot();
                }
                break;
            case CLIENT_USE_ENTITY:
                WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(packetEvent.getPacket(), getUser().getPlayer());
                if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                    if (increaseBuffer(1) > 2 && blocked) {
                        fail("Attacked while blocking");
                        TinyProtocolHandler.sendPacket(getUser().getPlayer(), new WrappedOutHeldItemSlot(lastSlot == 8 ? 0 : lastSlot + 1));
                        blocked = false;
                    } else if (!blocked) {
                        decreaseBuffer(getBuffer() > 0 ? 0.25 : 0);
                    }
                }
                break;
        }
    }
}
