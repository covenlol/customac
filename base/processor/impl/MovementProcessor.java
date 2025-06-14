package dev.phoenixhaven.customac.base.processor.impl;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.phoenixhaven.customac.base.processor.api.Processor;
import dev.phoenixhaven.customac.base.processor.api.ProcessorInfo;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;
import dev.phoenixhaven.customac.utils.player.CustomLocation;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;

@SuppressWarnings("ALL")
@Getter
@ProcessorInfo("MovementProcessor")
public class MovementProcessor extends Processor {
    private int ticks;

    private CustomLocation lastLastFrom;
    private CustomLocation lastFrom;
    private CustomLocation from;

    private final CustomLocation to = new CustomLocation(0, 0, 0, 0, 0, true);

    private int groundTicks;
    private int airTicks;
    private int lastAirTicks;

    private double lastDeltaY;
    private double deltaY;

    private boolean lastLastGround;
    private boolean lastGround;
    private boolean ground;

    private double lastDeltaXZ;
    private double deltaXZ;
    private double deltaX;
    private double deltaZ;
    private double lastDeltaX;
    private double lastDeltaZ;

    private double lastDeltaYaw;
    private double deltaYaw;
    private double lastDeltaPitch;
    private double deltaPitch;
    private double deltaYP;
    private double lastDeltaYP;

    private int diagonalTicks;

    private int flyingTicks;
    private int elytraTicks;
    private int riptideTicks;
    private short lastDura;
    private boolean elytra;

    public void handlePacket(Object packet, String type, User user) {
        if (PacketUtils.isMovement(type) || PacketUtils.isRotation(type)) {
            WrappedInFlyingPacket wrappedInFlyingPacket = new WrappedInFlyingPacket(packet, user.getPlayer());

            if (ExemptUtils.canFly(user)) {
                this.flyingTicks += (this.flyingTicks < 30 ? 1 : 0);
            } else {
                this.flyingTicks -= (this.flyingTicks > 0 ? 1 : 0);
            }

            if (isGliding(user.getPlayer())) {
                this.elytraTicks = 20;
                elytra = true;
            } else {
                this.elytraTicks -= (this.elytraTicks > 0 ? 1 : 0);
                elytra = false;
            }

            if (user.getPlayer().isRiptiding()) {
                this.riptideTicks += (this.riptideTicks < 10 ? 1 : 0);
            } else {
                this.riptideTicks -= (this.riptideTicks > 0 ? 1 : 0);
            }

            this.lastLastFrom = this.lastFrom != null ? this.lastFrom.clone() : null;
            this.lastFrom = this.from != null ? this.from.clone() : null;
            this.from = this.to.clone();

            this.lastLastGround = this.lastGround;
            this.lastGround = this.ground;
            this.ground = wrappedInFlyingPacket.isGround();

            if (this.ground) {
                this.lastAirTicks = airTicks;
                this.airTicks = 0;
                this.groundTicks++;
            } else {
                this.groundTicks = 0;
                this.lastAirTicks = airTicks;
                this.airTicks++;
            }

            if (wrappedInFlyingPacket.isLook()) {
                this.to.setYaw(wrappedInFlyingPacket.getYaw());
                this.to.setPitch(wrappedInFlyingPacket.getPitch());
            }

            if (wrappedInFlyingPacket.isPos()) {
                this.to.setX(wrappedInFlyingPacket.getX());
                this.to.setY(wrappedInFlyingPacket.getY());
                this.to.setZ(wrappedInFlyingPacket.getZ());
                this.to.setGround(this.ground);
            }

            this.lastDeltaY = this.deltaY;
            this.deltaY = this.to.getY() - this.from.getY();

            double x = Math.abs(Math.abs(this.to.getX()) - Math.abs(this.from.getX()));
            double z = Math.abs(Math.abs(this.to.getZ()) - Math.abs(this.from.getZ()));
            double yaw = Math.abs(this.to.getYaw() - this.from.getYaw());
            double pitch = Math.abs(this.to.getPitch() - this.from.getPitch());

            this.lastDeltaX = deltaX;
            this.lastDeltaZ = deltaZ;
            this.lastDeltaYaw = deltaYaw;
            this.lastDeltaPitch = deltaPitch;
            this.lastDeltaYP = MathUtils.hypot(lastDeltaYaw, lastDeltaPitch);
            this.deltaYP = MathUtils.hypot(deltaYaw, deltaPitch);
            this.deltaYaw = yaw;
            this.deltaPitch = pitch;
            this.deltaX = x;
            this.deltaZ = z;
            this.lastDeltaXZ = this.deltaXZ;
            this.deltaXZ = MathUtils.hypot(x, z);

            this.updateDiagonal();

            this.ticks++;
        }
    }

    private void updateDiagonal() {
        double x = this.to.getX();
        double z = this.to.getZ();

        double accelerationX = Math.abs(this.from.getX() - x);
        double accelerationZ = Math.abs(this.from.getZ() - z);

        if (accelerationX > .09 && accelerationZ > .09) {
            this.diagonalTicks++;
        } else {
            this.diagonalTicks = 0;
        }
    }

    public double getGroundSpeed(User user) {
        double baseSpeed = 0.30203f + user.getPlayer().getWalkSpeed() - 0.2;
        if (user.getPotionEffectProcessor().speedPotionTicks > 0) {
            double amplifier = user.getPotionEffectProcessor().getSpeedPotionEffectLevel();
            baseSpeed *= (1.0D + 0.2 * (amplifier + 0.5));
        }
        if (user.getPotionEffectProcessor().slownessPotionTicks > 0) {
            double slownessAmplifier = user.getPotionEffectProcessor().getSlowMultiplier();
            baseSpeed *= (1.0D + 0.2 * (-slownessAmplifier + 1));
        }
        return baseSpeed;
    }

    public double getJumpMotion(User user) {
        return 0.42f + (user.getPotionEffectProcessor().getJumpPotionMultiplier() * 0.1);
    }

    public boolean isGliding(LivingEntity entity) {
        return entity.isGliding();
    }

    public boolean isMoving() {
        return Math.abs(this.getDeltaY()) > 0 || this.getDeltaXZ() > 0 || Math.abs(this.getDeltaYaw()) > 0 || Math.abs(this.getDeltaPitch()) > 0;
    }
}
