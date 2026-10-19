package com.science.gtnl.common.railgun;

import java.util.Locale;

import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.items.RailgunModuleItem;

public enum RailgunModuleType {

    COMPUTE(0, 2),
    ACCELERATION(1, 2),
    RANGE(2, 2),
    EHV_BEAM(3, 1);

    private static final RailgunModuleType[] BY_META;

    static {
        int count = 0;
        for (RailgunModuleType type : values()) count = Math.max(count, type.meta + 1);
        BY_META = new RailgunModuleType[count];
        for (RailgunModuleType type : values()) BY_META[type.meta] = type;
    }

    public final String id;
    public final int meta, limit;

    RailgunModuleType(int meta, int limit) {
        this.id = name().toLowerCase(Locale.ROOT);
        this.meta = meta;
        this.limit = limit;
    }

    public static RailgunModuleType of(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof RailgunModuleItem)) return null;
        return byMeta(stack.getItemDamage());
    }

    public static RailgunModuleType byMeta(int meta) {
        return meta >= 0 && meta < BY_META.length ? BY_META[meta] : null;
    }

    public static int metaCount() {
        return BY_META.length;
    }
}
