package com.science.gtnl.common.celestweave;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import com.science.gtnl.common.item.items.CelestweaveArmor;
import com.science.gtnl.common.item.items.CelestweaveModuleItem;

import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;

public enum CelestweaveModule {

    APIARIST(0, Mask.ANY),
    NIGHT_VISION(1, Mask.HEAD, () -> new CelestweaveBehaviors.NightVision()),
    SATURATION(2, Mask.HEAD, () -> new CelestweaveBehaviors.Saturation()),
    REVEALING(3, Mask.HEAD, Trait.HIDEABLE | Trait.BUILT_IN,
        stack -> stack.getItem() instanceof IGoggles || stack.getItem() instanceof IRevealer, null),
    HOLO(4, Mask.HEAD, Trait.HIDEABLE | Trait.BUILT_IN, stack -> CelestweaveCompat.isHoloGlasses(stack), null),
    SHIELD(5, Mask.CHEST, () -> new CelestweaveBehaviors.Shield()),
    REFLECT(6, Mask.CHEST, () -> new CelestweaveBehaviors.Reflect()),
    PURIFICATION(7, Mask.CHEST, () -> new CelestweaveBehaviors.Purification()),
    HAZARD(8, Mask.CHEST),
    SOULBOUND(9, Mask.CHEST),
    REACH(10, Mask.CHEST),
    TERRASTEEL(11, Mask.CHEST, () -> new CelestweaveBehaviors.Terrasteel()),
    BACKPACK(12, Mask.CHEST, Trait.HIDEABLE, stack -> CelestweaveBackpack.accepts(stack), null),
    FLIGHT(13, Mask.LEGS, Trait.HIDEABLE, null, () -> new CelestweaveFlight()),
    WALK_ASSIST(14, Mask.BOOTS, () -> new CelestweaveBehaviors.WalkAssist()),
    DASH(15, Mask.BOOTS),
    DIG_AFFINITY(16, Mask.BOOTS);

    public static final class Mask {

        public static final int HEAD = 1, CHEST = 2, LEGS = 4, BOOTS = 8, ANY = 15;

        private Mask() {}
    }

    public static final class Trait {

        public static final int HIDEABLE = 1, BUILT_IN = 2;

        private Trait() {}
    }

    private static final CelestweaveModule[] BY_META;

    static {
        int count = 0;
        for (CelestweaveModule module : values()) count = Math.max(count, module.meta + 1);
        BY_META = new CelestweaveModule[count];
        for (CelestweaveModule module : values()) BY_META[module.meta] = module;
    }

    public final String id;
    public final int meta, slots;
    public final boolean holdsItem, builtIn;
    private final int traits;
    private final Predicate<ItemStack> holds;
    private final Supplier<CelestweaveBehavior> behavior;

    CelestweaveModule(int meta, int slots) {
        this(meta, slots, 0, null, null);
    }

    CelestweaveModule(int meta, int slots, Supplier<CelestweaveBehavior> behavior) {
        this(meta, slots, 0, null, behavior);
    }

    CelestweaveModule(int meta, int slots, int traits, Predicate<ItemStack> holds,
        Supplier<CelestweaveBehavior> behavior) {
        this.id = name().toLowerCase(Locale.ROOT);
        this.meta = meta;
        this.slots = slots;
        this.traits = traits;
        this.holds = holds;
        this.behavior = behavior;
        this.holdsItem = holds != null;
        this.builtIn = (traits & Trait.BUILT_IN) != 0;
    }

    public boolean fits(int piece) {
        return piece >= 0 && piece < CelestweaveArmor.PIECES && (slots & (1 << piece)) != 0;
    }

    public boolean hideable() {
        return (traits & Trait.HIDEABLE) != 0;
    }

    public boolean accepts(ItemStack stack) {
        if (holds == null || stack == null
            || stack.getItem() == null
            || stack.getItem() instanceof CelestweaveArmor
            || stack.getItem() instanceof CelestweaveModuleItem) return false;
        return holds.test(stack);
    }

    CelestweaveBehavior newBehavior() {
        return behavior == null ? null : behavior.get();
    }

    public static CelestweaveModule of(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof CelestweaveModuleItem)) return null;
        CelestweaveModule module = byMeta(stack.getItemDamage());
        return module == null || module.builtIn ? null : module;
    }

    public static CelestweaveModule byMeta(int meta) {
        return meta >= 0 && meta < BY_META.length ? BY_META[meta] : null;
    }

    public static int metaCount() {
        return BY_META.length;
    }

    public static CelestweaveModule byId(String id) {
        for (CelestweaveModule module : values()) if (module.id.equals(id)) return module;
        return null;
    }
}
