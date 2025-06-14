package dev.phoenixhaven.customac.base.processor.impl;

import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.processor.api.Processor;
import dev.phoenixhaven.customac.base.processor.api.ProcessorInfo;
import dev.phoenixhaven.customac.base.user.User;

@ProcessorInfo("PacketProcessor")
public class PacketProcessor extends Processor {
    public void handlePacket(Object packet, String type, long timestamp, User user) {
        user.getTransactionProcessor().handlePacket(packet, type, user);
        user.getMovementProcessor().handlePacket(packet, type, user);
        user.getTransactionHelper().handlePacket(packet, type, user);
        user.getBrandProcessor().handlePacket(packet, type, user);
        user.getTeleportProcessor().handlePacket(type, packet, user);
        user.getCollisionProcessor().handlePacket(packet, type);
        user.getGhostBlockProcessor().tick(type, user);
        user.getChunkMotionProcessor().tick(type, user);
        user.getPotionEffectProcessor().updatePotion(user);
        if (user.getMovementProcessor().getTicks() > 20) {
            user.getEventManager().invoke(new PacketEvent(packet, type, timestamp));
            user.getVelocityProcessor().handlePacket(packet, type, user);
        }
    }
}
