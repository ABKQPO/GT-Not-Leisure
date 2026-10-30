package com.gtnllib.utils;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class GameUtils {

    // Looks the block up by registry name, so the mod that adds it does not have to be installed.
    public static Block blockOr(String modId, String name, Block fallback) {
        Block block = GameRegistry.findBlock(modId, name);
        return block != null ? block : fallback;
    }

    public static boolean isClientSide() {
        return FMLCommonHandler.instance()
            .getSide()
            .isClient();
    }

    public static String ensureUUID(NBTTagCompound aNBT) {
        if (aNBT.hasKey("storeUUID")) {
            return aNBT.getString("storeUUID");
        } else {
            String uuid = UUID.randomUUID()
                .toString();
            aNBT.setString("storeUUID", uuid);
            return uuid;
        }
    }

    public static MovingObjectPosition rayTraceBlock(EntityPlayer player, double reachDistance) {
        World world = player.getEntityWorld();

        float partialTicks = 1.0F;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;

        double posX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double posY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks + 1.62D - player.yOffset;
        double posZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

        Vec3 startVec = Vec3.createVectorHelper(posX, posY, posZ);

        float fYawRad = (float) Math.toRadians(-yaw) - (float) Math.PI;
        float fPitchRad = (float) Math.toRadians(-pitch);
        float cosPitch = -MathHelper.cos(fPitchRad);
        float sinPitch = MathHelper.sin(fPitchRad);
        float cosYaw = MathHelper.cos(fYawRad);
        float sinYaw = MathHelper.sin(fYawRad);

        double dirX = sinYaw * cosPitch;
        double dirY = sinPitch;
        double dirZ = cosYaw * cosPitch;

        Vec3 endVec = startVec.addVector(dirX * reachDistance, dirY * reachDistance, dirZ * reachDistance);

        return world.rayTraceBlocks(startVec, endVec, true);
    }

    public static MovingObjectPosition rayTrace(EntityPlayer p, boolean hitBlocks, boolean hitEntities,
        boolean hitEntityItem, double range) {
        final World w = p.getEntityWorld();

        final float f = 1.0F;
        float f1 = p.prevRotationPitch + (p.rotationPitch - p.prevRotationPitch) * f;
        final float f2 = p.prevRotationYaw + (p.rotationYaw - p.prevRotationYaw) * f;
        final double d0 = p.prevPosX + (p.posX - p.prevPosX) * f;
        final double d1 = p.prevPosY + (p.posY - p.prevPosY) * f + 1.62D - p.yOffset;
        final double d2 = p.prevPosZ + (p.posZ - p.prevPosZ) * f;
        final Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        final float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        final float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        final float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        final float f6 = MathHelper.sin(-f1 * 0.017453292F);
        final float f7 = f4 * f5;
        final float f8 = f3 * f5;

        final Vec3 vec31 = vec3.addVector(f7 * range, f6 * range, f8 * range);

        final AxisAlignedBB bb = AxisAlignedBB
            .getBoundingBox(
                Math.min(vec3.xCoord, vec31.xCoord),
                Math.min(vec3.yCoord, vec31.yCoord),
                Math.min(vec3.zCoord, vec31.zCoord),
                Math.max(vec3.xCoord, vec31.xCoord),
                Math.max(vec3.yCoord, vec31.yCoord),
                Math.max(vec3.zCoord, vec31.zCoord))
            .expand(16, 16, 16);

        Entity entity = null;
        double closest = 9999999.0D;
        if (hitEntities) {
            final List<Entity> list = w.getEntitiesWithinAABBExcludingEntity(p, bb);

            for (Entity o : list) {

                if (!o.isDead && o != p && (hitEntityItem || !(o instanceof EntityItem))) {
                    if (o.isEntityAlive()) {
                        if (o.riddenByEntity == p) {
                            continue;
                        }

                        f1 = 0.3F;
                        final AxisAlignedBB boundingBox = o.boundingBox.expand(f1, f1, f1);
                        final MovingObjectPosition movingObjectPosition = boundingBox.calculateIntercept(vec3, vec31);

                        if (movingObjectPosition != null) {
                            final double nd = vec3.squareDistanceTo(movingObjectPosition.hitVec);

                            if (nd < closest) {
                                entity = o;
                                closest = nd;
                            }
                        }
                    }
                }
            }
        }

        MovingObjectPosition pos = null;
        Vec3 vec = null;

        if (hitBlocks) {
            vec = Vec3.createVectorHelper(d0, d1, d2);
            pos = w.rayTraceBlocks(vec3, vec31, true);
        }

        if (entity != null && pos != null && pos.hitVec.squareDistanceTo(vec) > closest) {
            pos = new MovingObjectPosition(entity);
        } else if (entity != null && pos == null) {
            pos = new MovingObjectPosition(entity);
        }

        return pos;
    }

    public static boolean hasPermission(ICommandSender sender, int permissionLevel) {
        if (sender instanceof CommandBlockLogic || sender instanceof MinecraftServer
            || sender instanceof RConConsoleSource) {
            return true;
        }
        if (sender instanceof EntityPlayer player) {
            MinecraftServer server = MinecraftServer.getServer();
            GameProfile profile = player.getGameProfile();

            if (server.getConfigurationManager()
                .func_152596_g(profile)) {
                UserListOpsEntry entry = (UserListOpsEntry) server.getConfigurationManager()
                    .func_152603_m()
                    .func_152683_b(profile);

                return entry != null ? entry.func_152644_a() >= permissionLevel
                    : server.getOpPermissionLevel() >= permissionLevel;
            }
        }

        return false;
    }

    public static void placeItemBackInInventory(EntityPlayer player, ItemStack stack) {
        if (stack == null || stack.stackSize == 0) return;

        if (!player.inventory.addItemStackToInventory(stack)) {
            player.func_146097_a(stack, false, false);
        } else if (stack.stackSize > 0) {
            player.func_146097_a(stack, false, false);
        }

        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
        }
    }

    public static @NotNull ItemStack newItemStack(Item aItem) {
        return new ItemStack(aItem, 1, 0);
    }

    public static @NotNull ItemStack newItemStack(Block aBlock) {
        return new ItemStack(aBlock, 1, 0);
    }
}
