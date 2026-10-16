package com.science.gtnl.common.item.items;

import java.util.List;
import java.util.Locale;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

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
        return new ItemStack(this, 1, type.ordinal());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = Math.max(0, Math.min(RailgunModuleType.values().length - 1, stack.getItemDamage()));
        return "item.gtnl.railgun_module." + RailgunModuleType.values()[meta].name()
            .toLowerCase(Locale.ROOT);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(StatCollector.translateToLocal(getUnlocalizedName(stack) + ".tooltip.0"));
        tooltip.add(StatCollector.translateToLocal("gtnl.railgun.module_install"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[RailgunModuleType.values().length];
        for (RailgunModuleType type : RailgunModuleType.values()) icons[type.ordinal()] = register.registerIcon(
            "sciencenotleisure:" + (type.energy() ? "energy_module"
                : "railgun_module_" + type.name()
                    .toLowerCase(Locale.ROOT)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return icons[Math.max(0, Math.min(icons.length - 1, meta))];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return switch (stack.getItemDamage()) {
            case 4 -> 0x70DFFF;
            case 5 -> 0xCB80FF;
            case 6 -> 0xFF6090;
            default -> 0xFFFFFF;
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        for (RailgunModuleType type : RailgunModuleType.values()) items.add(stack(type));
    }
}
