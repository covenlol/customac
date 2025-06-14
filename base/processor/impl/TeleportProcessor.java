package dev.phoenixhaven.customac.base.processor.impl;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutPositionPacket;
import dev.phoenixhaven.customac.base.processor.api.Processor;
import dev.phoenixhaven.customac.base.processor.api.ProcessorInfo;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;
import dev.phoenixhaven.customac.utils.vector.Vec5D;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ProcessorInfo("TeleportProcessor")
public class TeleportProcessor extends Processor {
    private List<Vec5D> teleports = new ArrayList<>();
    private int ticksSinceTeleport;
    private boolean hasTeleported;

    public void handlePacket(String type, Object packet, User user) {
        if (type.equalsIgnoreCase(Packet.Server.POSITION)) {
            this.ticksSinceTeleport = 0;
            WrappedOutPositionPacket positionPacket = new WrappedOutPositionPacket(packet, user.getPlayer());
            teleports.add(new Vec5D(positionPacket.getX(), positionPacket.getY(), positionPacket.getZ(), positionPacket.getYaw(), positionPacket.getPitch()));
        }
        if (type.equalsIgnoreCase(Packet.Client.POSITION_LOOK)) {
            WrappedInFlyingPacket flyingPacket = new WrappedInFlyingPacket(packet, user.getPlayer());
            if (flyingPacket.isLook()) {
                if (teleports.size() > 0) {
                    boolean posX = MathUtils.isInBoundsEqual(flyingPacket.getX() - teleports.get(0).getX(), -1E-10, 1E-10);
                    boolean posY = MathUtils.isInBoundsEqual(flyingPacket.getY() - teleports.get(0).getY(), -1E-10, 1E-10);
                    boolean posZ = MathUtils.isInBoundsEqual(flyingPacket.getZ() - teleports.get(0).getZ(), -1E-10, 1E-10);
                    boolean yaw = MathUtils.isInBoundsEqual(flyingPacket.getYaw() - teleports.get(0).getYa(), -1E-10, 1E-10);
                    boolean pitch = MathUtils.isInBoundsEqual(flyingPacket.getPitch() - teleports.get(0).getPi(), -1E-10, 1E-10);

                    if (posX && posY && posZ && yaw && pitch && !flyingPacket.isGround()) {
                        teleports.remove(0);
                        this.ticksSinceTeleport = 0;
                    }
                }
            }
        }
        if (PacketUtils.isMovement(type)) {
            this.ticksSinceTeleport++;
        }
    }
}
