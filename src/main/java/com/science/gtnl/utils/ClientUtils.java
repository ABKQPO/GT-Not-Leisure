package com.science.gtnl.utils;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.factory.GuiFactories;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.tile.TileEntityMultiEssentiaJar;
import com.science.gtnl.common.packet.GetTileEntityNBTRequestPacket;
import com.science.gtnl.common.packet.RequestGameProfilePacket;
import com.science.gtnl.utils.item.ItemUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.item.IPickBlockHandler;
import gregtech.api.items.MetaBaseItem;
import gregtech.crossmod.backhand.Backhand;

@SideOnly(Side.CLIENT)
public class ClientUtils {

    public static void showSubtitle(String messageKey) {
        IChatComponent component = new ChatComponentTranslation(messageKey);
        component.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE));
        Minecraft.getMinecraft().ingameGUI.func_110326_a(component.getFormattedText(), true);
    }

    public static void showSubtitle(String messageKey, int range) {
        IChatComponent component = new ChatComponentTranslation(messageKey, range);
        component.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE));
        Minecraft.getMinecraft().ingameGUI.func_110326_a(component.getFormattedText(), true);
    }

    public static boolean onBeforePickBlock(EntityPlayer playerMP, World world, boolean useAE) {
        if (tryOpenMultiEssentiaJarBlock(world)) {
            return true;
        }
        if (tryHandlePickBlockHandler(playerMP)) {
            return true;
        }
        boolean isCtrlKeyDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        double reachDistance = 1000;
        boolean handled = onPickEntity(playerMP, reachDistance, useAE);
        if (!handled) {
            if (isCtrlKeyDown) {
                return onPickBlockNBTRange(playerMP, world, reachDistance, useAE);
            } else {
                onPickBlockRange(playerMP, world, reachDistance, useAE);
                return true;
            }
        }
        return false;
    }

    private static boolean tryOpenMultiEssentiaJarBlock(World world) {
        MovingObjectPosition target = Minecraft.getMinecraft().objectMouseOver;

        if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return false;
        }

        TileEntity tile = world.getTileEntity(target.blockX, target.blockY, target.blockZ);

        if (!(tile instanceof TileEntityMultiEssentiaJar jar)) {
            return false;
        }

        // 与服务器端 OPEN_BLOCK_GUI 分支保持一致：
        // 有标签或空罐时不拦截，放行原版 pick block。
        if (jar.hasFilterLabel() || jar.getStoredTypeCount() <= 0) {
            return false;
        }

        // 请求服务端打开方块罐 GUI，MUI2 自带该客户端→服务端打开机制
        GuiFactories.tileEntity()
            .openClient(target.blockX, target.blockY, target.blockZ);

        return true;
    }

    private static boolean tryHandlePickBlockHandler(EntityPlayer player) {
        ItemStack mainHand = player.getCurrentEquippedItem();
        if (tryHandlePickBlockStack(mainHand, player)) {
            return true;
        }

        ItemStack offHand = Backhand.getOffhandItem(player);
        return tryHandlePickBlockStack(offHand, player);
    }

    private static boolean tryHandlePickBlockStack(ItemStack stack, EntityPlayer player) {
        if (stack == null) return false;
        Item item = stack.getItem();
        if (item instanceof IPickBlockHandler handler) {
            return handler.onPickBlock(stack, player);
        }
        return item instanceof MetaBaseItem metaBaseItem && metaBaseItem.onMiddleClick(stack, player);
    }

    public static boolean onPickEntity(EntityPlayer player, double range, boolean useAE) {
        MovingObjectPosition target = Utils.rayTrace(player, false, true, true, range);
        if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
            return false;
        }
        ItemStack result = null;
        Entity entity = target.entityHit;
        if (entity == null) return false;
        boolean isCreative = player.capabilities.isCreativeMode;

        if (entity instanceof EntityItem item) {
            ItemStack dropItem = item.getEntityItem()
                .copy();
            dropItem.stackSize = 1;
            result = dropItem;
        } else if (entity instanceof EntityPlayer entityPlayer) {
            ScienceNotLeisure.network
                .sendToServer(new RequestGameProfilePacket(entityPlayer.getCommandSenderName(), isCreative, useAE));
            return true;
        } else {
            int entityID = EntityList.getEntityID(entity);
            if (entityID <= 0) return false;
            Map<Integer, EntityList.EntityEggInfo> entityEggs = EntityList.entityEggs;
            for (EntityList.EntityEggInfo obj : entityEggs.values()) {
                if (obj != null && obj.spawnedID == entityID) {
                    result = new ItemStack(Items.spawn_egg, 1, obj.spawnedID);
                }
            }
        }

        return ItemUtils.placeItemInHotbar(player, result, isCreative, useAE);
    }

    public static boolean onPickBlockNBTRange(EntityPlayer player, World world, double reachDistance, boolean useAE) {
        MovingObjectPosition target = Utils.rayTraceBlock(player, reachDistance);
        if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return false;

        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;
        Block block = world.getBlock(x, y, z);
        if (block == null) return false;

        if (!block.isAir(world, x, y, z)) {
            ItemStack result = block.getPickBlock(target, world, x, y, z, player);
            if (result == null) return false;

            Item item = result.getItem();
            int blockID = Item.getIdFromItem(item);
            int blockMeta = result.getItemDamage();

            if (blockID == 0) return false;

            TileEntity tileentity = world.getTileEntity(x, y, z);
            if (tileentity != null) {
                ScienceNotLeisure.network.sendToServer(new GetTileEntityNBTRequestPacket(x, y, z, blockID, blockMeta));
                return true;
            } else {
                boolean isCreative = player.capabilities.isCreativeMode;
                return ItemUtils
                    .placeItemInHotbar(player, block.getPickBlock(target, world, x, y, z, player), isCreative, useAE);
            }
        }

        return false;
    }

    public static boolean onPickBlockRange(EntityPlayer player, World world, double reachDistance, boolean useAE) {
        MovingObjectPosition target = Utils.rayTraceBlock(player, reachDistance);

        if (target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return false;
        }

        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;

        Block block = world.getBlock(x, y, z);
        if (block.isAir(world, x, y, z)) {
            return false;
        }

        boolean isCreative = player.capabilities.isCreativeMode;
        return ItemUtils
            .placeItemInHotbar(player, block.getPickBlock(target, world, x, y, z, player), isCreative, useAE);
    }
}
