package dev.phoenixhaven.customac.impl.check.badpackets;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;

@CheckInfo(name = "BadPackets", type = "C", checkType = CheckType.PACKET)
public class BadPacketsC extends Check {
    private double lastCockAlive;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_KEEPALIVE) {
            WrappedInKeepAlivePacket keepAlivePacket = new WrappedInKeepAlivePacket(packetEvent.getPacket(), getUser().getPlayer());

            if (keepAlivePacket.getTime() == lastCockAlive) {
                fail("Same C00 Key");
            }

            lastCockAlive = keepAlivePacket.getTime();
        }
    }
}
