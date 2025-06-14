package dev.phoenixhaven.customac.impl.check.player.scaffold;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import dev.phoenixhaven.customac.base.check.api.Check;
import dev.phoenixhaven.customac.base.check.api.CheckInfo;
import dev.phoenixhaven.customac.base.check.api.CheckType;
import dev.phoenixhaven.customac.base.event.impl.PacketEvent;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;

@CheckInfo(name = "Scaffold", type = "B", checkType = CheckType.PLAYER)
public class ScaffoldB extends Check {
    private int placed;
    private double lastPlacedY;

    public void handleUseItem(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if ((getUser().getPlayer().getInventory().getItemInMainHand().getType().isBlock() || getUser().getPlayer().getInventory().getItemInOffHand().getType().isBlock()) &&
                    event.getBlockFace() != BlockFace.UP && event.getBlockFace() != BlockFace.DOWN &&
                    getUser().getMovementProcessor().getDeltaXZ() > getUser().getMovementProcessor().getGroundSpeed(getUser()) * 0.375 &&
                    event.getClickedBlock().getY() < getUser().getMovementProcessor().getTo().getY() && event.getClickedBlock().getY() == lastPlacedY) {
                placed++;
            } else {
                placed -= placed > 0 ? 1 : 0;
            }

            if (placed > 3) {
                fail("Impossible consistency");
                placed = 1;
            }
            lastPlacedY = event.getClickedBlock().getY();
        }
    }

    @Override
    public void processPacket(PacketEvent packetEvent) {
        if (packetEvent.getPacketType() == PacketUtils.Packets.CLIENT_ENTITY_ACTION) {
            WrappedInEntityActionPacket entityAction = new WrappedInEntityActionPacket(packetEvent.getPacket(), getUser().getPlayer());

            if (entityAction.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.START_SNEAKING ||
                    entityAction.getAction() == WrappedInEntityActionPacket.EnumPlayerAction.STOP_SNEAKING) {
                placed -= placed > 0 ? 1 : 0;
            }
        }
    }
}
