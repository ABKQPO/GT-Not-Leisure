package com.science.gtnl.common.item.items.equipment;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.equipment.IEquipmentVariant;
import com.science.gtnl.common.equipment.MetaLookup;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EquipmentVariantItem<T extends IEquipmentVariant> extends Item {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    protected EquipmentVariantItem() {
        setUnlocalizedName("gtnl." + name());
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, name());
    }

    protected abstract String name();

    protected abstract T[] variants();

    protected abstract MetaLookup<T> lookup();

    protected boolean listed(T variant) {
        return true;
    }

    public ItemStack stack(T variant) {
        return new ItemStack(this, 1, variant.meta());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        T variant = lookup().byMeta(stack.getItemDamage());
        return "item.gtnl." + name() + "." + (variant != null ? variant : lookup().byMeta(0)).id();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[lookup().size()];
        for (T variant : variants()) if (listed(variant)) icons[variant.meta()] = register
            .registerIcon(ScienceNotLeisure.RESOURCE_ROOT_ID + ":" + name() + "_" + variant.id());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        IIcon icon = icons[Math.max(0, Math.min(icons.length - 1, meta))];
        return icon != null ? icon : icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        for (T variant : variants()) if (listed(variant)) items.add(stack(variant));
    }
}
