package dev.phoenixhaven.customac.base.processor.impl;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.tinyprotocol.packet.types.MathHelper;
import dev.phoenixhaven.customac.base.processor.api.Processor;
import dev.phoenixhaven.customac.base.processor.api.ProcessorInfo;
import dev.phoenixhaven.customac.base.user.User;
import dev.phoenixhaven.customac.utils.block.BlockUtil;
import dev.phoenixhaven.customac.utils.block.BoundingBox;
import dev.phoenixhaven.customac.utils.block.CollideEntry;
import dev.phoenixhaven.customac.utils.java.StreamUtil;
import dev.phoenixhaven.customac.utils.math.EventTimer;
import dev.phoenixhaven.customac.utils.packet.PacketUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.AnvilInventory;

import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
@ProcessorInfo("CollisionProcessor")
public class CollisionProcessor extends Processor {
    private final User user;

    private int groundTicks;
    private int stairTicks;
    private int slabTicks;
    private int liquidTicks;
    private int collideHorizontalTicks;
    private int iceTicks;
    private int blockAboveTicks;
    private int blockBelowTicks;
    private int slimeTicks;
    private int climbableTicks;
    private int scaffoldingTicks;
    private int snowTicks;
    private int lillyPadTicks;
    private int carpetTicks;
    private int webTicks;
    private int halfBlockTicks;
    private int movingTicks;
    private int soulSandTicks;
    private int enderPortalTicks;
    private int pistionTicks;
    private int wallTicks;

    private boolean halfBlock;
    private boolean inLiquid;
    private boolean inWeb;
    private boolean onClimable;
    private boolean blockAbove;
    private boolean onSlime;
    private boolean isOnWall;
    private boolean onHalfBlock;
    private boolean isHoney;

    private boolean collideHorizontal, serverGround;

    private final EventTimer halfBlockTimer;
    private final EventTimer blockAboveTimer;
    private final EventTimer lastSoulsandTimer;
    private final EventTimer blockBelowTimer;

    public CollisionProcessor(User user) {
        this.user = user;
        this.halfBlockTimer = new EventTimer(20, user);
        this.blockAboveTimer = new EventTimer(20, user);
        this.lastSoulsandTimer = new EventTimer(20, user);
        this.blockBelowTimer = new EventTimer(20, user);
    }

    private BoundingBox boundingBox;

    public void handlePacket(Object packet, String type) {
        if (PacketUtils.isMovement(type)) {
            if (user.getMovementProcessor().getTicks() >= 20) {
                WrappedInFlyingPacket flyingPacket = new WrappedInFlyingPacket(packet, user.getPlayer());

                boolean badVector = Math.abs(user.getMovementProcessor().getTo().toVector().length()
                        - user.getMovementProcessor().getFrom().toVector().length()) >= 1;

                this.boundingBox = new BoundingBox((badVector ? user.getMovementProcessor().getTo().toVector()
                        : user.getMovementProcessor().getFrom().toVector()),
                        user.getMovementProcessor().getTo().toVector())
                        .grow(0.3f, 0, 0.3f).add(0, 0, 0, 0, 1.84f, 0);

                Location location = user.getPlayer().getLocation().add(0, 0.05, 0);
                Block block = BlockUtil.getBlock(location);
                if (block.getType().isSolid() && !isOnClimable()) {
                    if (!block.getBoundingBox().equals(new org.bukkit.util.BoundingBox(0, 0, 0, 1, 1, 1))) return;
                    if (user.getMovementProcessor().getDeltaXZ() > 0.2) {
                        user.runTeleportSync(user.getMovementProcessor().getFrom().toLocation(user.getPlayer().getWorld()));
                        user.getPlayer().sendMessage("§7[§cDEBUG§7] Moved too fast inside block: " + BlockUtil.getBlock(user.getPlayer().getLocation().add(0, 0.05, 0)));
                    }
                }

                List<CollideEntry> collideEntries = this.boundingBox.getCollidedBlocks(user);

                BlockResult blockResult = new BlockResult();
                collideEntries.forEach(blockResult::process);

                BoundingBox boundingBox = new BoundingBox((float) flyingPacket.getX() - 0.3F,
                        (float) flyingPacket.getY(), (float) flyingPacket.getZ() - 0.3F,
                        (float) flyingPacket.getX() + 0.3F,
                        (float) flyingPacket.getY() + 1.8F,
                        (float) flyingPacket.getZ() + 0.3F);

                double minX = boundingBox.minX;
                double minZ = boundingBox.minZ;

                double maxX = boundingBox.maxX;
                double maxZ = boundingBox.maxZ;

                boolean velocity = this.user.getVelocityProcessor().getTicksSinceVelocity() <= 20;
                double offset = Math.abs(user.getMovementProcessor().getDeltaY() - 0.20000004768371404);

                if (offset < .212 || velocity) {
                    blockResult.checkBlockAbove(user);
                }
                if (user.getMovementProcessor().getDeltaY() == 0) {
                    blockResult.checkBlockBelow(user);
                }

                if (this.testCollision(minX) || this.testCollision(minZ)
                        || this.testCollision(maxX) || this.testCollision(maxZ)) {
                    blockResult.checkHorizontal(user);
                }

                this.processTicks(blockResult);
            }
        }
    }

    void processTicks(BlockResult blockResult) {
        if (blockResult.isWall()) {
            isOnWall = true;
            this.wallTicks += this.wallTicks < 20 ? 1 : 0;
        } else {
            isOnWall = false;
            this.wallTicks -= this.wallTicks > 0 ? 1 : 0;
        }

        if (blockResult.isPiston()) {
            this.pistionTicks += this.pistionTicks < 20 ? 1 : 0;
        } else {
            this.pistionTicks -= this.pistionTicks > 0 ? 1 : 0;
        }

        if (blockResult.isEnderPortal()) {
            this.enderPortalTicks += this.enderPortalTicks < 20 ? 1 : 0;
        } else {
            this.enderPortalTicks -= this.enderPortalTicks > 0 ? 1 : 0;
        }

        if (blockResult.isSoulSand()) {
            this.lastSoulsandTimer.reset();
            this.soulSandTicks += this.soulSandTicks < 20 ? 1 : 0;
        } else {
            this.soulSandTicks -= this.soulSandTicks > 0 ? 1 : 0;
        }

        if (blockResult.isMovingUp()) {
            this.movingTicks += this.movingTicks < 50 ? 10 : 0;
        } else {
            this.movingTicks -= this.movingTicks > 0 ? 1 : 0;
        }

        if (blockResult.isHalfBlock()) {
            this.halfBlockTimer.reset();
            this.onHalfBlock = true;
            this.halfBlockTicks += this.halfBlockTicks < 20 ? 1 : 0;
        } else {
            this.onHalfBlock = false;
            this.halfBlockTicks -= this.halfBlockTicks > 0 ? 1 : 0;
        }

        this.halfBlock = blockResult.isHalfBlock();

        this.isHoney = blockResult.isHoney();

        if (blockResult.isWeb()) {
            this.webTicks += (this.webTicks < 20 ? 1 : 0);
            this.inWeb = true;
        } else {
            this.webTicks -= (this.webTicks > 0 ? 1 : 0);
            this.inWeb = false;
        }

        if (blockResult.isServerGround()) {
            this.groundTicks += this.groundTicks < 20 ? 1 : 0;
        } else {
            this.groundTicks -= this.groundTicks > 0 ? 1 : 0;
        }

        if (blockResult.isStair()) {
            this.halfBlockTimer.reset();
            this.stairTicks += this.stairTicks < 20 ? 1 : 0;
        } else {
            this.stairTicks -= this.stairTicks > 0 ? 1 : 0;
        }

        if (blockResult.isSlab()) {
            this.halfBlockTimer.reset();
            this.slabTicks += this.slabTicks < 20 ? 1 : 0;
        } else {
            this.slabTicks -= this.slabTicks > 0 ? 1 : 0;
        }

        if (blockResult.isLiquid()) {
            this.liquidTicks += this.liquidTicks < 20 ? 1 : 0;
            this.inLiquid = true;
        } else {
            this.liquidTicks -= this.liquidTicks > 0 ? 1 : 0;
            this.inLiquid = false;
        }

        if (blockResult.isCollideHorizontal()) {
            this.collideHorizontalTicks += this.collideHorizontalTicks < 20 ? 1 : 0;
        } else {
            this.collideHorizontalTicks -= this.collideHorizontalTicks > 0 ? 1 : 0;
        }

        if (blockResult.isIce()) {
            this.iceTicks += this.iceTicks < 20 ? 3 : 0;
        } else {
            this.iceTicks -= this.iceTicks > 0 ? 1 : 0;
        }

        if (blockResult.isBlockAbove()) {
            this.blockAboveTimer.reset();
            this.blockAboveTicks += (this.blockAboveTicks < 20 ? 5 : 0);
            this.blockAbove = true;
        } else {
            this.blockAbove = false;
            this.blockAboveTicks -= (this.blockAboveTicks > 0 ? 1 : 0);
        }

        if (blockResult.isBlockBelow()) {
            this.blockBelowTimer.reset();
            this.blockBelowTicks += (this.blockAboveTicks < 20 ? 5 : 0);
        } else {
            this.blockBelowTicks -= (this.blockAboveTicks > 0 ? 1 : 0);
        }

        if (blockResult.isSlime()) {
            this.slimeTicks += (this.slimeTicks < 20 ? 5 : 0);
            onSlime = true;
        } else {
            this.slimeTicks -= (this.slimeTicks > 0 ? 1 : 0);
            onSlime = false;
        }

        if (blockResult.isScaffolding()) {
            this.scaffoldingTicks += (this.scaffoldingTicks < 20 ? 1 : 0);
        } else {
            this.scaffoldingTicks -= (this.scaffoldingTicks > 0 ? 1 : 0);
        }

        if (blockResult.isClimbable()) {
            this.climbableTicks += (this.climbableTicks < 20 ? 1 : 0);
            this.onClimable = true;
        } else {
            this.climbableTicks -= (this.climbableTicks > 0 ? 1 : 0);
            this.onClimable = false;
        }

        if (blockResult.isSnow()) {
            this.snowTicks += (this.snowTicks < 20 ? 1 : 0);
        } else {
            this.snowTicks -= (this.snowTicks > 0 ? 1 : 0);
        }

        if (blockResult.isLillyPad()) {
            this.lillyPadTicks += (this.lillyPadTicks < 20 ? 1 : 0);
        } else {
            this.lillyPadTicks -= (this.lillyPadTicks > 0 ? 1 : 0);
        }

        if (blockResult.isCarpet()) {
            this.carpetTicks += (this.carpetTicks < 20 ? 1 : 0);
        } else {
            this.carpetTicks -= (this.carpetTicks > 0 ? 1 : 0);
        }

        this.serverGround = blockResult.isServerGround();
        this.collideHorizontal = blockResult.isCollideHorizontal();
    }

    boolean testCollision(double value) {
        return Math.abs(value % 0.015625D) < 1E-10;
    }

    @SuppressWarnings("deprecation")
    @Getter
    public static final class BlockResult {

        private boolean serverGround;

        private boolean liquid;
        private boolean stair;
        private boolean slab;
        private boolean ice;
        private boolean slime;
        private boolean climbable;
        private boolean scaffolding;
        private boolean snow;
        private boolean lillyPad;
        private boolean carpet;
        private boolean web;
        private boolean halfBlock;
        private boolean movingUp;
        private boolean soulSand;
        private boolean enderPortal;
        private boolean piston;
        private boolean honey;

        private boolean collideHorizontal;
        private boolean blockAbove;
        private boolean blockBelow;
        private boolean wall;

        private double lastBoundingBoxY;

        public void checkBlockAbove(User user) {
            this.blockAbove = StreamUtil.anyMatch(new BoundingBox(
                    (float) user.getMovementProcessor().getTo().getX(),
                    (float) user.getPlayer().getEyeLocation().getY(),
                    (float) user.getMovementProcessor().getTo().getZ(),
                    (float) user.getMovementProcessor().getTo().getX(),
                    (float) user.getPlayer().getEyeLocation().getY(),
                    (float) user.getMovementProcessor().getTo().getZ()).expand(.3, .0, .3)
                    .addXYZ(0, .6, 0).getCollidedBlocks(user), collideEntry ->
                    collideEntry.getBlock().isSolid());
        }

        public void checkBlockBelow(User user) {
            this.blockBelow = StreamUtil.anyMatch(new BoundingBox(
                    (float) user.getMovementProcessor().getTo().getX(),
                    (float) user.getPlayer().getEyeLocation().getY(),
                    (float) user.getMovementProcessor().getTo().getZ(),
                    (float) user.getMovementProcessor().getTo().getX(),
                    (float) user.getPlayer().getEyeLocation().getY(),
                    (float) user.getMovementProcessor().getTo().getZ()).expand(-.3, .0, -.3)
                    .addXYZ(0, -.6, 0).getCollidedBlocks(user), collideEntry ->
                    collideEntry.getBlock().isSolid());
        }

        public void checkHorizontal(User user) {
            this.collideHorizontal = StreamUtil.anyMatch(new BoundingBox(
                    (float) user.getMovementProcessor().getTo().getX(),
                    (float) user.getPlayer().getEyeLocation().getY(),
                    (float) user.getMovementProcessor().getTo().getZ(),
                    (float) user.getMovementProcessor().getTo().getX(),
                    (float) user.getPlayer().getEyeLocation().getY(),
                    (float) user.getMovementProcessor().getTo().getZ()).expand(1.2, .0, 1.2)
                    .getCollidedBlocks(user), collideEntry -> collideEntry.getBlock().isSolid());
        }

        public void process(CollideEntry collideEntry) {
            Material material = collideEntry.getBlock();

            double minY = collideEntry.getBoundingBox().minY;


            if (material.isSolid()) {
                serverGround = true;
            }

            switch (material) {
                case HONEY_BLOCK:
                    this.honey = true;
                    break;

                case LEGACY_ENDER_PORTAL:
                case LEGACY_ENDER_PORTAL_FRAME: {
                    this.enderPortal = true;
                    break;
                }

                case SOUL_SAND: {
                    this.soulSand = true;
                    break;
                }

                case COBWEB: {
                    this.web = true;
                    break;
                }

                case LEGACY_CARPET: {
                    this.carpet = true;
                    break;
                }

                case LILY_PAD: {
                    this.lillyPad = true;
                    break;
                }

                case SNOW: {
                    this.snow = true;
                    break;
                }

                case SCAFFOLDING:
                    this.scaffolding = true;
                case VINE:
                case LADDER: {
                    this.climbable = true;
                    break;
                }

                case SLIME_BLOCK: {
                    this.slime = true;
                    break;
                }

                case ICE:
                case PACKED_ICE: {
                    this.ice = true;
                    break;
                }

                case LAVA:
                case LEGACY_STATIONARY_LAVA:
                case LEGACY_STATIONARY_WATER:
                case WATER: {
                    this.liquid = true;
                    break;
                }

                case BREWING_STAND:
                case CHEST:
                case TRAPPED_CHEST:
                case ENDER_CHEST:
                case ENCHANTING_TABLE:
                case IRON_BARS:
                case LEGACY_SKULL:
                case LEGACY_BED: {
                    this.halfBlock = true;
                    break;
                }
            }

            if (material.name().toLowerCase().contains("bed") && !material.name().toLowerCase().contains("rock")) {
                this.halfBlock = true;
            }

            if (material.name().toLowerCase().contains("slab")) {
                this.slab = true;
            }

            if (material.name().toLowerCase().contains("stair")) {
                this.stair = true;
            }

            if (material.name().toLowerCase().contains("fence")) {
                this.halfBlock = true;
            }

            if (material.name().toLowerCase().contains("piston")) {
                this.piston = true;
            }

            if (material.name().toLowerCase().contains("wall")) {
                this.wall = true;
            }

            if (this.slab || this.stair) {
                this.halfBlock = true;
            }

            if (this.halfBlock) {
                double y = Math.abs(this.lastBoundingBoxY - minY);
                double round = y % 1;

                if ((round == .5 || round == 1.5) || (round > .4995 && round < .732)) {
                    this.movingUp = true;
                }
            }

            this.lastBoundingBoxY = minY;
        }
    }

    public boolean isBlockPlaced(Object packet, User user) {
        WrappedInBlockPlacePacket placePacket = new WrappedInBlockPlacePacket(packet, user.getPlayer());

        return Objects.requireNonNull(BlockUtil.getBlock(new Location(user.getPlayer().getWorld(), placePacket.getBlockPosition().getX(),
                placePacket.getBlockPosition().getY(), placePacket.getBlockPosition().getZ()))).getType() == placePacket.getItemStack().getType();
    }

    public boolean checkAnvilNear(User user) {
        World world = user.getPlayer().getWorld();

        for (double x = -0.5; x <= 0.5; x += 0.25) {
            for (double z = -0.5; z <= 0.5; z += 0.25) {
                double floorX = MathHelper.floor(user.getMovementProcessor().getTo().getX() + x);
                double floorY = MathHelper.floor(user.getMovementProcessor().getTo().getY());
                double floorZ = MathHelper.floor(user.getMovementProcessor().getTo().getZ() + z);

                Block block = BlockUtil.getBlock(new Location(world, floorX, floorY, floorZ));

                if (block instanceof AnvilInventory) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean checkSlimeNear(User user) {
        World world = user.getPlayer().getWorld();

        for (double x = -0.5; x <= 0.5; x += 0.1) {
            for (double z = -0.5; z <= 0.5; z += 0.1) {
                for (double y = -0.5; y <= 0.5; y += 0.1) {
                    double floorX = MathHelper.floor(user.getMovementProcessor().getTo().getX() + x);
                    double floorY = MathHelper.floor(user.getMovementProcessor().getTo().getY() + y);
                    double floorZ = MathHelper.floor(user.getMovementProcessor().getTo().getZ() + z);

                    Material block = Objects.requireNonNull(BlockUtil.getBlock(new Location(world, floorX, floorY, floorZ))).getBlockData().getMaterial();

                    if (block == Material.SLIME_BLOCK) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean checkLiquidNear(User user, double distance) {
        World world = user.getPlayer().getWorld();

        for (double x = -distance; x <= distance; x += distance) {
            for (double z = -distance; z <= distance; z += distance) {
                for (double y = -distance; y <= distance; y += distance) {
                    double floorX = MathHelper.floor(user.getMovementProcessor().getTo().getX() + x);
                    double floorY = MathHelper.floor(user.getMovementProcessor().getTo().getY() + y);
                    double floorZ = MathHelper.floor(user.getMovementProcessor().getTo().getZ() + z);

                    Material block = Objects.requireNonNull(BlockUtil.getBlock(new Location(world, floorX, floorY, floorZ))).getBlockData().getMaterial();

                    if (block.equals(Material.WATER) || block.equals(Material.LAVA)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isInsideBlock(User user) {
        return !Objects.requireNonNull(BlockUtil.getBlock(user.getPlayer().getLocation())).isEmpty() && !user.getCollisionProcessor().isOnClimable()
                && !user.getCollisionProcessor().isInLiquid();
    }
}
