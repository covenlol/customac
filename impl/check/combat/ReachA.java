package dev.phoenixhaven.customac.impl.check.combat;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.utils.player.CustomLocation;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "Reach", type = "A", checkType = CheckType.COMBAT)
public class ReachA extends Check {
    private final List<Double> pastLocations = new ArrayList<>();

    @Override
    public strictfp void processPacket(PacketEvent packetEvent) {
        if (packetEvent.getType().equalsIgnoreCase(Packet.Client.USE_ENTITY)) {
            WrappedInUseEntityPacket useEntityPacket
                    = new WrappedInUseEntityPacket(packetEvent.getPacket(), this.getUser().getPlayer());

            double max = 3.25;

            if (useEntityPacket.getAction() == WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK) {
                if (useEntityPacket.getEntity() != null) {
                    CustomLocation targetLocation = new CustomLocation(useEntityPacket.getEntity().getLocation(), false);
                    double reach = targetLocation.distanceToHorizontal(getUser().getMovementProcessor().getTo());
                    pastLocations.add(reach);
                    double average = 0;
                    for (double pastLocation : pastLocations) {
                        average += pastLocation;
                    }
                    if (pastLocations.size() > 20) {
                        if ((average / pastLocations.size()) >= max) {
                            if (increaseBuffer(1) >= 3) {
                                fail("Average Reach = " + average / pastLocations.size());
                            }
                        } else {
                            if (reach < average / pastLocations.size() - 0.2) {
                                decreaseBuffer(1);
                            }
                        }
                        pastLocations.remove(0);
                    }
                }
            }
        }
    }
}
