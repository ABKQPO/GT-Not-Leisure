package com.science.gtnl.common.block.casings.glass;

import static com.science.gtnl.utils.item.MetaItemStackUtils.initMetaItemStack;
import static com.science.gtnl.utils.item.MetaItemStackUtils.metaItemStackTooltipsAdd;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.loader.BlockLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class ItemBlockGlass extends ItemBlock {

    public static Int2ObjectMap<String[]> metaItemTooltipsMap = new Int2ObjectOpenHashMap<>();
    public static IntSet metaSet = new IntOpenHashSet();

    public ItemBlockGlass(Block aBlock) {
        super(aBlock);
        setHasSubtypes(true);
        setMaxDamage(0);
        this.setCreativeTab(GTNLCreativeTabs.GTNotLeisureBlock);
    }

    public static ItemStack initMetaBlockGlass(int meta) {
        return initMetaItemStack(meta, BlockLoader.metaBlockGlass, metaSet);
    }

    public static ItemStack initMetaBlockGlass(String i18nName, int Meta, String[] tooltips) {
        if (tooltips != null) {
            metaItemStackTooltipsAdd(metaItemTooltipsMap, Meta, tooltips);
        }
        return initMetaBlockGlass(Meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack aItemStack, EntityPlayer p_77624_2_, List<String> theTooltipsList,
        boolean p_77624_4_) {
        int meta = aItemStack.getItemDamage();
        if (null != metaItemTooltipsMap.get(meta)) {
            String[] tooltips = metaItemTooltipsMap.get(meta);
            theTooltipsList.addAll(Arrays.asList(tooltips));
        }
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
