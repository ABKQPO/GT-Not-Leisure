package com.science.gtnl.common.celestweave;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import com.science.gtnl.common.equipment.IEquipmentVariant;
import com.science.gtnl.common.equipment.MetaLookup;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;
import com.science.gtnl.common.item.items.equipment.CelestweaveModuleItem;

import thaumcraft.api.IGoggles;
import thaumcraft.api.nodes.IRevealer;

public enum CelestweaveModule implements IEquipmentVariant {

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

    public static class Mask {

        public static final int HEAD = 1, CHEST = 2, LEGS = 4, BOOTS = 8, ANY = 15;

        private Mask() {}
    }

    public static class Trait {

        public static final int HIDEABLE = 1, BUILT_IN = 2;

        private Trait() {}
    }

    public static final MetaLookup<CelestweaveModule> LOOKUP = new MetaLookup<>(values());

    public final String id;
    public final int meta, pieceMask;
    public final boolean holdsItem, builtIn;
    private final int traits;
    private final Predicate<ItemStack> holds;
    private final Supplier<ICelestweaveBehavior> behavior;

    CelestweaveModule(int meta, int pieceMask) {
        this(meta, pieceMask, 0, null, null);
    }

    CelestweaveModule(int meta, int pieceMask, Supplier<ICelestweaveBehavior> behavior) {
        this(meta, pieceMask, 0, null, behavior);
    }

    CelestweaveModule(int meta, int pieceMask, int traits, Predicate<ItemStack> holds,
        Supplier<ICelestweaveBehavior> behavior) {
        this.id = name().toLowerCase(Locale.ROOT);
        this.meta = meta;
        this.pieceMask = pieceMask;
        this.traits = traits;
        this.holds = holds;
        this.behavior = behavior;
        this.holdsItem = holds != null;
        this.builtIn = (traits & Trait.BUILT_IN) != 0;
    }

    public boolean fits(int piece) {
        return piece >= 0 && piece < CelestweaveArmor.PIECES && (pieceMask & (1 << piece)) != 0;
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

    ICelestweaveBehavior newBehavior() {
        return behavior == null ? null : behavior.get();
    }

    public static CelestweaveModule of(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof CelestweaveModuleItem)) return null;
        CelestweaveModule module = byMeta(stack.getItemDamage());
        return module == null || module.builtIn ? null : module;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public int meta() {
        return meta;
    }

    public static CelestweaveModule byMeta(int meta) {
        return LOOKUP.byMeta(meta);
    }

    public static int metaCount() {
        return LOOKUP.size();
    }

    public static CelestweaveModule byId(String id) {
        for (CelestweaveModule module : values()) if (module.id.equals(id)) return module;
        return null;
    }
}
