package dev.phoenixhaven.customac.impl.check.movement.entityflight;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.utils.block.BlockUtil;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;
import dev.phoenixhaven.customac.utils.player.GroundUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.*;
import org.bukkit.entity.*;

@CheckInfo(name = "Entity Flight", type = "A", checkType = CheckType.MOVEMENT)
public class EntityFlightA extends Check {
    private double lastDeltaY;
    private Location lastEntityLocation;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_STEER) {
            if (getUser().getPlayer().getVehicle() != null) {
                Entity entity = getUser().getPlayer().getVehicle();
                if (lastEntityLocation != null) {
                    double deltaY = entity.getLocation().getY() - lastEntityLocation.getY();

                    if (entity instanceof CraftHorse || entity instanceof CraftChestedHorse || entity instanceof CraftSkeletonHorse || entity instanceof CraftZombieHorse) {
                        if (((MathUtils.isInBoundsEqual(deltaY, -0.07, 0.05) || entity.getLocation().getY() == lastEntityLocation.getY())
                                && !GroundUtils.isNearGround(getUser(), 2) || deltaY >= lastDeltaY && deltaY != 0 && lastDeltaY != 0) &&
                                !BlockUtil.getBlock(entity.getLocation().subtract(0, 0.1, 0)).isLiquid()) {
                            if (increaseBuffer(1) >= 3) {
                                fail("Invalid movement in vehicle", "offset: " + (deltaY - lastDeltaY));
                            }
                        } else {
                            decreaseBuffer(0.5);
                        }
                    }
                    if (entity instanceof CraftBoat) {
                        if ((deltaY > 0 || deltaY >= lastDeltaY && deltaY < 0 || deltaY == lastDeltaY && !GroundUtils.isNearGround(getUser())) && !BlockUtil.getBlock(entity.getLocation().subtract(0, 0.1, 0)).isLiquid()) {
                            if (increaseBuffer(1) >= 2) {
                                fail("Invalid movement in boat", String.valueOf(deltaY));
                            }
                        } else {
                            decreaseBuffer(0.25);
                        }
                    }
                    lastDeltaY = deltaY;
                }
                lastEntityLocation = entity.getLocation();
            }
        }
    }
}
