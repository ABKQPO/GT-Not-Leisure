package com.science.gtnl.common.item.items;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.part.PartEnergyCellBase;

import appeng.api.AEApi;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.PowerUnits;
import appeng.api.parts.IPartItem;
import appeng.core.localization.GuiText;
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
        tooltip.add(formatPartTooltip(getStoredPower(stack), getMaximumPower()));
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

    protected static String formatPartTooltip(double currentPower, double maximumPower) {
        double percent = maximumPower <= 0.0 ? 0.0 : currentPower / maximumPower;
        return GuiText.StoredEnergy.getLocal() + ": "
            + NumberFormat.getNumberInstance(Locale.US)
                .format(currentPower)
            + PowerUnits.AE.getLocal()
            + " - "
            + MessageFormat.format(" {0,number,#.##%} ", percent);
    }
}
