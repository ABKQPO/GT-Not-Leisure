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
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnergyModuleItem extends Item {

    public static final int TIERS = 3;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public EnergyModuleItem() {
        setUnlocalizedName("gtnl.energy_module");
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(GTNLCreativeTabs.GTNotLeisureItem);
        GameRegistry.registerItem(this, "energy_module");
        GTNLItemList.EnergyModule.set(new ItemStack(this));
    }

    public ItemStack stack(int tier) {
        return new ItemStack(this, 1, Math.max(1, Math.min(TIERS, tier)) - 1);
    }

    public static int tier(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof EnergyModuleItem)) return 0;
        return Math.max(1, Math.min(TIERS, stack.getItemDamage() + 1));
    }

    public static double capacity(int tier, double base) {
        var config = MainConfig.item.energyModule;
        double capacity = switch (tier) {
            case 3 -> config.capacityT3;
            case 2 -> config.capacityT2;
            case 1 -> config.capacityT1;
            default -> base;
        };
        return Double.isFinite(capacity) ? Math.max(0, capacity) : 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item.gtnl.energy_module.t" + tier(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(
            StatCollector.translateToLocalFormatted(
                "item.gtnl.energy_module.tooltip",
                EquipmentEnergy.format(capacity(tier(stack), 0))));
        tooltip.add(StatCollector.translateToLocal("item.gtnl.energy_module.install"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[TIERS];
        for (int tier = 1; tier <= TIERS; tier++)
            icons[tier - 1] = register.registerIcon(ScienceNotLeisure.RESOURCE_ROOT_ID + ":energy_module_t" + tier);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return icons[Math.max(0, Math.min(TIERS - 1, meta))];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        for (int tier = 1; tier <= TIERS; tier++) items.add(stack(tier));
    }
}
