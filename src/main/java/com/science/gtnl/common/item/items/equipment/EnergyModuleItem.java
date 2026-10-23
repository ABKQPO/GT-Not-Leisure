package com.science.gtnl.common.item.items.equipment;

import java.util.List;
import java.util.Locale;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.equipment.IEquipmentVariant;
import com.science.gtnl.common.equipment.MetaLookup;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnergyModuleItem extends EquipmentVariantItem<EnergyModuleItem.Tier> {

    public static final int TIERS = 3;

    public EnergyModuleItem() {
        GTNLItemList.EnergyModule.set(new ItemStack(this));
    }

    public ItemStack stack(int tier) {
        return stack(Tier.of(tier));
    }

    public static int tier(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof EnergyModuleItem)) return 0;
        Tier tier = Tier.LOOKUP.byMeta(stack.getItemDamage());
        return tier == null ? 0 : tier.meta() + 1;
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
    protected String name() {
        return "energy_module";
    }

    @Override
    protected Tier[] variants() {
        return Tier.values();
    }

    @Override
    protected MetaLookup<Tier> lookup() {
        return Tier.LOOKUP;
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

    public enum Tier implements IEquipmentVariant {

        T1(0),
        T2(1),
        T3(2);

        static final MetaLookup<Tier> LOOKUP = new MetaLookup<>(values());

        private final int meta;

        Tier(int meta) {
            this.meta = meta;
        }

        public static Tier of(int tier) {
            return LOOKUP.byMeta(Math.max(1, Math.min(TIERS, tier)) - 1);
        }

        @Override
        public String id() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public int meta() {
            return meta;
        }
    }
}
