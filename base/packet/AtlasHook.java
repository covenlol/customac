package dev.phoenixhaven.customac.base.packet;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.listener.functions.PacketListener;
import dev.phoenixhaven.customac.CustomAC;
import dev.phoenixhaven.customac.base.user.User;
import org.bukkit.event.EventPriority;

public class AtlasHook {
    PacketListener listener = Atlas.getInstance().getPacketProcessor().process(CustomAC.getInstance(),
            EventPriority.NORMAL, info -> {
                User user = CustomAC.getInstance().getUserManager().getUser(info.getPlayer());

                if (user == null || user.getPacketProcessor() == null ||
                        info.getPacket() == null || info.getType() == null) return;

                user.getPacketProcessor().handlePacket(info.getPacket(), info.getType(), info.getTimestamp(), user);
            });
}
