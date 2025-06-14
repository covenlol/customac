package dev.phoenixhaven.customac.impl.check.badpackets;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;

@CheckInfo(name = "BadPackets", type = "F", checkType = CheckType.PACKET)
public class BadPacketsF extends Check {
    private boolean swing;
    private boolean attacked;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_ARM_ANIMATION) {
            swing = true;
        }
        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_USE_ENTITY) {
            WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(packetEvent.getPacket(), getUser().getPlayer());

            if (useEntityPacket.getEntity() == null) {
                return;
            }

            if (getUser().getPlayer().isDead()) {
                fail("Sent C02 while dead");
            }
        }
        if (packetEvent.isMovement() || packetEvent.isRotation()) {
            if (attacked && !swing) {
                fail("NoSwing");
            }
            attacked = false;
            swing = false;
        }
    }
}
