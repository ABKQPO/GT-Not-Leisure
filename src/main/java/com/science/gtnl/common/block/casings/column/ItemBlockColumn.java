package com.science.gtnl.common.block.casings.column;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.gtnllib.utils.MetaTooltipUtils;
import com.science.gtnl.common.item.GTNLCreativeTabs;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.item.MetaItemStackUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class ItemBlockColumn extends ItemBlock {

    public static final Int2ObjectMap<String[]> META_ITEM_TOOLTIPS_MAP = new Int2ObjectOpenHashMap<>();
    public static final IntSet META_SET = new IntOpenHashSet();

    public ItemBlockColumn(Block aBlock) {
        super(aBlock);
        setHasSubtypes(true);
        setMaxDamage(0);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
    }

    public static ItemStack initMetaBlock(int meta) {
        return MetaItemStackUtils.initMetaItemStack(meta, BlockLoader.metaBlockColumn, META_SET);
    }

    public static ItemStack initMetaBlock(String i18nName, int Meta, String[] tooltips) {
        if (tooltips != null) {
            MetaItemStackUtils.metaItemStackTooltipsAdd(META_ITEM_TOOLTIPS_MAP, Meta, tooltips);
        }
        return initMetaBlock(Meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack aItemStack, EntityPlayer entityPlayer, List<String> theTooltipsList,
        boolean p_77624_4_) {
        MetaTooltipUtils.appendTooltips(META_ITEM_TOOLTIPS_MAP, aItemStack.getItemDamage(), theTooltipsList);
    }

    @Override
    public String getUnlocalizedName(ItemStack aStack) {
        return this.field_150939_a.getUnlocalizedName() + "." + this.getDamage(aStack);
    }

    @Override
    public int getMetadata(int aMeta) {
        return aMeta;
    }
}
