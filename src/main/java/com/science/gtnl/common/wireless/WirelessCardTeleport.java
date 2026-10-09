package com.science.gtnl.common.wireless;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.WirelessCardGuiPacket;
import com.science.gtnl.common.wireless.WirelessCardTeleport.Landing.Position;
import com.science.gtnl.common.wireless.WirelessChannelPrototype.Address;

import appeng.api.util.IOrientable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GTUtility;

/** Server-side movement to an already loaded and permission-checked device. Free, with no cooldown. */
final class WirelessCardTeleport {

    private WirelessCardTeleport() {}

    /** Bounded search; remains independent of world access so its policy can be tested on its own. */
    static final class Landing {

        record Position(int x, int y, int z) {}

        private Landing() {}

        static Position find(int x, int y, int z, ForgeDirection front, Predicate<Position> safe) {
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
    }

    static boolean teleport(EntityPlayerMP player, Address target) {
        WorldServer world = DimensionManager.getWorld(target.dimension());
        TileEntity tile = target.tile();
        if (world == null || tile == null || tile.isInvalid()) return fail(player, "teleport_unavailable");
        if (player.isPlayerSleeping()) return fail(player, "teleport_failed");
        ForgeDirection front = target.side();
        if (front == ForgeDirection.UNKNOWN) {
            if (tile instanceof IGregTechTileEntity machine) front = machine.getFrontFacing();
            else if (tile instanceof IOrientable orientable) front = orientable.getForward();
        }
        Position landing = Landing
            .find(target.x(), target.y(), target.z(), front, position -> safe(world, player, position));
        if (landing == null) return fail(player, "teleport_no_space");
        double x = landing.x() + 0.5, y = landing.y(), z = landing.z() + 0.5;
        if (player.dimension != target.dimension()) {
            // GT's helper handles dimension packets, player trackers, effects and inventory synchronization.
            if (!GTUtility.moveEntityToDimensionAtCoords(player, target.dimension(), x, y, z))
                return fail(player, "teleport_failed");
        } else {
            player.mountEntity(null);
            if (player.riddenByEntity != null) player.riddenByEntity.mountEntity(null);
            player.closeScreen();
        }
        double dx = target.x() + 0.5 - x, dz = target.z() + 0.5 - z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = horizontal < 1.0E-6 ? player.rotationYaw : (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(Math.atan2(y + player.getEyeHeight() - target.y() - 0.5, horizontal));
        player.motionX = player.motionY = player.motionZ = 0;
        player.fallDistance = 0;
        player.velocityChanged = true;
        player.playerNetServerHandler.setPlayerLocation(x, y, z, yaw, pitch);
        ScienceNotLeisure.network.sendTo(
            new WirelessCardGuiPacket.TeleportHighlight(target.dimension(), target.x(), target.y(), target.z()),
            player);
        player.addChatMessage(
            new ChatComponentTranslation(
                "gtnl.wireless.gui.teleport_done",
                target.dimension(),
                target.x(),
                target.y(),
                target.z()));
        return true;
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

    private static boolean fail(EntityPlayerMP player, String key) {
        player.addChatMessage(new ChatComponentTranslation("gtnl.wireless.gui." + key));
        return false;
    }
}
