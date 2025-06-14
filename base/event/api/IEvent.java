package dev.phoenixhaven.customac.base.event.api;

import dev.phoenixhaven.customac.base.event.impl.PacketEvent;

public interface IEvent {
    void processPacket(PacketEvent event);
}
