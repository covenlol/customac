package dev.phoenixhaven.customac.base.listener;

import dev.phoenixhaven.customac.CustomAC;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.impl.check.movement.NoSlowA;
import dev.phoenixhaven.customac.impl.check.movement.elytraflight.ElytraFlightB;
import dev.phoenixhaven.customac.impl.check.player.scaffold.ScaffoldB;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        CustomAC.getInstance().getUserManager().addUser(event.getPlayer());
        if (event.getPlayer().hasPermission("customac.alerts")) {
            event.getPlayer().sendMessage("§9Run /alerts to see alerts");
        }
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        CustomAC.getInstance().getUserManager().removeUser(event.getPlayer());
    }

    @EventHandler
    void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        User user = CustomAC.getInstance().getUserManager().getUser(event.getPlayer());
        NoSlowA noSlowA = user.getCheckManager().getCheck(NoSlowA.class);
        noSlowA.handleConsume();
    }

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            User user = CustomAC.getInstance().getUserManager().getUser(event.getPlayer());
            ScaffoldB scaffoldB = user.getCheckManager().getCheck(ScaffoldB.class);
            scaffoldB.handleUseItem(event);
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            User user = CustomAC.getInstance().getUserManager().getUser(event.getPlayer());
            NoSlowA noSlowA = user.getCheckManager().getCheck(NoSlowA.class);
            noSlowA.handleUseItem(event);
            ElytraFlightB elytraFlightB = user.getCheckManager().getCheck(ElytraFlightB.class);
            elytraFlightB.handleUseItem(event);
        }
    }
}
