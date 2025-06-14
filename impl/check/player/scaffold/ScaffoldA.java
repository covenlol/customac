package dev.phoenixhaven.customac.impl.check.player.scaffold;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumDirection;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import net.minecraft.server.v1_16_R3.PacketPlayInUseItem;

@CheckInfo(name = "Scaffold", type = "A", checkType = CheckType.PLAYER)
public class ScaffoldA extends Check {
    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacket() instanceof PacketPlayInUseItem) {
            WrappedInBlockPlacePacket placePacket = new WrappedInBlockPlacePacket(packetEvent.getPacket(), getUser().getPlayer());

            if (!shouldExempt(getUser())) {
                if (placePacket.getBlockPosition().getY() < getUser().getMovementProcessor().getTo().getY() && placePacket.getFace() == WrappedEnumDirection.DOWN) {
                    fail("Downwards scaffold", String.valueOf(placePacket.getBlockPosition().getY()), String.valueOf(getUser().getMovementProcessor().getTo().getY()));
                }
            }

            if (placePacket.getBlockPosition().getY() == getUser().getMovementProcessor().getTo().getY() - 1 &&
                    placePacket.getBlockPosition().getZ() == Math.round(getUser().getMovementProcessor().getTo().getZ()) &&
                    placePacket.getBlockPosition().getX() == Math.round(getUser().getMovementProcessor().getTo().getX()) &&
                    !(placePacket.getFace() == WrappedEnumDirection.UP || placePacket.getFace() == WrappedEnumDirection.DOWN)) {
                if (getUser().getMovementProcessor().getDeltaXZ() > (getUser().getMovementProcessor().getGroundSpeed(getUser()) * 0.3)) {
                    if (getUser().getMovementProcessor().getTo().getPitch() < 76) {
                        fail("too low pitch", String.valueOf(getUser().getMovementProcessor().getTo().getPitch()));
                    }
                    if (getUser().getMovementProcessor().getTo().getPitch() > 83.5) {
                        fail("too high pitch", String.valueOf(getUser().getMovementProcessor().getTo().getPitch()));
                    }
                }
            }
        }
    }

    public boolean shouldExempt(User user) {
        return user.getCollisionProcessor().getBlockAboveTicks() > 0;
    }
}
