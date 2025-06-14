package dev.phoenixhaven.customac;

import dev.phoenixhaven.customac.base.command.CommandManager;
import dev.phoenixhaven.customac.base.config.ConfigManager;
import dev.phoenixhaven.customac.base.listener.BukkitListener;
import dev.phoenixhaven.customac.base.packet.AtlasHook;
import dev.phoenixhaven.customac.base.packet.ConnectionHandler;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.base.user.UserManager;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CustomAC extends JavaPlugin {
    @Getter
    private static CustomAC instance;
    @Getter
    private final boolean testMode = false;

    private final UserManager userManager = new UserManager();
    private final CommandManager commandManager = new CommandManager();
    private ConnectionHandler connectionHandler;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        this.connectionHandler = new ConnectionHandler();
        this.configManager = new ConfigManager();
        getCommand("alerts").setExecutor(commandManager);
        getCommand("debug").setExecutor(commandManager);

        Bukkit.getServer().getPluginManager().registerEvents(new BukkitListener(), this);
        Bukkit.getOnlinePlayers().forEach(this.userManager::addUser);
        new AtlasHook();
    }

    public void sendStaffMessage(User user, String text) {
        String message = String.format(
                "§c§l%s §8» §f%s §7failed §f%s",
                "CustomAC",
                user.getPlayer().getName(),
                text
        );
        TextComponent component = new TextComponent(message);

        CustomAC.getInstance().getUserManager().getUserMap().values().stream()
                .filter(user2 -> user2.getPlayer().hasPermission("customac.alerts") && user2.isAlertsEnabled())
                .forEach(user2 -> user2.getPlayer().spigot().sendMessage(component));
    }

    public void sendBrandAlert(User user, String brand) {
        String message = String.format(
                "§c§l%s §8» §f%s §7joined with brand §f%s",
                "CustomAC",
                user.getPlayer().getName(),
                brand
        );
        TextComponent component = new TextComponent(message);

        CustomAC.getInstance().getUserManager().getUserMap().values().stream()
                .filter(user2 -> user2.getPlayer().hasPermission("customac.alerts") && user2.isAlertsEnabled())
                .forEach(user2 -> user2.getPlayer().spigot().sendMessage(component));
    }
}
