package dev.phoenixhaven.customac.base.processor.impl;

import dev.phoenixhaven.customac.CustomAC;
import dev.phoenixhaven.customac.base.processor.api.Processor;
import dev.phoenixhaven.customac.base.processor.api.ProcessorInfo;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;
import dev.phoenixhaven.customac.utils.player.GroundUtils;
import lombok.Getter;
import org.bukkit.Location;

@Getter
@ProcessorInfo("ChunkMotionProcessor")
public class ChunkMotionProcessor extends Processor {
    private double streaks;

    private Location groundLocation;

    public void tick(String type, User user) {
        if (PacketUtils.isMovement(type)) {
            boolean serverGround = user.getCollisionProcessor().isServerGround();
            double deltaY = user.getMovementProcessor().getDeltaY();
            if (serverGround) {
                this.groundLocation = user.getPlayer().getLocation();
            }

            if (deltaY == -0.09800000190734881) {
                if (!this.shouldExempt(user)) {
                    this.streaks++;

                    if (this.streaks >= 3) {
                        if (this.groundLocation != null) {
                            user.runTeleportSync(groundLocation);
                        } else {
                            user.runTeleportSync(GroundUtils.getSolidBlockUnder(user));
                        }
                        if (CustomAC.getInstance().isTestMode()) {
                            user.getPlayer().sendMessage("§7[§cDEBUG§7] Flagged chunk motion processor");
                        } else {
                            CustomAC.getInstance().sendStaffMessage(user, "Chunk Motion Processor");
                        }
                        this.streaks = 0;
                    }
                }
            } else {
                this.streaks = Math.max(0, this.streaks - 0.2);
            }
        }
    }

    boolean shouldExempt(User user) {
        return user.getPlayer().getTicksLived() <= 20;
    }
}
