package dev.phoenixhaven.customac.impl.check.badpackets;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInHeldItemSlotPacket;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;

@CheckInfo(name = "BadPackets", type = "D", checkType = CheckType.PACKET)
public class BadPacketsD extends Check {
    private int lastSlot = -1;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_HELD_ITEM_SLOT) {
            WrappedInHeldItemSlotPacket slotPacket = new WrappedInHeldItemSlotPacket(packetEvent.getPacket(), getUser().getPlayer());

            if (slotPacket.getSlot() == lastSlot) {
                fail("Sent same slot");
            }
            this.lastSlot = slotPacket.getSlot();
        }
    }
}
