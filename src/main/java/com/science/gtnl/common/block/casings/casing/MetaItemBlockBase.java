package com.science.gtnl.common.block.casings.casing;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class MetaItemBlockBase extends ItemBlock {

    public MetaItemBlockBase(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public Block getThisBlock() {
        return field_150939_a;
    }

    public abstract boolean canCreatureSpawn();

    @SideOnly(Side.CLIENT)
    public abstract String[] getTooltips(int meta);

    @Override
    public String getUnlocalizedName(ItemStack aStack) {
        return this.field_150939_a.getUnlocalizedName() + "." + this.getDamage(aStack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack aItemStack, EntityPlayer p_77624_2_, List<String> theTooltipsList,
        boolean p_77624_4_) {
        if (null == aItemStack) return;
        String[] tooltips = getTooltips(aItemStack.getItemDamage());
        if (tooltips != null && tooltips.length > 0) {
            theTooltipsList.addAll(Arrays.asList(tooltips));
        }
        if (!canCreatureSpawn()) {
            theTooltipsList.add(StatCollector.translateToLocal("Tooltip_NoMobsSpawnInThisBlock"));
            theTooltipsList.add(StatCollector.translateToLocal("Tooltip_NoTileEntitySpawnInThisBlock"));
        }
    }

    @Override
    public int getMetadata(int aMeta) {
        return aMeta;
    }

}
