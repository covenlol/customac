package dev.phoenixhaven.customac.base.user;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class UserManager {
    private final Map<UUID, User> userMap = new HashMap<>();

    public User getUser(UUID uuid) {
        return this.userMap.get(uuid);
    }

    public User getUser(Player player) {
        return this.userMap.get(player.getUniqueId());
    }

    public User getUser(String player) {
        return this.userMap.values().stream().filter(user -> user.getPlayer().getName().equalsIgnoreCase(player)).findFirst().orElse(null);
    }

    public User addUser(Player player) {
        return this.userMap.put(player.getUniqueId(), new User(player));
    }

    public User removeUser(Player player) {
        return this.userMap.get(player.getUniqueId());
    }
}
