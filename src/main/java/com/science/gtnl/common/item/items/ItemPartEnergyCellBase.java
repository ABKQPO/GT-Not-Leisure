package com.science.gtnl.common.item.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.part.PartEnergyCellBase;

import appeng.api.AEApi;
import appeng.api.config.PowerMultiplier;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemPartEnergyCellBase extends Item implements IPartItem {

    public ItemPartEnergyCellBase(String unlocalizedName) {
        setMaxStackSize(1);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
        setUnlocalizedName(unlocalizedName);
        GameRegistry.registerItem(this, getUnlocalizedName());
        AEApi.instance()
            .partHelper()
            .setItemBusRenderer(this);
    }

    protected abstract double getBaseCapacity();

    protected abstract Block getEnergyCellBlock();

    public double getMaximumPower() {
        return getBaseCapacity() * PowerMultiplier.CONFIG.multiplier;
    }

    public double getStoredPower(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag == null ? 0.0 : Math.max(0.0, Math.min(tag.getDouble("internalCurrentPower"), getMaximumPower()));
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float xOffset, float yOffset, float zOffset) {
        return AEApi.instance()
            .partHelper()
            .placeBus(player.getHeldItem(), x, y, z, side, player, world);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advancedTooltips) {
        tooltip.add(
            StatCollector.translateToLocalFormatted(
                "Tooltip_PartEnergyCell_00",
                NumberFormatUtil.formatNumber(getStoredPower(stack)),
                NumberFormatUtil.formatNumber(getMaximumPower())));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack) {
        return getEnergyCellBlock()
            .getIcon(0, PartEnergyCellBase.getChargeLevel(getStoredPower(stack), getMaximumPower()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }
}
