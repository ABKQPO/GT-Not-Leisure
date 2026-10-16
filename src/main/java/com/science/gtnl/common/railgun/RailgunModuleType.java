package com.science.gtnl.common.railgun;

import net.minecraft.item.ItemStack;

public enum RailgunModuleType {

    COMPUTE(2),
    ACCELERATION(2),
    RANGE(2),
    EHV_BEAM(1),
    ENERGY_T1(1),
    ENERGY_T2(1),
    ENERGY_T3(1);

    public final int limit;

    RailgunModuleType(int limit) {
        this.limit = limit;
    }

    public boolean energy() {
        return ordinal() >= ENERGY_T1.ordinal();
    }

    public static RailgunModuleType of(ItemStack stack) {
        if (stack == null || !"item.gtnl.railgun_module".equals(
            stack.getItem()
                .getUnlocalizedName()))
            return null;
        int meta = stack.getItemDamage();
        return meta >= 0 && meta < values().length ? values()[meta] : null;
    }
}
