package dev.phoenixhaven.customac.utils.player;

import dev.phoenixhaven.customac.base.user.User;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;

@UtilityClass
public class PlayerUtils {
    public boolean isElytraEquipped(User user) {
        return user.getPlayer().getInventory().getChestplate() != null && user.getPlayer().getInventory().getChestplate().getType() == Material.ELYTRA;
    }
}
