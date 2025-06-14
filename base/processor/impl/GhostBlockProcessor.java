package dev.phoenixhaven.customac.base.processor.impl;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import dev.phoenixhaven.customac.CustomAC;
import dev.phoenixhaven.customac.base.processor.api.Processor;
import dev.phoenixhaven.customac.base.processor.api.ProcessorInfo;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.block.BlockUtil;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;
import dev.phoenixhaven.customac.utils.player.ExemptUtils;
import dev.phoenixhaven.customac.utils.player.GroundUtils;
import lombok.Getter;
import org.bukkit.Location;

@ProcessorInfo("GhostBlockProcessor")
public class GhostBlockProcessor extends Processor {
    @Getter
    private Location groundLocation;
    @Getter
    private int ticksSinceLastPlace;
    private double threshold;

    public void tick(String type, User user) {
        if (PacketUtils.isMovement(type)) {
            ticksSinceLastPlace++;
            boolean serverGround = user.getCollisionProcessor().isServerGround();
            if (serverGround) {
                this.groundLocation = user.getPlayer().getLocation();
            }

            if (!user.getCollisionProcessor().isCollideHorizontal()) {
                if (user.getMovementProcessor().getDeltaY() == 0.20000004768371582 && user.getMovementProcessor().getAirTicks() == 1 &&
                        (!user.getCollisionProcessor().isBlockAbove())) {
                    if (CustomAC.getInstance().isTestMode()) {
                        user.getPlayer().sendMessage("§7[§cDEBUG§7] Flagged ghost block processor");
                    } else {
                        CustomAC.getInstance().sendStaffMessage(user, "Ghost Block Processor");
                    }

                    Location location = user.getPlayer().getLocation().add(0, 2, 0);
                    user.getPlayer().sendBlockChange(location, BlockUtil.getBlock(location).getBlockData());
                    if (threshold++ >= 2) {
                        user.runTeleportSync(groundLocation);
                    }
                } else {
                    threshold -= 0.25;
                }
            }

            if (!this.shouldExempt(user) && !serverGround && user.getMovementProcessor().isGround()) {
                if (!ExemptUtils.isNearRideableEntity(user, 0.5)) {
                    if (CustomAC.getInstance().isTestMode()) {
                        user.getPlayer().sendMessage("§7[§cDEBUG§7] Flagged ghost block processor");
                    } else {
                        CustomAC.getInstance().sendStaffMessage(user, "Ghost Block Processor");
                    }

                    if (ticksSinceLastPlace > 1) {
                        Location location = user.getPlayer().getLocation().subtract(0, 1, 0);
                        user.getPlayer().sendBlockChange(location, BlockUtil.getBlock(location).getBlockData());
                    }

                    double distance = getTicksSinceLastPlace() <= 20 ? 1.5 : 0.01;
                    if (!GroundUtils.isNearGround(user, distance)) {
                        if (this.groundLocation == null) {
                            if (user.getMovementProcessor().getTicks() > 20) {
                                user.runKickSync();
                            }
                        } else {
                            if (GroundUtils.getSolidBlockUnder(user).getY() == 0) {
                                user.runTeleportSync(user.getPlayer().getLocation().subtract(0, user.getPlayer().getLocation().getY() >= 0 ? user.getPlayer().getLocation().getY() - 1 : 1, 0));
                            } else {
                                user.runTeleportSync(groundLocation);
                            }
                        }
                    }
                }
            }
        }
        if (type.equalsIgnoreCase(Packet.Client.BLOCK_PLACE)) {
            ticksSinceLastPlace = 0;
        }
    }

    private boolean shouldExempt(User user) {
        return user.getCollisionProcessor().getLillyPadTicks() > 0 || user.getMovementProcessor().getTicks() <= 8 ||
                user.getCollisionProcessor().getCarpetTicks() > 0 || user.getCollisionProcessor().getPistionTicks() > 0 ||
                user.getCollisionProcessor().getBlockBelowTimer().hasNotPassed() || user.getCollisionProcessor().isOnClimable() ||
                ExemptUtils.isNearRideableEntity(user, 0.5);
    }
}
