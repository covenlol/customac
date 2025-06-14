package dev.phoenixhaven.customac.base.packet;

import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutTransaction;
import dev.phoenixhaven.customac.CustomAC;
import org.bukkit.Bukkit;

import java.util.concurrent.ThreadLocalRandom;

public class ConnectionHandler {
    public ConnectionHandler() {
        Bukkit.getServer().getScheduler().runTaskTimer(CustomAC.getInstance(), this::sendTransaction, 0L, 0L);
    }

    private short action;
    private short lastAction;

    void sendTransaction() {
        this.action = (short) ThreadLocalRandom.current().nextInt(-32676, 32676);

        if (this.lastAction == this.action) {
            this.action--;
        }
        this.lastAction = this.action;

        WrappedOutTransaction transaction = new WrappedOutTransaction(0, this.action, false);

        long current = System.currentTimeMillis();

        CustomAC.getInstance().getUserManager().getUserMap().values().forEach(user -> {
            user.getTransactionProcessor().getTransactionMap().put(this.action, current);

            TinyProtocolHandler.sendPacket(user.getPlayer(), transaction);
        });
    }
}
