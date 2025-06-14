package dev.phoenixhaven.customac.base.check.api;

import dev.phoenixhaven.customac.CustomAC;
import dev.phoenixhaven.customac.base.event.api.Event;
import dev.phoenixhaven.customac.base.user.User;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@Getter
public class Check extends Event {
    @Setter
    private User user;
    private int vl = 0;

    private final CheckInfo checkInfo;

    @Setter
    private double buffer;

    public Check() {
        this.checkInfo = this.getClass().getAnnotation(CheckInfo.class);
    }

    public void fail(String... info) {
        String message = String.format(
                "§c§l%s §8» §f%s §7failed §f%s %s §cx%s",
                "CustomAC",
                this.user.getPlayer().getName(),
                this.checkInfo.name(),
                this.checkInfo.type(),
                this.vl++
        );

        TextComponent component = new TextComponent(message);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(String.join("\n", info)).create()));

        if (CustomAC.getInstance().isTestMode()) {
            if (component.toString().contains(user.getPlayer().getName()) && !user.isDebugAlerts()) {
                CustomAC.getInstance().getUserManager().getUser(user.getUuid()).getPlayer().spigot().sendMessage(component);
            }
            CustomAC.getInstance().getUserManager().getUserMap().values().stream()
                    .filter(user -> user.getPlayer().hasPermission("customac.alerts") && (user.getPlayer().getName().equals("PhoenixHaven") ||
                            user.getPlayer().getName().equals("NathanTalksTech")) && user.isDebugAlerts())
                    .forEach(user -> user.getPlayer().spigot().sendMessage(component));
        } else {
            CustomAC.getInstance().getUserManager().getUserMap().values().stream()
                    .filter(user -> user.getPlayer().hasPermission("customac.alerts") && user.isAlertsEnabled())
                    .forEach(user -> user.getPlayer().spigot().sendMessage(component));
        }
    }

    public void debug(Object message) {
        if (CustomAC.getInstance().getUserManager().getUser(getUser().getPlayer()).isDebugAlerts()) {
            this.getUser().getPlayer().sendMessage(String.format("§7[§cDEBUG§7] §f%s", message.toString()));
        }
    }

    public double increaseBuffer(double amount) {
        return this.buffer += amount;
    }

    public void decreaseBuffer(double amount) {
        this.buffer = Math.max(this.buffer - amount, 0);
    }
}
