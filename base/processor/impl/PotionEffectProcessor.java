package dev.phoenixhaven.customac.base.processor.impl;

import dev.phoenixhaven.customac.base.processor.api.Processor;
import dev.phoenixhaven.customac.base.processor.api.ProcessorInfo;
import dev.phoenixhaven.customac.base.user.User;
import lombok.Getter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Getter
@ProcessorInfo("PotionEffectProcessor")
public class PotionEffectProcessor extends Processor {
    public float speedPotionEffectLevel,
            jumpPotionMultiplier, slowMultiplier, lastJumpPotionMultiplier, slowFallingMultiplier, levitationMultiplier;

    public boolean hasWitherEffect, hasJumpPotion,
            hasSpeedPotion, hasSlowness, hasSlowFalling, hasLevitation;

    public int speedPotionTicks;
    public int jumpPotionTicks;
    public int witherEffectTicks;
    public int slownessPotionTicks;
    public int slowFallingTicks;
    public int levitationTicks;

    public void updatePotion(User user) {
        boolean jump = false;
        boolean speed = false;
        boolean wither = false;
        boolean slowness = false;
        boolean slowfalling = false;
        boolean levitation = false;

        int speedAmplifier = 0;
        int jumpAmplifier = 0;
        int slowAmplifier = 0;
        int slowfallingAmplifier = 0;
        int levitationAmplifier = 0;

        for (PotionEffect potionEffect : user.getPlayer().getActivePotionEffects()) {

            if (potionEffect == null) continue;

            PotionEffectType type = potionEffect.getType();
            int amplifer = potionEffect.getAmplifier() + 1;

            if (type.equals(PotionEffectType.SPEED)) {
                speedAmplifier = amplifer;
                speed = true;
            }

            if (type.equals(PotionEffectType.JUMP)) {
                jumpAmplifier = amplifer;
                jump = true;
            }

            if (type.equals(PotionEffectType.WITHER)) {
                wither = true;
            }

            if (type.equals(PotionEffectType.SLOW)) {
                slowAmplifier = amplifer;
                slowness = true;
            }

            if (type.equals(PotionEffectType.LEVITATION)) {
                levitationAmplifier = amplifer;
                levitation = true;
            }

            if (type.equals(PotionEffectType.SLOW_FALLING)) {
                slowfallingAmplifier = amplifer;
                slowfalling = true;
            }
        }

        // update booleans

        this.speedPotionEffectLevel = speedAmplifier;
        this.lastJumpPotionMultiplier = this.jumpPotionMultiplier;
        this.jumpPotionMultiplier = jumpAmplifier;
        this.slowMultiplier = slowAmplifier;
        this.slowFallingMultiplier = slowfallingAmplifier;
        this.levitationMultiplier = levitationAmplifier;

        this.hasWitherEffect = wither;
        this.hasJumpPotion = jump;
        this.hasSpeedPotion = speed;
        this.hasSlowness = slowness;
        this.hasSlowFalling = slowfalling;
        this.hasLevitation = levitation;

        // update ticks

        if (this.hasSpeedPotion) {
            this.speedPotionTicks += (this.speedPotionTicks < 20 ? 1 : 0);
        } else {
            this.speedPotionTicks -= (this.speedPotionTicks > 0 ? 1 : 0);
        }

        if (this.hasJumpPotion) {
            this.jumpPotionTicks += (this.jumpPotionTicks < 20 ? 1 : 0);
        } else {
            this.jumpPotionTicks -= (this.jumpPotionTicks > 0 ? 1 : 0);
        }

        if (this.hasWitherEffect) {
            this.witherEffectTicks += (this.witherEffectTicks < 20 ? 1 : 0);
        } else {
            this.witherEffectTicks -= (this.witherEffectTicks > 0 ? 1 : 0);
        }

        if (this.hasSlowness) {
            this.slownessPotionTicks += (this.slownessPotionTicks < 20 ? 1 : 0);
        } else {
            this.slownessPotionTicks -= (this.slownessPotionTicks > 0 ? 1 : 0);
        }

        if (this.hasSlowFalling) {
            this.slowFallingTicks += (this.slowFallingTicks < 20 ? 1 : 0);
        } else {
            this.slowFallingTicks -= (this.slowFallingTicks > 0 ? 1 : 0);
        }

        if (this.hasLevitation) {
            this.levitationTicks += (this.levitationTicks < 40 ? 1 : 0);
        } else {
            this.levitationTicks -= (this.levitationTicks > 0 ? 1 : 0);
        }
    }
}
