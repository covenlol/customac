package dev.phoenixhaven.customac.impl.check.player.inventory;

import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInWindowClickPacket;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutCloseWindowPacket;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;

@CheckInfo(name = "Inventory", type = "A", checkType = CheckType.PLAYER)
public class InventoryA extends Check {
    private boolean isOpen;
    private int lastWindowID;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_WINDOW_CLICK) {
            WrappedInWindowClickPacket windowClickPacket = new WrappedInWindowClickPacket(packetEvent.getPacket(), getUser().getPlayer());
            isOpen = true;
            lastWindowID = windowClickPacket.getId();
        }
        if (packetEvent.isMovement() || packetEvent.isRotation()) {
            if (!shouldExempt(getUser())) {
                if (increaseBuffer(1) >= 2) {
                    if ((getUser().getMovementProcessor().getDeltaYaw() > 0 || getUser().getMovementProcessor().getDeltaPitch() > 0) && isOpen) {
                        fail("Rotating while inside inventory", String.valueOf(getUser().getMovementProcessor().getDeltaYaw()), String.valueOf(getUser().getMovementProcessor().getDeltaPitch()));
                        TinyProtocolHandler.sendPacket(getUser().getPlayer(), new WrappedOutCloseWindowPacket(lastWindowID));
                        isOpen = false;
                    }
                    if (getUser().getMovementProcessor().getDeltaXZ() - getUser().getMovementProcessor().getLastDeltaXZ() * 0.91f > 1E-8 && isOpen && getUser().getMovementProcessor().getDeltaXZ() > 0.041f && getUser().getMovementProcessor().getLastDeltaXZ() < 0.041f) {
                        fail("Moving while inside inventory", String.valueOf(getUser().getMovementProcessor().getDeltaXZ()), String.valueOf(getUser().getMovementProcessor().getLastDeltaXZ() * 0.91f));
                        TinyProtocolHandler.sendPacket(getUser().getPlayer(), new WrappedOutCloseWindowPacket(lastWindowID));
                        isOpen = false;
                    }
                }
            }
        }
        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_CLOSE_WINDOW) {
            isOpen = false;
            decreaseBuffer(1);
        }
    }

    private boolean shouldExempt(User user) {
        return user.getTeleportProcessor().getTicksSinceTeleport() <= 3 || user.getVelocityProcessor().getTicksSinceVelocity() <= 10 ||
                user.getPlayer().isInsideVehicle();
    }
}
