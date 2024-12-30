package com.science.gtnl.common.block.Casings.Casing;

import static com.science.gtnl.Utils.TextLocalization.mNoMobsToolTip;
import static com.science.gtnl.Utils.TextLocalization.mNoTileEntityToolTip;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

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

    // region Abstract
    public abstract boolean canCreatureSpawn();

    @SideOnly(Side.CLIENT)
    public abstract String[] getTooltips(int meta);
    // endregion

    @Override
    public String getUnlocalizedName(ItemStack aStack) {
        return this.field_150939_a.getUnlocalizedName() + "." + this.getDamage(aStack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings({ "unchecked" })
    public void addInformation(ItemStack aItemStack, EntityPlayer p_77624_2_, List theTooltipsList,
        boolean p_77624_4_) {
        if (null == aItemStack) return;
        String[] tooltips = getTooltips(aItemStack.getItemDamage());
        if (tooltips != null && tooltips.length > 0) {
            theTooltipsList.addAll(Arrays.asList(tooltips));
        }
        if (!canCreatureSpawn()) {
            theTooltipsList.add(mNoMobsToolTip);
            theTooltipsList.add(mNoTileEntityToolTip);
        }
    }

    @Override
    public int getMetadata(int aMeta) {
        return aMeta;
    }

}