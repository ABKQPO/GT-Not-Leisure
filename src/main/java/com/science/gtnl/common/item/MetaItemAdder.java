package com.science.gtnl.common.item;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.utils.item.MetaItemStackUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fox.spiteful.avaritia.render.IHaloRenderItem;

/**
 * An ItemStack Generator used Meta Item System.
 * <li>Use {@link #initItem(int)} to create your Item at ItemList01.
 *
 */
public class MetaItemAdder extends ItemAdder_Basic implements IHaloRenderItem {

    /**
     * A Set contains the meta value that has been used.
     */
    public static final Set<Integer> metaSet = new HashSet<>();
    public static final Map<Integer, String[]> metaItemTooltipsMap = new HashMap<>();

    public final String unlocalizedName;
    public IIcon[] halo;

    /**
     * Create the basic item MetaItem.
     */
    public MetaItemAdder(String aName, CreativeTabs aCreativeTabs) {
        super(aName, aCreativeTabs);
        this.unlocalizedName = aName;
    }

    /**
     * The method about creating Items with ItemStack form by Meta Item System.
     * Use this method to create Items at ReAvaItemList.
     *
     * @param aMeta The MetaValue of your creating item.
     * @return Return the Item with ItemStack form you create.
     */
    public static ItemStack initItem(int aMeta) {

        return MetaItemStackUtils.initMetaItemStack(aMeta, ItemLoader.metaItem, metaSet);

    }

    public static ItemStack initItem(int aMeta, String[] tooltips) {

        if (tooltips != null) {
            MetaItemStackUtils.metaItemStackTooltipsAdd(metaItemTooltipsMap, aMeta, tooltips);
        }

        return initItem(aMeta);

    }

    @Override
    public String getUnlocalizedName(ItemStack aItemStack) {
        return "item." + this.unlocalizedName + "." + aItemStack.getItemDamage();
    }

    @Override
    public String getUnlocalizedName() {
        return "item." + this.unlocalizedName;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
        this.itemIcon = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + "MetaItem/0");
        for (int meta : metaSet) {
            ItemStaticDataClientOnly.iconsMapMetaItem01
                .put(meta, iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + "MetaItem/" + meta));
        }
        halo = new IIcon[1];
        halo[0] = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + "halonoise");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int aMetaData) {
        return ItemStaticDataClientOnly.iconsMapMetaItem01.containsKey(aMetaData)
            ? ItemStaticDataClientOnly.iconsMapMetaItem01.get(aMetaData)
            : ItemStaticDataClientOnly.iconsMapMetaItem01.get(0);
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
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item aItem, CreativeTabs aCreativeTabs, List<ItemStack> aList) {
        for (int meta : metaSet) {
            aList.add(new ItemStack(ItemLoader.metaItem, 1, meta));
        }
    }

    @Override
    public boolean drawHalo(ItemStack stack) {
        return switch (stack.getItemDamage()) {
            case 26, 27 -> true;
            default -> false;
        };
    }

    @Override
    public IIcon getHaloTexture(ItemStack stack) {
        return switch (stack.getItemDamage()) {
            default -> halo[0];
        };
    }

    @Override
    public int getHaloSize(ItemStack stack) {
        return 10;
    }

    @Override
    public boolean drawPulseEffect(ItemStack stack) {
        return false;
    }

    @Override
    public int getHaloColour(ItemStack stack) {
        return 0xE6FFFFFF;
    }
}
