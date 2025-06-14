package dev.phoenixhaven.customac.base.processor.impl;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInTransactionPacket;
import dev.phoenixhaven.customac.base.processor.api.Processor;
import dev.phoenixhaven.customac.base.processor.api.ProcessorInfo;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.impl.check.player.timer.TimerA;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@ProcessorInfo("TransactionProcessor")
public class TransactionProcessor extends Processor {
    private final Map<Short, Long> transactionMap = new ConcurrentHashMap<>();

    private long ping;
    private int pingTicks;
    private int transactionKick;

    public void handlePacket(Object packet, String type, User user) {
        if (Packet.Client.TRANSACTION.equals(type)) {
            WrappedInTransactionPacket transactionPacket = new WrappedInTransactionPacket(packet, user.getPlayer());

            if (!transactionMap.isEmpty()) {
                if (ping > 2000 && user.getMovementProcessor().getTicks() >= 200) {
                    user.runKickSync();
                }

                if ((Short) this.transactionMap.keySet().toArray()[0] != transactionPacket.getAction() && user.getVelocityProcessor().getTicksSinceVelocity() != 0) {
                    if (user.getPlayer() != null) {
                        transactionKick++;
                        if (transactionKick >= 15) {
                            //((CraftPlayer) user.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutKickDisconnect(new ChatComponentText("Timed out")));
                        }
                    }
                } else {
                    transactionKick = 0;
                }
            }

            if (this.transactionMap.containsKey(transactionPacket.getAction())) {
                this.ping = System.currentTimeMillis() - this.transactionMap.remove(transactionPacket.getAction());
                this.pingTicks = (int) ((this.ping / 50) + 1);
                TimerA timerA = user.getCheckManager().getCheck(TimerA.class);
                timerA.onTransaction();
            }
        }
    }
}
