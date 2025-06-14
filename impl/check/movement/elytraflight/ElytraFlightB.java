package dev.phoenixhaven.customac.impl.check.movement.elytraflight;

import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.base.user.User;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

@CheckInfo(name = "Elytra Flight", type = "B", checkType = CheckType.MOVEMENT)
public class ElytraFlightB extends Check {
    private int lastBoostTicks;
    private double limit = 3.95;

    public void handleUseItem(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getItem().getType() == Material.FIREWORK_ROCKET) {
                lastBoostTicks = 0;
            }
        }
    }

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.isMovement() || packetEvent.isRotation()) {
            if (getUser().getPlayer().isRiptiding()) {
                lastBoostTicks = 0;
            }
            if (getUser().getPlayer().isGliding()) {
                if (getUser().getMovementProcessor().getDeltaXZ() >= 0.325 &&
                        getUser().getMovementProcessor().getTo().getY() >= getUser().getMovementProcessor().getFrom().getY()) {
                    if (getUser().getMovementProcessor().getDeltaXZ() >= getUser().getMovementProcessor().getLastDeltaXZ()
                     && !shouldExempt(getUser())) {
                        fail("Speeding up while going upwards", String.valueOf(getUser().getMovementProcessor().getDeltaXZ() - getUser().getMovementProcessor().getLastDeltaXZ()),
                                String.valueOf(getUser().getMovementProcessor().getDeltaY()), String.valueOf(lastBoostTicks));
                    }
                }
                if (getUser().getMovementProcessor().getRiptideTicks() > 0) {
                    limit += 2.9;
                }
                if (Math.abs(getUser().getMovementProcessor().getDeltaY()) > limit) {
                    fail("Too high y movement", "offset: " + (getUser().getMovementProcessor().getDeltaY() - limit));
                }
            }
            lastBoostTicks++;
        }
    }

    public boolean shouldExempt(User user) {
        return lastBoostTicks < 3 || user.getMovementProcessor().getAirTicks() <= 5 || user.getPlayer().getGameMode() == GameMode.CREATIVE ||
                user.getPlayer().getGameMode() == GameMode.SPECTATOR || user.getMovementProcessor().getElytraTicks() <= 10;
    }
}
