package com.science.gtnl.utils.text;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.science.gtnl.common.block.blocks.tile.TileEntityCardboardBox;
import com.science.gtnl.utils.CardboardBoxUtils;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class CardboardBoxWailaDataProvider implements IWailaDataProvider {

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
        NBTTagCompound tag = accessor.getNBTData();
        if (!tag.hasKey("blockData")) {
            return currentTip;
        }

        CardboardBoxUtils.BlockData data = CardboardBoxUtils.BlockData.read(tag.getCompoundTag("blockData"));

        currentTip.add(
            AnimatedTooltipHandler.BLUE + StatCollector
                .translateToLocal("gtnl.waila.cardboard_box.has_block_data." + (data != null ? "yes" : "no")));

        if (data != null) {
            if (Item.getItemFromBlock(data.block) == null) {
                currentTip.add(
                    StatCollector.translateToLocal("gtnl.waila.cardboard_box.block") + data.block.getLocalizedName());
            } else {
                currentTip.add(
                    StatCollector.translateToLocal("gtnl.waila.cardboard_box.block")
                        + new ItemStack(data.block, 1, data.metaSpecial != -1 ? data.metaSpecial : data.meta)
                            .getDisplayName());
            }
            currentTip.add(
                StatCollector.translateToLocal("gtnl.waila.cardboard_box.metadata")
                    + (data.metaSpecial != -1 ? data.metaSpecial : data.meta));

            String tileEntityId = data.getTileEntityId();
            if (tileEntityId != null && !tileEntityId.isEmpty()) {
                currentTip.add(StatCollector.translateToLocal("gtnl.waila.cardboard_box.tile_entity") + tileEntityId);
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
        if (te instanceof TileEntityCardboardBox cardboardBox) {
            if (cardboardBox.storedData != null) {
                // waila data only has to be displayed, so oversized tile entity data is dropped instead of stored
                tag.setTag("blockData", CardboardBoxUtils.createBlockDataTag(cardboardBox.storedData, false));
            }
        }
        return tag;
    }
}
