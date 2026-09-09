package com.science.gtnl.common.block.blocks.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.science.gtnl.common.block.blocks.tile.TileEntityCardboardBox;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.CardboardBoxUtils;
import com.science.gtnl.utils.text.AnimatedTooltipHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

/**
 * Original Author: Mekanism
 * License: MIT
 * Original Code Date: 2014-01-13
 */
public class ItemBlockCardboardBox extends ItemBlock {

    private static boolean isMonitoring;

    public Block metaBlock;

    public ItemBlockCardboardBox(Block block) {
        super(block);
        metaBlock = block;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        CardboardBoxUtils.BlockData data = CardboardBoxUtils.getBlockData(itemstack);
        list.add(
            AnimatedTooltipHandler.BLUE + StatCollector
                .translateToLocal("gtnl.waila.cardboard_box.has_block_data." + (data != null ? "yes" : "no")));

        if (data != null) {
            if (Item.getItemFromBlock(data.block) == null) {
                list.add(
                    StatCollector.translateToLocal("gtnl.waila.cardboard_box.block") + data.block.getLocalizedName());
            } else {
                list.add(
                    StatCollector.translateToLocal("gtnl.waila.cardboard_box.block")
                        + new ItemStack(data.block, 1, data.metaSpecial != -1 ? data.metaSpecial : data.meta)
                            .getDisplayName());
            }
            list.add(
                StatCollector.translateToLocal("gtnl.waila.cardboard_box.metadata")
                    + (data.metaSpecial != -1 ? data.metaSpecial : data.meta));

            if (data.tileTag != null) {
                list.add(
                    StatCollector.translateToLocal("gtnl.waila.cardboard_box.tile_entity")
                        + data.tileTag.getString("id"));
            }
        }
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public IIcon getIconFromDamage(int i) {
        return metaBlock.getIcon(2, i);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!player.isSneaking() && !world.isAirBlock(x, y, z) && stack.getItemDamage() == 0) {
            Block block = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);

            if (!world.isRemote && CardboardBoxUtils.isBlockCompatible(Item.getItemFromBlock(block), meta)
                && block.getBlockHardness(world, x, y, z) != -1) {
                CardboardBoxUtils.BlockData data = new CardboardBoxUtils.BlockData();
                data.block = block;
                data.meta = meta;

                isMonitoring = true;

                if (world.getTileEntity(x, y, z) != null) {
                    TileEntity tile = world.getTileEntity(x, y, z);
                    if (tile instanceof IGregTechTileEntity gtTE) {
                        data.metaSpecial = gtTE.getMetaTileID();
                    }
                    NBTTagCompound tag = new NBTTagCompound();

                    tile.writeToNBT(tag);
                    data.tileTag = tag;
                }

                if (!player.capabilities.isCreativeMode) {
                    stack.stackSize--;
                }

                world.setBlock(x, y, z, BlockLoader.cardboardBox, 1, 3);

                isMonitoring = false;

                TileEntityCardboardBox tileEntity = (TileEntityCardboardBox) world.getTileEntity(x, y, z);

                if (tileEntity != null) {
                    tileEntity.storedData = data;
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int metadata) {
        if (world.isRemote) {
            return true;
        }

        boolean place = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);

        if (place) {
            TileEntityCardboardBox tileEntity = (TileEntityCardboardBox) world.getTileEntity(x, y, z);

            if (tileEntity != null) {
                tileEntity.storedData = CardboardBoxUtils.getBlockData(stack);
            }
        }

        return place;
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityItem && isMonitoring) {
            event.setCanceled(true);
        }
    }
}
