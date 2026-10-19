package com.science.gtnl.common.item.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.common.railgun.RailgunModuleType;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RailgunModuleItem extends Item {

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public RailgunModuleItem() {
        setUnlocalizedName("gtnl.railgun_module");
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, "railgun_module");
        GTNLItemList.RailgunModule.set(new ItemStack(this));
    }

    public ItemStack stack(RailgunModuleType type) {
        return new ItemStack(this, 1, type.meta);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        RailgunModuleType type = RailgunModuleType.byMeta(stack.getItemDamage());
        return "item.gtnl.railgun_module." + (type != null ? type : RailgunModuleType.byMeta(0)).id;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(StatCollector.translateToLocal(getUnlocalizedName(stack) + ".tooltip.0"));
        tooltip.add(StatCollector.translateToLocal("item.gtnl.railgun.module_install"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[RailgunModuleType.metaCount()];
        for (RailgunModuleType type : RailgunModuleType.values())
            icons[type.meta] = register.registerIcon(ScienceNotLeisure.RESOURCE_ROOT_ID + ":railgun_module_" + type.id);
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
        for (RailgunModuleType type : RailgunModuleType.values()) items.add(stack(type));
    }
}
