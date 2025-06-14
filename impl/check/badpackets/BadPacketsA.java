package dev.phoenixhaven.customac.impl.check.badpackets;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;

@CheckInfo(name = "BadPackets", type = "A", checkType = CheckType.PACKET)
public class BadPacketsA extends Check {

    @Override
    public void processPacket(PacketEvent event) {
        if (event.isMovement()) {
            float pitch = this.getUser().getMovementProcessor().getTo().getPitch();

            if (Math.abs(pitch) > 90) {
                if (increaseBuffer(1) >= 2) { // have some leniency for mc's stupid protocol
                    this.fail("pitch: " + pitch);
                }
            }
            if (getUser().getTeleportProcessor().getTicksSinceTeleport() <= 2) {
                decreaseBuffer(1);
            }
        }
    }
}
