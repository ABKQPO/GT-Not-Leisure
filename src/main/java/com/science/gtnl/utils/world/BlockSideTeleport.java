package com.science.gtnl.utils.world;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.WirelessHighlightPacket;

import appeng.api.util.IOrientable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GTUtility;

public class BlockSideTeleport {

    public enum Outcome {

        MOVED,
        UNAVAILABLE,
        NO_SPACE,
        FAILED
    }

    private record Position(int x, int y, int z) {}

    private BlockSideTeleport() {}

    public static Outcome toBlockSide(EntityPlayerMP player, int dimension, int x, int y, int z, ForgeDirection front) {
        WorldServer world = DimensionManager.getWorld(dimension);
        if (world == null || !world.blockExists(x, y, z)) return Outcome.UNAVAILABLE;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null || tile.isInvalid()) return Outcome.UNAVAILABLE;
        if (player.isPlayerSleeping()) return Outcome.FAILED;

        ForgeDirection approach = front == null ? ForgeDirection.UNKNOWN : front;
        if (approach == ForgeDirection.UNKNOWN) {
            if (tile instanceof IGregTechTileEntity machine) approach = machine.getFrontFacing();
            else if (tile instanceof IOrientable orientable) approach = orientable.getForward();
        }
        Position landing = find(x, y, z, approach, position -> safe(world, player, position));
        if (landing == null) return Outcome.NO_SPACE;

        double targetX = landing.x() + 0.5, targetY = landing.y(), targetZ = landing.z() + 0.5;
        if (player.dimension != dimension) {
            // GT's helper handles dimension packets, player trackers, effects and inventory synchronization.
            if (!GTUtility.moveEntityToDimensionAtCoords(player, dimension, targetX, targetY, targetZ))
                return Outcome.FAILED;
        } else {
            player.mountEntity(null);
            if (player.riddenByEntity != null) player.riddenByEntity.mountEntity(null);
            player.closeScreen();
        }
        face(player, targetX, targetY, targetZ, x, y, z);
        ScienceNotLeisure.network.sendTo(new WirelessHighlightPacket(dimension, x, y, z), player);
        return Outcome.MOVED;
    }

    private static void face(EntityPlayerMP player, double x, double y, double z, int targetX, int targetY,
        int targetZ) {
        double dx = targetX + 0.5 - x, dz = targetZ + 0.5 - z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = horizontal < 1.0E-6 ? player.rotationYaw : (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(Math.atan2(y + player.getEyeHeight() - targetY - 0.5, horizontal));
        player.motionX = player.motionY = player.motionZ = 0;
        player.fallDistance = 0;
        player.velocityChanged = true;
        player.playerNetServerHandler.setPlayerLocation(x, y, z, yaw, pitch);
    }

    private static Position find(int x, int y, int z, ForgeDirection front, Predicate<Position> safe) {
        Set<Position> candidates = new LinkedHashSet<>();
        if (front != null && front != ForgeDirection.UNKNOWN && front.offsetY == 0)
            candidates.add(new Position(x + front.offsetX, y, z + front.offsetZ));
        for (int dy : new int[] { 0, 1, -1, 2, -2 }) {
            if (dy > 0) candidates.add(new Position(x, y + dy, z));
            for (int radius = 1; radius <= 3; radius++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (Math.max(Math.abs(dx), Math.abs(dz)) == radius)
                            candidates.add(new Position(x + dx, y + dy, z + dz));
                    }
                }
            }
        }
        return candidates.stream()
            .filter(safe)
            .findFirst()
            .orElse(null);
    }

    private static boolean safe(WorldServer world, EntityPlayerMP player, Position pos) {
        double halfWidth = Math.max(0.3, player.width / 2.0);
        double height = Math.max(1.8, player.height);
        int margin = (int) Math.ceil(halfWidth) + 2;
        if (pos.y() < 1 || pos.y() + height > world.getActualHeight()
            || Math.abs((long) pos.x()) + margin >= 30_000_000
            || Math.abs((long) pos.z()) + margin >= 30_000_000
            || !world.checkChunksExist(
                pos.x() - margin,
                pos.y() - 1,
                pos.z() - margin,
                pos.x() + margin,
                (int) Math.ceil(pos.y() + height),
                pos.z() + margin))
            return false;
        Block floor = world.getBlock(pos.x(), pos.y() - 1, pos.z());
        if (!floor.isSideSolid(world, pos.x(), pos.y() - 1, pos.z(), ForgeDirection.UP) || hazardous(floor))
            return false;
        AxisAlignedBB body = AxisAlignedBB.getBoundingBox(
            pos.x() + 0.5 - halfWidth,
            pos.y(),
            pos.z() + 0.5 - halfWidth,
            pos.x() + 0.5 + halfWidth,
            pos.y() + height,
            pos.z() + 0.5 + halfWidth);
        if (!world.getCollidingBoundingBoxes(player, body)
            .isEmpty() || world.isAnyLiquid(body)) return false;
        for (int x = MathHelper.floor_double(body.minX); x <= MathHelper.floor_double(body.maxX); x++) {
            for (int y = pos.y() - 1; y <= MathHelper.floor_double(body.maxY); y++) {
                for (int z = MathHelper.floor_double(body.minZ); z <= MathHelper.floor_double(body.maxZ); z++) {
                    if (hazardous(world.getBlock(x, y, z))) return false;
                }
            }
        }
        return true;
    }

    private static boolean hazardous(Block block) {
        return block.getMaterial()
            .isLiquid() || block == Blocks.fire
            || block == Blocks.cactus
            || block == Blocks.portal
            || block == Blocks.end_portal
            || block == Blocks.web;
    }
}
