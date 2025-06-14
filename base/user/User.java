package dev.phoenixhaven.customac.base.user;

import cc.funkemunky.api.utils.RunUtils;
import dev.phoenixhaven.customac.base.check.CheckManager;
import dev.phoenixhaven.customac.base.event.EventManager;
import dev.phoenixhaven.customac.base.processor.impl.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class User {
    private final Player player;
    private final UUID uuid;
    @Setter
    private boolean alertsEnabled;
    @Setter
    private boolean debugAlerts;
    @Setter
    private String clientBrand;

    private final PacketProcessor packetProcessor = new PacketProcessor();
    private final MovementProcessor movementProcessor = new MovementProcessor();
    private final CollisionProcessor collisionProcessor;
    private final TransactionHelper transactionHelper = new TransactionHelper();
    private final VelocityProcessor velocityProcessor = new VelocityProcessor();
    private final TeleportProcessor teleportProcessor = new TeleportProcessor();
    private final TransactionProcessor transactionProcessor = new TransactionProcessor();
    private final GhostBlockProcessor ghostBlockProcessor = new GhostBlockProcessor();
    private final ChunkMotionProcessor chunkMotionProcessor = new ChunkMotionProcessor();
    private final PotionEffectProcessor potionEffectProcessor = new PotionEffectProcessor();
    private final BrandProcessor brandProcessor = new BrandProcessor();

    private final EventManager eventManager = new EventManager();
    private final CheckManager checkManager = new CheckManager();

    public User(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();

        this.collisionProcessor = new CollisionProcessor(this);

        this.checkManager.registerCheck(this);
    }

    public void runKickSync() {
        RunUtils.task(() -> this.getPlayer().kickPlayer("Disconnected"));
    }

    public void runKickSync(String message) {
        RunUtils.task(() -> this.getPlayer().kickPlayer(message));
    }

    public void runTeleportSync(Location location) {
        RunUtils.task(() -> this.getPlayer().teleport(location));
    }
}
