package dev.phoenixhaven.customac.base.command;

import dev.phoenixhaven.customac.CustomAC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            switch (command.getName().toLowerCase()) {
                case "alerts": {
                    if (player.getPlayer().hasPermission("customac.alerts")) {
                        if (CustomAC.getInstance().getUserManager().getUser(player.getPlayer()).isAlertsEnabled()) {
                            player.sendMessage("§cDisabled Alerts");
                            CustomAC.getInstance().getUserManager().getUser(player.getPlayer()).setAlertsEnabled(false);
                        } else {
                            player.sendMessage("§9Enabled Alerts");
                            CustomAC.getInstance().getUserManager().getUser(player.getPlayer()).setAlertsEnabled(true);
                        }
                    }
                    break;
                }

                case "debug": {
                    if (player.getPlayer().hasPermission("customac.alerts")) {
                        if (CustomAC.getInstance().getUserManager().getUser(player.getPlayer()).isDebugAlerts()) {
                            player.sendMessage("§cDisabled Debug");
                            CustomAC.getInstance().getUserManager().getUser(player.getPlayer()).setDebugAlerts(false);
                        } else {
                            player.sendMessage("§9Enabled Debug");
                            CustomAC.getInstance().getUserManager().getUser(player.getPlayer()).setDebugAlerts(true);
                        }
                    }
                    break;
                }
            }
        }
        return true;
    }
}
