package dev.phoenixhaven.customac.base.event.impl;

import dev.phoenixhaven.customac.base.event.api.Event;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;
import lombok.Getter;

@Getter
public class PacketEvent extends Event {
    private final Object packet;
    private final String type;
    private final long timestamp;

    private final boolean isMovement;
    private final boolean isRotation;

    private final PacketUtils.Packets packetType;

    public PacketEvent(Object packet, String type, long timestamp) {
        this.packet = packet;
        this.type = type;
        this.timestamp = timestamp;

        this.isMovement = PacketUtils.isMovement(type);
        this.isRotation = PacketUtils.isRotation(type);

        this.packetType = PacketUtils.toPacket(this);
    }
}
