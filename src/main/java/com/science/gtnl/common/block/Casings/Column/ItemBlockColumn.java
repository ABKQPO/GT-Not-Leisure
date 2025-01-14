package com.science.gtnl.common.block.Casings.Column;

import static com.science.gtnl.Utils.MetaItemStackUtils.initMetaItemStack;
import static com.science.gtnl.Utils.MetaItemStackUtils.metaItemStackTooltipsAdd;
import static com.science.gtnl.common.block.Casings.BasicBlocks.MetaBlockColumn;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import com.science.gtnl.client.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.util.GTLanguageManager;

public class ItemBlockColumn extends ItemBlock {

    // region statics

    public static final Map<Integer, String[]> MetaBlockTooltipsMap = new HashMap<>();
    // public static final Map<Integer, ItemStack> MetaBlockMap01 = new HashMap<>();
    public static final Set<Integer> MetaBlockSet = new HashSet<>();

    // endregion
    // -----------------------
    // region Constructors

    public ItemBlockColumn(Block aBlock) {
        super(aBlock);
        setHasSubtypes(true);
        setMaxDamage(0);
        this.setCreativeTab(CreativeTabsLoader.GTNotLeisure);
    }

    // endregion
    // -----------------------
    // region MetaBlock Generators

    public static ItemStack initMetaBlock(String i18nName, int Meta) {
        return initMetaItemStack(i18nName, Meta, MetaBlockColumn, MetaBlockSet);
    }

    public static ItemStack initMetaBlock(String i18nName, int Meta, String[] tooltips) {
        if (tooltips != null) {
            metaItemStackTooltipsAdd(MetaBlockTooltipsMap, Meta, tooltips);
        }
        return initMetaBlock(i18nName, Meta);
    }

    // endregion
    // -----------------------
    // region Member Variables

    public final String mNoMobsToolTip = GTLanguageManager
        .addStringLocalization("gt.nomobspawnsonthisblock", "Mobs cannot Spawn on this Block");
    public final String mNoTileEntityToolTip = GTLanguageManager
        .addStringLocalization("gt.notileentityinthisblock", "This is NOT a TileEntity!");

    // endregion
    // -----------------------
    // region Overrides

    /**
     * Handle the tooltips.
     *
     * @param aItemStack
     * @param theTooltipsList
     */
    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings({ "unchecked" })
    public void addInformation(ItemStack aItemStack, EntityPlayer p_77624_2_, List theTooltipsList,
        boolean p_77624_4_) {
        int meta = aItemStack.getItemDamage();
        if (null != MetaBlockTooltipsMap.get(meta)) {
            String[] tooltips = MetaBlockTooltipsMap.get(meta);
            theTooltipsList.addAll(Arrays.asList(tooltips));
        }
        theTooltipsList.add(mNoMobsToolTip);
        theTooltipsList.add(mNoTileEntityToolTip);
    }

    @Override
    public String getUnlocalizedName(ItemStack aStack) {
        return this.field_150939_a.getUnlocalizedName() + "." + this.getDamage(aStack);
    }

    @Override
    public int getMetadata(int aMeta) {
        return aMeta;
    }

    // endregion
}
