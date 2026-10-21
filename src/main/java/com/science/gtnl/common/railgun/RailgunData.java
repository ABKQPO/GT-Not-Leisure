package com.science.gtnl.common.railgun;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.MapMaker;
import com.gtnllib.utils.MathUtils;
import com.science.gtnl.common.equipment.EquipmentModules;
import com.science.gtnl.common.equipment.EquipmentTag;
import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;
import com.science.gtnl.config.MainConfig;

public class RailgunData {

    public static final String ROOT = "Railgun";
    public static final String MODULES = "Modules";
    public static final int MODULE_SLOTS = RailgunModule.values().length + EquipmentModules.ENERGY_SLOTS;
    public static final int FULL_CHARGE_UNITS = RailgunChargeTier.EHV3.chargeTicks;
    public static final double RATE_EPSILON = 1.0E-6;
    public static final int CHARGE_RESUME_GRACE_TICKS = 6;
    public static final int BEAM_RANGE = 64;
    public static final EquipmentModules.Storage MODULE_STORAGE = EquipmentModules
        .storage(RailgunData::moduleList, RailgunData::moduleStacks, RailgunData::writeModuleStacks);
    private static final Modules NO_MODULES = new Modules(Collections.emptyMap(), 0);
    private static final Map<NBTTagList, Modules> CACHE = new MapMaker().weakKeys()
        .makeMap();

    private RailgunData() {}

    public static boolean holding(EntityPlayer player) {
        return player != null && ElectromagneticRailgun.is(player.getHeldItem());
    }

    public static double chargeRate(ItemStack stack) {
        double bonus = MathUtils.clampUnit(MainConfig.item.railgun.accelerationBonus);
        return 1 + bonus * modules(stack).count(RailgunModule.ACCELERATION);
    }

    public static int units(long ticks, double rate) {
        return (int) Math.floor(Math.max(0, ticks) * rate + RATE_EPSILON);
    }

    public static NBTTagCompound root(ItemStack stack) {
        return EquipmentTag.child(stack, ROOT);
    }

    public static NBTTagCompound rootForWrite(ItemStack stack) {
        return EquipmentTag.childForWrite(stack, ROOT);
    }

    public static NBTTagList moduleList(ItemStack stack) {
        return root(stack).getTagList(MODULES, Constants.NBT.TAG_COMPOUND);
    }

    public static ItemStack[] moduleStacks(ItemStack stack) {
        return EquipmentModules.read(moduleList(stack), MODULE_SLOTS);
    }

    public static void writeModuleStacks(ItemStack stack, ItemStack[] slots) {
        rootForWrite(stack).setTag(MODULES, EquipmentModules.write(slots));
    }

    public static Modules modules(ItemStack stack) {
        NBTTagCompound root = root(stack);
        if (!root.hasKey(MODULES, Constants.NBT.TAG_LIST)) return NO_MODULES;
        NBTTagList list = root.getTagList(MODULES, Constants.NBT.TAG_COMPOUND);
        Modules cached = CACHE.get(list);
        if (cached != null) return cached;
        ItemStack[] stacks = moduleStacks(stack);
        Map<RailgunModule, Integer> counts = new EnumMap<>(RailgunModule.class);
        for (ItemStack module : stacks) {
            RailgunModule type = RailgunModule.of(module);
            if (type != null) counts.put(type, Math.min(type.limit, counts.getOrDefault(type, 0) + module.stackSize));
        }
        Modules result = new Modules(Collections.unmodifiableMap(counts), EquipmentModules.energyTier(stacks));
        CACHE.put(list, result);
        return result;
    }

    public record Modules(Map<RailgunModule, Integer> counts, int energyTier) {

        public int count(RailgunModule type) {
            return counts.getOrDefault(type, 0);
        }

        public int total() {
            int total = 0;
            for (int count : counts.values()) total += count;
            return total;
        }
    }
}
