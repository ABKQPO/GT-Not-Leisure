package com.science.gtnl.common.item;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.science.gtnl.loader.ItemLoader;
import com.science.gtnl.utils.item.MetaItemStackUtils;
import com.science.gtnl.utils.item.MetaTooltipUtils;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fox.spiteful.avaritia.render.IHaloRenderItem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * An ItemStack Generator used Meta Item System.
 * <li>Use {@link #initItem(int)} to create your Item at ItemList01.
 *
 */
@Optional.Interface(iface = "fox.spiteful.avaritia.render.IHaloRenderItem", modid = "Avaritia")
public class MetaItemAdder extends ItemAdder_Basic implements IHaloRenderItem {

    public static final Int2ObjectMap<String[]> META_ITEM_TOOLTIPS_MAP = new Int2ObjectOpenHashMap<>();
    public static final IntSet META_SET = new IntOpenHashSet();

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

        return MetaItemStackUtils.initMetaItemStack(aMeta, ItemLoader.metaItem, META_SET);

    }

    public static ItemStack initItem(int aMeta, String[] tooltips) {

        if (tooltips != null) {
            MetaItemStackUtils.metaItemStackTooltipsAdd(META_ITEM_TOOLTIPS_MAP, aMeta, tooltips);
        }

        return initItem(aMeta);

    }

    @Override
    public String getUnlocalizedName(ItemStack aItemStack) {
        return "gtnl.item." + this.unlocalizedName + "." + aItemStack.getItemDamage();
    }

    @Override
    public String getUnlocalizedName() {
        return "gtnl.item." + this.unlocalizedName;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
        this.itemIcon = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":meta_item/0");
        MetaTooltipUtils.registerIcons(
            META_SET,
            ItemStaticDataClientOnly.META_ITEM_01_ICONS,
            iconRegister,
            RESOURCE_ROOT_ID + ":meta_item/");
        halo = new IIcon[1];
        halo[0] = iconRegister.registerIcon(RESOURCE_ROOT_ID + ":" + "halonoise");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int aMetaData) {
        return MetaTooltipUtils.getIcon(ItemStaticDataClientOnly.META_ITEM_01_ICONS, aMetaData);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack aItemStack, EntityPlayer p_77624_2_, List<String> theTooltipsList,
        boolean p_77624_4_) {
        MetaTooltipUtils.appendTooltips(META_ITEM_TOOLTIPS_MAP, aItemStack.getItemDamage(), theTooltipsList);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item aItem, CreativeTabs aCreativeTabs, List<ItemStack> aList) {
        for (int meta : META_SET) {
            aList.add(new ItemStack(ItemLoader.metaItem, 1, meta));
        }
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public boolean drawHalo(ItemStack stack) {
        return switch (stack.getItemDamage()) {
            case 26, 27 -> true;
            default -> false;
        };
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public IIcon getHaloTexture(ItemStack stack) {
        return switch (stack.getItemDamage()) {
            default -> halo[0];
        };
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public int getHaloSize(ItemStack stack) {
        return 10;
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public boolean drawPulseEffect(ItemStack stack) {
        return false;
    }

    @Override
    @Optional.Method(modid = "Avaritia")
    public int getHaloColour(ItemStack stack) {
        return 0xE6FFFFFF;
    }
}
