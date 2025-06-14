package dev.phoenixhaven.customac.impl.check.combat.velocity;

import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutVelocityPacket;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.player.GroundUtils;
import org.bukkit.event.entity.EntityDamageEvent;

@CheckInfo(name = "Velocity", type = "A", checkType = CheckType.COMBAT)
public class VelocityA extends Check {
    private boolean runCheck;
    private double serverVelocityY;
    private double lastVelocityY;

    @Override
    public void processPacket(PacketEvent packetEvent) {
        switch (packetEvent.getPacketType()) {
            case SERVER_VELOCITY: {
                WrappedOutVelocityPacket velocityPacket
                        = new WrappedOutVelocityPacket(packetEvent.getPacket(), this.getUser().getPlayer());

                if (velocityPacket.getId() == this.getUser().getPlayer().getEntityId() && getUser().getMovementProcessor().getAirTicks() == 0) {
                    this.serverVelocityY = velocityPacket.getY();

                    if (serverVelocityY > 0) {
                        this.getUser().getTransactionHelper().runTransaction(() -> this.runCheck = true, this.getUser());
                    }
                }
                break;
            }

            case CLIENT_POSITION_LOOK:
            case CLIENT_POSITION:
            case CLIENT_FLYING: {
                if (!this.runCheck) {
                    return;
                }

                double deltaY = this.getUser().getMovementProcessor().getDeltaY();

                double percentage = (deltaY * 100.0) / this.serverVelocityY;

                if (getUser().getVelocityProcessor().getTicksSinceVelocity() > 4 || GroundUtils.isNearGround(getUser(), 0.1) &&
                        (getUser().getMovementProcessor().getAirTicks() > 4 || getUser().getTeleportProcessor().getTicksSinceTeleport() <= 3)) {
                    this.runCheck = false;
                }

                if (percentage < 99.9999999 && percentage >= 0 &&
                        getUser().getCollisionProcessor().getBlockAboveTimer().passed() && !shouldExempt(getUser())) {
                    this.fail(
                            "deltaY: " + deltaY,
                            "fixedServerY: " + this.serverVelocityY,
                            "percentage: " + percentage,
                            "velocity tick: " + getUser().getVelocityProcessor().getTicksSinceVelocity()
                    );
                }

                this.lastVelocityY = serverVelocityY;
                this.serverVelocityY = (this.serverVelocityY - 0.08) * 0.98f;
                break;
            }
        }
    }

    private boolean shouldExempt(User user) {
        return user.getPlayer().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
                user.getCollisionProcessor().isInLiquid() || user.getCollisionProcessor().isInWeb() ||
                user.getPlayer().isInsideVehicle() || getUser().getMovementProcessor().getDeltaY() == this.lastVelocityY ||
                user.getPlayer().isRiptiding() || user.getPlayer().isGliding();
    }
}
