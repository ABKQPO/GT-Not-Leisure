package com.science.gtnl.common.block.casings.glass;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.gtnllib.utils.MetaTooltipUtils;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.item.MetaItemStackUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class ItemBlockGlass extends ItemBlock {

    public static final Int2ObjectMap<String[]> META_ITEM_TOOLTIPS_MAP = new Int2ObjectOpenHashMap<>();
    public static final IntSet META_SET = new IntOpenHashSet();

    public ItemBlockGlass(Block aBlock) {
        super(aBlock);
        setHasSubtypes(true);
        setMaxDamage(0);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
    }

    public static ItemStack initMetaBlockGlass(int meta) {
        return MetaItemStackUtils.initMetaItemStack(meta, BlockLoader.metaBlockGlass, META_SET);
    }

    public static ItemStack initMetaBlockGlass(String i18nName, int Meta, String[] tooltips) {
        if (tooltips != null) {
            MetaItemStackUtils.metaItemStackTooltipsAdd(META_ITEM_TOOLTIPS_MAP, Meta, tooltips);
        }
        return initMetaBlockGlass(Meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack aItemStack, EntityPlayer p_77624_2_, List<String> theTooltipsList,
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
