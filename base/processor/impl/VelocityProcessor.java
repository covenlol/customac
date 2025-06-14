package dev.phoenixhaven.customac.base.processor.impl;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import dev.phoenixhaven.customac.base.processor.api.Processor;
import dev.phoenixhaven.customac.base.processor.api.ProcessorInfo;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;
import lombok.Getter;

@Getter
@ProcessorInfo("VelocityProcessor")
public class VelocityProcessor extends Processor {
    private int ticksSinceVelocity;
    private double velocityX, velocityY, velocityZ;

    public void handlePacket(Object packet, String type, User user) {
        if (type.equalsIgnoreCase(Packet.Server.ENTITY_VELOCITY)) {
            WrappedOutVelocityPacket velocityPacket = new WrappedOutVelocityPacket(packet, user.getPlayer());

            if (velocityPacket.getId() == user.getPlayer().getEntityId()) {
                user.getTransactionHelper().runTransaction(() -> this.ticksSinceVelocity = 0, user);
                velocityX = velocityPacket.getX();
                velocityY = velocityPacket.getY();
                velocityZ = velocityPacket.getZ();
            }
        } else if (PacketUtils.isMovement(type)) {
            this.ticksSinceVelocity++;
        }
    }
}
