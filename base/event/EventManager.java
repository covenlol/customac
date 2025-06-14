package dev.phoenixhaven.customac.base.event;

import dev.phoenixhaven.customac.base.event.api.Event;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EventManager {
    private final List<Event> eventList = new ArrayList<>();

    public void invoke(PacketEvent event) {
        this.eventList.forEach(eventCache -> eventCache.processPacket(event));
    }
}
