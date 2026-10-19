package com.science.gtnl.common.celestweave;

import java.util.Locale;

import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.items.CelestweaveArmor;
import com.science.gtnl.common.item.items.CelestweaveModuleItem;

import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;

public enum CelestweaveModule {

    ENERGY_T1(Mask.ANY, Group.ENERGY, false),
    ENERGY_T2(Mask.ANY, Group.ENERGY, false),
    ENERGY_T3(Mask.ANY, Group.ENERGY, false),
    APIARIST(Mask.ANY, Group.NONE, false),
    NIGHT_VISION(Mask.HEAD, Group.NONE, false),
    SATURATION(Mask.HEAD, Group.NONE, false),
    REVEALING(Mask.HEAD, Group.NONE, true),
    HOLO(Mask.HEAD, Group.NONE, true),
    SHIELD(Mask.CHEST, Group.NONE, false),
    REFLECT(Mask.CHEST, Group.NONE, false),
    PURIFICATION(Mask.CHEST, Group.NONE, false),
    HAZARD(Mask.CHEST, Group.NONE, false),
    SOULBOUND(Mask.CHEST, Group.NONE, false),
    REACH(Mask.CHEST, Group.NONE, false),
    TERRASTEEL(Mask.CHEST, Group.NONE, false),
    BACKPACK(Mask.CHEST, Group.NONE, true),
    FLIGHT(Mask.LEGS, Group.NONE, false),
    WALK_ASSIST(Mask.BOOTS, Group.NONE, false),
    DASH(Mask.BOOTS, Group.NONE, false),
    DIG_AFFINITY(Mask.BOOTS, Group.NONE, false);

    public enum Group {
        NONE,
        ENERGY
    }

    public static final class Mask {

        public static final int HEAD = 1, CHEST = 2, LEGS = 4, BOOTS = 8, ANY = 15;

        private Mask() {}
    }

    public final String id;
    public final int slots;
    public final Group group;
    public final boolean holdsItem;

    CelestweaveModule(int slots, Group group, boolean holdsItem) {
        this.id = name().toLowerCase(Locale.ROOT);
        this.slots = slots;
        this.group = group;
        this.holdsItem = holdsItem;
    }

    public boolean fits(int armorType) {
        return armorType >= 0 && armorType < 4 && (slots & (1 << armorType)) != 0;
    }

    public boolean toggleable() {
        return group != Group.ENERGY;
    }

    public boolean hideable() {
        return switch (this) {
            case REVEALING, HOLO, BACKPACK, FLIGHT -> true;
            default -> false;
        };
    }

    public int energyTier() {
        return switch (this) {
            case ENERGY_T1 -> 1;
            case ENERGY_T2 -> 2;
            case ENERGY_T3 -> 3;
            default -> 0;
        };
    }

    public boolean accepts(ItemStack stack) {
        if (stack == null || stack.getItem() == null
            || stack.getItem() instanceof CelestweaveArmor
            || stack.getItem() instanceof CelestweaveModuleItem) return false;
        return switch (this) {
            case REVEALING -> stack.getItem() instanceof IGoggles || stack.getItem() instanceof IRevealer;
            case HOLO -> CelestweaveCompat.isHoloGlasses(stack);
            case BACKPACK -> CelestweaveBackpack.accepts(stack);
            default -> false;
        };
    }

    public static CelestweaveModule of(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof CelestweaveModuleItem)) return null;
        int meta = stack.getItemDamage();
        return meta >= 0 && meta < values().length ? values()[meta] : null;
    }

    public static CelestweaveModule byId(String id) {
        for (CelestweaveModule module : values()) if (module.id.equals(id)) return module;
        return null;
    }
}
