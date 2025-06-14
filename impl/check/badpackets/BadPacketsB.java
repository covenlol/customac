package dev.phoenixhaven.customac.impl.check.badpackets;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;

@CheckInfo(name = "BadPackets", type = "B", checkType = CheckType.PACKET)
public class BadPacketsB extends Check {
    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_STEER && !getUser().getPlayer().isInsideVehicle()) {
            if (!shouldExempt(getUser())) {
                if (increaseBuffer(1) >= 5) {
                    fail("Null vehicle while sending C0C");
                }
            }
        } else if (getUser().getPlayer().isInsideVehicle()) {
            decreaseBuffer(1);
        }
    }

    private boolean shouldExempt(User user) {
        return ExemptUtils.isNearRideableEntity(user, 1.5);
    }
}
