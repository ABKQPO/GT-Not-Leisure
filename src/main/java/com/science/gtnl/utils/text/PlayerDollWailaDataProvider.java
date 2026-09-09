package com.science.gtnl.utils.text;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

import com.science.gtnl.common.block.blocks.tile.TileEntityPlayerDoll;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class PlayerDollWailaDataProvider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        final NBTTagCompound tag = accessor.getNBTData();

        if (tag.hasKey("CapeHttp", 8)) {
            String capeUrl = tag.getString("CapeHttp");
            if (!StringUtils.isNullOrEmpty(capeUrl)) {
                currentTip.add(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("gtnl.waila.player_doll.cape_url")
                        + " "
                        + EnumChatFormatting.GOLD
                        + capeUrl);
            }
        }

        if (tag.hasKey("RenderCapeMode", 1)) {
            byte renderMode = tag.getByte("RenderCapeMode");

            String renderStatus = switch (renderMode) {
                case 1 -> StatCollector.translateToLocal("gtnl.waila.player_doll.render_mode.cape");
                case 2 -> StatCollector.translateToLocal("gtnl.waila.player_doll.render_mode.elytra");
                default -> StatCollector.translateToLocal("gtnl.waila.player_doll.render_mode.off");
            };

            currentTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("gtnl.waila.player_doll.render_mode")
                    + " "
                    + EnumChatFormatting.GOLD
                    + renderStatus);
        }

        if (tag.hasKey("SkinHttp", 8)) {
            String skinUrl = tag.getString("SkinHttp");
            if (!StringUtils.isNullOrEmpty(skinUrl)) {
                currentTip.add(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("gtnl.waila.player_doll.skin_url")
                        + " "
                        + EnumChatFormatting.GOLD
                        + skinUrl);
                return currentTip;
            }
        }

        if (tag.hasKey("SkullOwner", 8)) {
            String playerName = tag.getString("SkullOwner");
            if (!StringUtils.isNullOrEmpty(playerName)) {
                currentTip.add(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("gtnl.waila.player_doll.player_name")
                        + " "
                        + EnumChatFormatting.GOLD
                        + playerName);
            }
        }

        return currentTip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        if (te instanceof TileEntityPlayerDoll playerDoll) {

            if (playerDoll.getSkullOwner() != null) {
                tag.setString("SkullOwner", playerDoll.getSkullOwner());
            }

            if (playerDoll.getSkinHttp() != null) {
                tag.setString("SkinHttp", playerDoll.getSkinHttp());
            }
            if (playerDoll.getCapeHttp() != null) {
                tag.setString("CapeHttp", playerDoll.getCapeHttp());
            }
            tag.setByte("RenderCapeMode", playerDoll.getRenderCapeMode());

        }
        return tag;
    }
}
