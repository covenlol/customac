package dev.phoenixhaven.customac.utils.player;

import dev.phoenixhaven.customac.base.user.User;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;

@UtilityClass
public class GroundUtils {
    public boolean isNearGround(User user) {
        return isNearGround(user, 1);
    }

    public boolean isNearGround(User user, double distance) {
        Location location = user.getMovementProcessor().getTo().toLocation(user.getPlayer().getWorld());

        for (double x = -distance; x <= distance; x += distance / 2) {
            for (double z = -distance; z <= distance; z += distance / 2) {
                for (double y = -distance; y <= distance; y += distance / 2) {
                    Location currentLocation = location.clone().add(x, -y, z);

                    if (currentLocation.getBlock().getType() != Material.AIR && !currentLocation.getBlock().isLiquid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Location getBlockUnder(User user) {
        for (int offset = 0; offset <= user.getMovementProcessor().getTo().getY(); offset += 1) {
            Location location = user.getMovementProcessor().getTo().toLocation(user.getPlayer().getWorld()).subtract(0, offset, 0);
            if (location.getBlock().getType() != Material.AIR) {
                return new Location(user.getPlayer().getWorld(), location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
            }
        }
        return new Location(user.getPlayer().getWorld(), user.getMovementProcessor().getFrom().getX(), 0, user.getMovementProcessor().getFrom().getZ());
    }

    public Location getSolidBlockUnder(User user) {
        for (int offset = -1; offset < user.getMovementProcessor().getTo().getY(); offset += 1) {
            Location location = user.getMovementProcessor().getTo().toLocation(user.getPlayer().getWorld()).subtract(0, offset, 0);
            if (location.getBlock().getType().isSolid() && location.getBlock().getType() != Material.LADDER) {
                return new Location(user.getPlayer().getWorld(), location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
            }
        }
        return new Location(user.getPlayer().getWorld(), user.getMovementProcessor().getFrom().getX(), 0, user.getMovementProcessor().getFrom().getZ());
    }
}
