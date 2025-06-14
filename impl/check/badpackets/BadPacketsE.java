package dev.phoenixhaven.customac.impl.check.badpackets;

import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInHeldItemSlotPacket;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutHeldItemSlot;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;

@CheckInfo(name = "BadPackets", type = "E", checkType = CheckType.PACKET)
public class BadPacketsE extends Check {
    private boolean place;
    private boolean dig;
    private int lastSlot;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        switch (packetEvent.getPacketType()) {
            case CLIENT_BLOCK_PLACE:
                place = true;
                break;
            case CLIENT_BLOCK_DIG:
                WrappedInBlockDigPacket digPacket = new WrappedInBlockDigPacket(packetEvent.getPacket(), getUser().getPlayer());
                if (digPacket.getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.RELEASE_USE_ITEM) {
                    dig = true;
                }
                break;
            case CLIENT_HELD_ITEM_SLOT:
                WrappedInHeldItemSlotPacket slot = new WrappedInHeldItemSlotPacket(packetEvent.getPacket(), getUser().getPlayer());
                lastSlot = slot.getSlot();
                break;
        }
        if (packetEvent.isRotation() || packetEvent.isMovement()) {
            if (place && dig) {
                fail("Sent C08 and C07 in the same tick");
                TinyProtocolHandler.sendPacket(getUser().getPlayer(), new WrappedOutHeldItemSlot(lastSlot));
            }
            place = false;
            dig = false;
        }
    }
}
