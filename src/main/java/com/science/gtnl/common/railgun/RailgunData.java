package com.science.gtnl.common.railgun;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.collect.MapMaker;
import com.science.gtnl.common.equipment.EquipmentModules;
import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;
import com.science.gtnl.config.MainConfig;

public class RailgunData {

    public static final String MODULES = "Modules";
    public static final int MODULE_SLOTS = 8;
    public static final int FULL_CHARGE_UNITS = RailgunChargeTier.EHV3.chargeTicks;
    public static final double RATE_EPSILON = 1.0E-6;
    private static final Modules NO_MODULES = new Modules(Collections.emptyMap(), 0);
    private static final Map<NBTTagList, Modules> CACHE = new MapMaker().weakKeys()
        .makeMap();

    private RailgunData() {}

    public static boolean soundEnabled(EntityPlayer player) {
        ItemStack stack = player == null ? null : player.getHeldItem();
        return ElectromagneticRailgun.is(stack) && RailgunSetting.SOUND.active(stack);
    }

    public static double chargeRate(ItemStack stack) {
        double bonus = Math.max(0, Math.min(1, MainConfig.item.railgun.accelerationBonus));
        return 1 + bonus * modules(stack).count(RailgunModuleType.ACCELERATION);
    }

    public static int units(long ticks, double rate) {
        return (int) Math.floor(Math.max(0, ticks) * rate + RATE_EPSILON);
    }

    public static NBTTagCompound rootForWrite(ItemStack stack) {
        if (stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    public static NBTTagList moduleList(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound()
            .getTagList(MODULES, 10) : new NBTTagList();
    }

    public static ItemStack[] moduleStacks(ItemStack stack) {
        return EquipmentModules.read(moduleList(stack), MODULE_SLOTS);
    }

    public static void writeModuleStacks(ItemStack stack, ItemStack[] slots) {
        rootForWrite(stack).setTag(MODULES, EquipmentModules.write(slots, MODULE_SLOTS));
    }

    public static Modules modules(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey(MODULES, 9)) return NO_MODULES;
        NBTTagList list = tag.getTagList(MODULES, 10);
        Modules cached = CACHE.get(list);
        if (cached != null) return cached;
        ItemStack[] stacks = moduleStacks(stack);
        Map<RailgunModuleType, Integer> counts = new EnumMap<>(RailgunModuleType.class);
        for (ItemStack module : stacks) {
            RailgunModuleType type = RailgunModuleType.of(module);
            if (type != null) counts.put(type, Math.min(type.limit, counts.getOrDefault(type, 0) + 1));
        }
        Modules result = new Modules(Collections.unmodifiableMap(counts), EquipmentModules.energyTier(stacks));
        CACHE.put(list, result);
        return result;
    }

    public record Modules(Map<RailgunModuleType, Integer> counts, int energyTier) {

        public int count(RailgunModuleType type) {
            return counts.getOrDefault(type, 0);
        }

        public int total() {
            int total = 0;
            for (int count : counts.values()) total += count;
            return total;
        }
    }
}
