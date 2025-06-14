package dev.phoenixhaven.customac.impl.check.combat.autoclicker;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.utils.java.Timer;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;

@CheckInfo(name = "Autoclicker", type = "A", checkType = CheckType.COMBAT)
public class AutoclickerA extends Check {
    private int packets = 0;
    private final Timer timer = new Timer();
    private boolean attacked;
    private int limit = 20;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_ARM_ANIMATION) {
            packets++;
            if (timer.hasReached(1000)) {
                limit += attacked ? 0 : 2;
                if (packets > limit) {
                    if (increaseBuffer(1) >= 2) {
                        fail("Too high cps", String.valueOf(packets));
                    }
                } else {
                    decreaseBuffer(0.2);
                }
                packets = 0;
                attacked = false;
                timer.reset();
            }
        }

        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_USE_ENTITY) {
            WrappedInUseEntityPacket useEntityPacket = new WrappedInUseEntityPacket(packetEvent.getPacket(), getUser().getPlayer());

            attacked = useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK;
        }
    }
}
