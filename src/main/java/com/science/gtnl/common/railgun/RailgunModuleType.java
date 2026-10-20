package com.science.gtnl.common.railgun;

import java.util.Locale;

import net.minecraft.item.ItemStack;

import com.science.gtnl.common.equipment.IEquipmentVariant;
import com.science.gtnl.common.equipment.MetaLookup;
import com.science.gtnl.common.item.items.equipment.RailgunModuleItem;

public enum RailgunModuleType implements IEquipmentVariant {

    COMPUTE(0, 2),
    ACCELERATION(1, 2),
    RANGE(2, 2),
    EHV_BEAM(3, 1);

    public static final MetaLookup<RailgunModuleType> LOOKUP = new MetaLookup<>(values());

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

    @Override
    public String id() {
        return id;
    }

    @Override
    public int meta() {
        return meta;
    }

    public static RailgunModuleType byMeta(int meta) {
        return LOOKUP.byMeta(meta);
    }

    public static int metaCount() {
        return LOOKUP.size();
    }
}
