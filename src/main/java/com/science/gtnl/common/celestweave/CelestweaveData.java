package com.science.gtnl.common.celestweave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.google.common.collect.MapMaker;
import com.science.gtnl.common.item.items.CelestweaveModuleItem;

public final class CelestweaveData {

    public static final String ROOT = "Celestweave";
    public static final String MODULES = "Modules";
    public static final String DISABLED = "Disabled";
    public static final String HIDDEN = "Hidden";
    public static final String COSMETIC = "Cosmetic";
    public static final String HELD = "Held";
    public static final String CFG = "Cfg";
    public static final int MODULE_SLOTS = 8;

    private static final Map<NBTTagList, Set<CelestweaveModule>> INSTALLED = new MapMaker().weakKeys()
        .makeMap();
    private static final Map<NBTTagList, Map<CelestweaveModule, ItemStack>> HELD_ITEMS = new MapMaker().weakKeys()
        .makeMap();

    private CelestweaveData() {}

    public static NBTTagCompound root(ItemStack stack) {
        if (stack == null) return new NBTTagCompound();
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey(ROOT, 10) ? tag.getCompoundTag(ROOT) : new NBTTagCompound();
    }

    public static NBTTagCompound rootForWrite(ItemStack stack) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey(ROOT, 10)) tag.setTag(ROOT, new NBTTagCompound());
        return tag.getCompoundTag(ROOT);
    }

    public static ItemStack[] moduleStacks(ItemStack stack) {
        ItemStack[] stacks = new ItemStack[MODULE_SLOTS];
        NBTTagList list = root(stack).getTagList(MODULES, 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            int slot = entry.getByte("Slot");
            if (slot >= 0 && slot < MODULE_SLOTS) stacks[slot] = ItemStack.loadItemStackFromNBT(entry);
        }
        return stacks;
    }

    public static void writeModuleStacks(ItemStack stack, ItemStack[] stacks) {
        NBTTagList list = new NBTTagList();
        for (int slot = 0; slot < Math.min(MODULE_SLOTS, stacks.length); slot++) {
            if (stacks[slot] == null) continue;
            NBTTagCompound entry = new NBTTagCompound();
            stacks[slot].writeToNBT(entry);
            entry.setByte("Slot", (byte) slot);
            list.appendTag(entry);
        }
        rootForWrite(stack).setTag(MODULES, list);
    }

    public static Set<CelestweaveModule> installed(ItemStack stack) {
        if (stack == null) return Collections.emptySet();
        NBTTagCompound root = root(stack);
        if (!root.hasKey(MODULES, 9)) return Collections.emptySet();
        NBTTagList list = root.getTagList(MODULES, 10);
        Set<CelestweaveModule> cached = INSTALLED.get(list);
        if (cached != null) return cached;
        Set<CelestweaveModule> set = EnumSet.noneOf(CelestweaveModule.class);
        boolean energy = false;
        for (ItemStack module : moduleStacks(stack)) {
            CelestweaveModule type = CelestweaveModule.of(module);
            if (type == null || set.contains(type)) continue;
            if (type.group == CelestweaveModule.Group.ENERGY) {
                if (energy) continue;
                energy = true;
            }
            set.add(type);
        }
        Set<CelestweaveModule> result = Collections.unmodifiableSet(set);
        INSTALLED.put(list, result);
        return result;
    }

    public static Set<CelestweaveModule> activeModules(ItemStack stack) {
        Set<CelestweaveModule> installed = installed(stack);
        if (installed.isEmpty()) return installed;
        Set<String> disabled = idList(stack, DISABLED);
        if (disabled.isEmpty()) return installed;
        Set<CelestweaveModule> set = EnumSet.noneOf(CelestweaveModule.class);
        for (CelestweaveModule module : installed) if (!disabled.contains(module.id)) set.add(module);
        return set;
    }

    public static ItemStack held(ItemStack stack, CelestweaveModule module) {
        if (stack == null) return null;
        NBTTagCompound root = root(stack);
        if (!root.hasKey(MODULES, 9)) return null;
        NBTTagList list = root.getTagList(MODULES, 10);
        Map<CelestweaveModule, ItemStack> cached = HELD_ITEMS.get(list);
        if (cached == null) {
            cached = new EnumMap<>(CelestweaveModule.class);
            for (ItemStack entry : moduleStacks(stack)) {
                CelestweaveModule type = CelestweaveModule.of(entry);
                if (type == null || !type.holdsItem || cached.containsKey(type)) continue;
                ItemStack held = heldOf(entry);
                if (held != null) cached.put(type, held);
            }
            HELD_ITEMS.put(list, cached);
        }
        return cached.get(module);
    }

    public static ItemStack heldOf(ItemStack moduleStack) {
        if (moduleStack == null || !moduleStack.hasTagCompound()
            || !moduleStack.getTagCompound()
                .hasKey(HELD, 10))
            return null;
        return ItemStack.loadItemStackFromNBT(
            moduleStack.getTagCompound()
                .getCompoundTag(HELD));
    }

    public static ItemStack withHeld(ItemStack moduleStack, ItemStack held) {
        if (!moduleStack.hasTagCompound()) moduleStack.setTagCompound(new NBTTagCompound());
        if (held == null) moduleStack.getTagCompound()
            .removeTag(HELD);
        else moduleStack.getTagCompound()
            .setTag(HELD, held.writeToNBT(new NBTTagCompound()));
        return moduleStack;
    }

    public static NBTTagCompound cfg(ItemStack stack, CelestweaveModule module) {
        return root(stack).getCompoundTag(CFG)
            .getCompoundTag(module.id);
    }

    public static NBTTagCompound cfgForWrite(ItemStack stack, CelestweaveModule module) {
        NBTTagCompound root = rootForWrite(stack);
        if (!root.hasKey(CFG, 10)) root.setTag(CFG, new NBTTagCompound());
        NBTTagCompound all = root.getCompoundTag(CFG);
        if (!all.hasKey(module.id, 10)) all.setTag(module.id, new NBTTagCompound());
        return all.getCompoundTag(module.id);
    }

    public static int cfgInt(ItemStack stack, CelestweaveModule module, String key, int fallback, int min, int max) {
        NBTTagCompound cfg = cfg(stack, module);
        return cfg.hasKey(key, 99) ? Math.max(min, Math.min(max, cfg.getInteger(key))) : fallback;
    }

    public static boolean cfgBool(ItemStack stack, CelestweaveModule module, String key, boolean fallback) {
        NBTTagCompound cfg = cfg(stack, module);
        return cfg.hasKey(key, 99) ? cfg.getBoolean(key) : fallback;
    }

    public static Set<String> idList(ItemStack stack, String key) {
        NBTTagList list = root(stack).getTagList(key, 8);
        if (list.tagCount() == 0) return Collections.emptySet();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < list.tagCount(); i++) set.add(list.getStringTagAt(i));
        return set;
    }

    public static void writeIdList(ItemStack stack, String key, Set<String> ids) {
        NBTTagList list = new NBTTagList();
        for (String id : ids) list.appendTag(new NBTTagString(id));
        rootForWrite(stack).setTag(key, list);
    }

    public static void setFlag(ItemStack stack, String key, CelestweaveModule module, boolean present) {
        Set<String> ids = new HashSet<>(idList(stack, key));
        if (present ? ids.add(module.id) : ids.remove(module.id)) writeIdList(stack, key, ids);
    }

    public static boolean active(ItemStack stack, CelestweaveModule module) {
        return installed(stack).contains(module) && !idList(stack, DISABLED).contains(module.id);
    }

    public static boolean shown(ItemStack stack, CelestweaveModule module) {
        return installed(stack).contains(module) && (!module.hideable() || !idList(stack, HIDDEN).contains(module.id));
    }

    public static int energyTier(ItemStack stack) {
        int tier = 0;
        for (CelestweaveModule module : installed(stack)) tier = Math.max(tier, module.energyTier());
        return tier;
    }

    public static CelestweaveCosmetic cosmetic(ItemStack stack) {
        NBTTagCompound root = root(stack);
        return root.hasKey(COSMETIC, 10) ? CelestweaveCosmetic.read(root.getCompoundTag(COSMETIC))
            : CelestweaveCosmetic.DEFAULT;
    }

    public static void writeCosmetic(ItemStack stack, CelestweaveCosmetic cosmetic) {
        rootForWrite(stack).setTag(COSMETIC, cosmetic.write());
    }

    public static ItemStack[] stacksOf(List<ItemStack> modules) {
        ItemStack[] stacks = new ItemStack[MODULE_SLOTS];
        for (int i = 0; i < Math.min(MODULE_SLOTS, modules.size()); i++) stacks[i] = modules.get(i);
        return stacks;
    }

    public static List<ItemStack> presetModules(int armorType, CelestweaveModuleItem item) {
        List<ItemStack> list = new ArrayList<>();
        list.add(item.stack(CelestweaveModule.ENERGY_T3));
        switch (armorType) {
            case 0 -> {
                list.add(withHeld(item.stack(CelestweaveModule.REVEALING), CelestweavePresets.revealingItem()));
                list.add(withHeld(item.stack(CelestweaveModule.HOLO), CelestweavePresets.holoItem()));
                list.add(item.stack(CelestweaveModule.NIGHT_VISION));
            }
            case 1 -> {
                list.add(item.stack(CelestweaveModule.SHIELD));
                list.add(withHeld(item.stack(CelestweaveModule.BACKPACK), CelestweavePresets.backpackItem()));
            }
            case 2 -> list.add(item.stack(CelestweaveModule.FLIGHT));
            case 3 -> {
                list.add(item.stack(CelestweaveModule.DASH));
                list.add(item.stack(CelestweaveModule.WALK_ASSIST));
            }
            default -> {}
        }
        return list;
    }
}
