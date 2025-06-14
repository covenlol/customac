package dev.phoenixhaven.customac.impl.check.movement.invalid;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;
import dev.phoenixhaven.customac.utils.player.GroundUtils;

@CheckInfo(name = "Invalid", type = "B", checkType = CheckType.MOVEMENT)
public class InvalidB extends Check {
    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_POSITION) {

            if (!shouldExempt(getUser())) {
                if (getUser().getMovementProcessor().isGround() && getUser().getMovementProcessor().getTo().getY() % 0.015625 != 0) {
                    fail("Ground is true while not on client ground", "offset= " + getUser().getMovementProcessor().getTo().getY() % 0.015625);
                }
            }

            if (!shouldExempt2(getUser())) {
                if (!getUser().getMovementProcessor().isGround() && getUser().getCollisionProcessor().isServerGround() &&
                        getUser().getMovementProcessor().getDeltaY() == 0) {
                    fail("NoGround");
                }
            }
        }
    }

    private boolean shouldExempt(User user) {
        return GroundUtils.isNearGround(user, 0.5) || ExemptUtils.isNearRideableEntity(getUser(), 2);
    }

    private boolean shouldExempt2(User user) {
        return user.getTeleportProcessor().getTicksSinceTeleport() <= 2 || user.getTeleportProcessor().getTicksSinceTeleport() <= 8 ||
                user.getMovementProcessor().getAirTicks() <= 6 || user.getCollisionProcessor().checkAnvilNear(user) ||
                user.getCollisionProcessor().isInWeb() || user.getVelocityProcessor().getTicksSinceVelocity() <= 10 || user.getCollisionProcessor().isInLiquid() ||
                ExemptUtils.canFly(getUser()) || user.getCollisionProcessor().isOnClimable() || user.getCollisionProcessor().getSlimeTicks() > 0;
    }
}
