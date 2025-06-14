package dev.phoenixhaven.customac.utils.player;

import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.math.MathUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;

import java.util.NoSuchElementException;

@UtilityClass
public class ExemptUtils {
    public boolean isNearRideableEntity(User user) {
        return isNearRideableEntity(user, 4);
    }

    public boolean isNearRideableEntity(User user, double distance) {
        try {
            return MathUtils.getEntitiesWithinRadius(user.getPlayer().getLocation(), distance).stream().anyMatch(entity -> entity.getType() == EntityType.BOAT) ||
                    MathUtils.getEntitiesWithinRadius(user.getPlayer().getLocation(), distance).stream().anyMatch(entity -> entity.getType() == EntityType.MINECART) ||
                    MathUtils.getEntitiesWithinRadius(user.getPlayer().getLocation(), distance).stream().anyMatch(entity -> entity.getType() == EntityType.HORSE);
        } catch (IndexOutOfBoundsException ignored) {
            return true;
        } catch (NoSuchElementException ignored) {
            return false;
        }
    }

    public boolean canFly(User user) {
        return user.getPlayer().getGameMode() == GameMode.CREATIVE || user.getPlayer().getGameMode() == GameMode.SPECTATOR ||
                user.getPlayer().isFlying() || user.getPlayer().isRiptiding();
    }
}
