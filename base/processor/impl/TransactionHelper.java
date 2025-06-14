package dev.phoenixhaven.customac.base.processor.impl;

import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInTransactionPacket;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutTransaction;
import dev.phoenixhaven.customac.base.processor.api.Processor;
import dev.phoenixhaven.customac.base.processor.api.ProcessorInfo;
import dev.phoenixhaven.customac.base.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@ProcessorInfo("TransactionHelper")
public class TransactionHelper extends Processor {
    private final Map<Short, Runnable> consumerMap = new HashMap<>();

    private short action = (short) ThreadLocalRandom.current().nextInt(-32676, 32676);

    @Setter
    private long lastPacket;

    public void runTransaction(Runnable runnable, User user) {
        this.action = (short) ThreadLocalRandom.current().nextInt(-32676, 32676);

        TinyProtocolHandler.sendPacket(user.getPlayer(), new WrappedOutTransaction(0, this.action, false));

        this.consumerMap.put(this.action, runnable);
    }

    public void handlePacket(Object packet, String type, User user) {
        if (type.equalsIgnoreCase(Packet.Client.TRANSACTION)) {
            WrappedInTransactionPacket transactionPacket
                    = new WrappedInTransactionPacket(packet, user.getPlayer());

            Optional.ofNullable(this.consumerMap.remove(transactionPacket.getAction())).ifPresent(Runnable::run);
        }
    }
}
