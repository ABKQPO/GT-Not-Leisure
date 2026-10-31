package com.science.gtnl.common.celestweave;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import com.google.common.collect.MapMaker;
import com.science.gtnl.common.equipment.EquipmentModules;
import com.science.gtnl.common.equipment.EquipmentTag;
import com.science.gtnl.common.item.items.armor.CelestweaveArmor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class CelestweaveData {

    public static final String ROOT = "Celestweave";
    public static final String MODULES = "Modules";
    public static final String DISABLED = "Disabled";
    public static final String HIDDEN = "Hidden";
    public static final String COSMETIC = "Cosmetic";
    public static final String HELD = "Held";
    public static final String SETTINGS_TAG = "Settings";
    public static final EquipmentModules.Storage MODULE_STORAGE = EquipmentModules
        .storage(CelestweaveData::moduleList, CelestweaveData::moduleStacks, CelestweaveData::writeModuleStacks);

    private static int[] slotCounts;
    private static final Map<NBTTagList, Set<CelestweaveModule>> INSTALLED = new MapMaker().weakKeys()
        .makeMap();
    private static final Map<NBTTagList, Map<CelestweaveModule, ItemStack>> HELD_ITEMS = new MapMaker().weakKeys()
        .makeMap();
    private static final Map<NBTTagList, Integer> ENERGY_TIERS = new MapMaker().weakKeys()
        .makeMap();
    private static final Map<NBTTagList, Set<String>> ID_LISTS = new MapMaker().weakKeys()
        .makeMap();
    private static final Map<NBTTagList, Active> ACTIVE = new MapMaker().weakKeys()
        .makeMap();

    private static final Map<NBTTagCompound, ItemStack> BUILT_IN_HELD = new MapMaker().weakKeys()
        .makeMap();
    private static final List<Set<CelestweaveModule>> BUILT_IN = new ObjectArrayList<>();

    static {
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) {
            Set<CelestweaveModule> set = EnumSet.noneOf(CelestweaveModule.class);
            for (CelestweaveModule module : CelestweaveModule.values())
                if (module.builtIn && module.fits(piece)) set.add(module);
            BUILT_IN.add(Collections.unmodifiableSet(set));
        }
    }

    private record Active(Set<CelestweaveModule> installed, Set<CelestweaveModule> set) {}

    public static NBTTagCompound root(ItemStack stack) {
        return EquipmentTag.child(stack, ROOT);
    }

    public static NBTTagCompound rootForWrite(ItemStack stack) {
        return EquipmentTag.childForWrite(stack, ROOT);
    }

    public static ItemStack piece(EntityLivingBase entity, int piece) {
        ItemStack stack = entity.getEquipmentInSlot(armorIndex(piece) + 1);
        return stack != null && stack.getItem() instanceof CelestweaveArmor armor && armor.armorType == piece ? stack
            : null;
    }

    public static int armorIndex(int piece) {
        return CelestweaveArmor.PIECES - 1 - piece;
    }

    public static boolean isArmorSlot(EntityPlayer player, int slot) {
        return slot >= player.inventory.mainInventory.length;
    }

    public static boolean wearsAny(EntityLivingBase entity) {
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) if (piece(entity, piece) != null) return true;
        return false;
    }

    public static NBTTagList moduleList(ItemStack stack) {
        return root(stack).getTagList(MODULES, Constants.NBT.TAG_COMPOUND);
    }

    public static int moduleSlots(int piece) {
        if (slotCounts == null) {
            int[] counts = new int[CelestweaveArmor.PIECES];
            for (int part = 0; part < counts.length; part++) {
                counts[part] = EquipmentModules.ENERGY_SLOTS;
                for (CelestweaveModule module : CelestweaveModule.values())
                    if (module.fits(part) && !module.builtIn) counts[part]++;
            }
            slotCounts = counts;
        }
        return slotCounts[piece];
    }

    public static int moduleSlots(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof CelestweaveArmor armor) return moduleSlots(armor.armorType);
        int most = 0;
        for (int piece = 0; piece < CelestweaveArmor.PIECES; piece++) most = Math.max(most, moduleSlots(piece));
        return most;
    }

    public static ItemStack[] moduleStacks(ItemStack stack) {
        return EquipmentModules.read(moduleList(stack), moduleSlots(stack));
    }

    public static void writeModuleStacks(ItemStack stack, ItemStack[] stacks) {
        rootForWrite(stack).setTag(MODULES, EquipmentModules.write(stacks));
    }

    public static Set<CelestweaveModule> installed(ItemStack stack) {
        if (stack == null) return Collections.emptySet();
        NBTTagCompound root = root(stack);
        if (!root.hasKey(MODULES, Constants.NBT.TAG_LIST)) return builtIn(stack);
        NBTTagList list = root.getTagList(MODULES, Constants.NBT.TAG_COMPOUND);
        Set<CelestweaveModule> cached = INSTALLED.get(list);
        if (cached != null) return cached;
        Set<CelestweaveModule> set = EnumSet.noneOf(CelestweaveModule.class);
        set.addAll(builtIn(stack));
        for (ItemStack module : moduleStacks(stack)) {
            CelestweaveModule type = CelestweaveModule.of(module);
            if (type != null) set.add(type);
        }
        Set<CelestweaveModule> result = Collections.unmodifiableSet(set);
        INSTALLED.put(list, result);
        return result;
    }

    public static Set<CelestweaveModule> builtIn(ItemStack stack) {
        return stack != null && stack.getItem() instanceof CelestweaveArmor armor ? builtIn(armor.armorType)
            : Collections.emptySet();
    }

    public static Set<CelestweaveModule> builtIn(int piece) {
        return piece >= 0 && piece < BUILT_IN.size() ? BUILT_IN.get(piece) : Collections.emptySet();
    }

    public static Set<CelestweaveModule> activeModules(ItemStack stack) {
        Set<CelestweaveModule> installed = installed(stack);
        if (installed.isEmpty()) return installed;
        NBTTagCompound root = root(stack);
        NBTTagList disabledList = root.getTagList(DISABLED, Constants.NBT.TAG_STRING);
        if (disabledList.tagCount() == 0) return installed;
        Active cached = ACTIVE.get(disabledList);
        if (cached != null && cached.installed() == installed) return cached.set();
        Set<String> disabled = idList(stack, DISABLED);
        Set<CelestweaveModule> set = EnumSet.noneOf(CelestweaveModule.class);
        for (CelestweaveModule module : installed) if (!disabled.contains(module.id)) set.add(module);
        Set<CelestweaveModule> result = Collections.unmodifiableSet(set);
        ACTIVE.put(disabledList, new Active(installed, result));
        return result;
    }

    public static ItemStack held(ItemStack stack, CelestweaveModule module) {
        if (stack == null) return null;
        if (module.builtIn) return builtInHeld(stack, module);
        NBTTagCompound root = root(stack);
        if (!root.hasKey(MODULES, Constants.NBT.TAG_LIST)) return null;
        NBTTagList list = root.getTagList(MODULES, Constants.NBT.TAG_COMPOUND);
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

    private static ItemStack builtInHeld(ItemStack stack, CelestweaveModule module) {
        NBTTagCompound root = root(stack);
        if (!root.hasKey(HELD, Constants.NBT.TAG_COMPOUND)) return null;
        NBTTagCompound all = root.getCompoundTag(HELD);
        if (!all.hasKey(module.id, Constants.NBT.TAG_COMPOUND)) return null;
        NBTTagCompound tag = all.getCompoundTag(module.id);
        ItemStack cached = BUILT_IN_HELD.get(tag);
        if (cached != null) return cached;
        ItemStack held = ItemStack.loadItemStackFromNBT((NBTTagCompound) tag.copy());
        if (held != null) BUILT_IN_HELD.put(tag, held);
        return held;
    }

    public static void writeHeld(ItemStack stack, CelestweaveModule module, ItemStack held) {
        NBTTagCompound root = rootForWrite(stack);
        if (!root.hasKey(HELD, Constants.NBT.TAG_COMPOUND)) root.setTag(HELD, new NBTTagCompound());
        NBTTagCompound all = root.getCompoundTag(HELD);
        if (held == null) all.removeTag(module.id);
        else all.setTag(
            module.id,
            held.copy()
                .writeToNBT(new NBTTagCompound()));
    }

    public static ItemStack heldOf(ItemStack moduleStack) {
        if (moduleStack == null || !moduleStack.hasTagCompound()
            || !moduleStack.getTagCompound()
                .hasKey(HELD, Constants.NBT.TAG_COMPOUND))
            return null;
        return ItemStack.loadItemStackFromNBT(
            (NBTTagCompound) moduleStack.getTagCompound()
                .getCompoundTag(HELD)
                .copy());
    }

    public static ItemStack withHeld(ItemStack moduleStack, ItemStack held) {
        if (!moduleStack.hasTagCompound()) moduleStack.setTagCompound(new NBTTagCompound());
        if (held == null) moduleStack.getTagCompound()
            .removeTag(HELD);
        else moduleStack.getTagCompound()
            .setTag(
                HELD,
                held.copy()
                    .writeToNBT(new NBTTagCompound()));
        return moduleStack;
    }

    public static NBTTagCompound settings(ItemStack stack, CelestweaveModule module) {
        return root(stack).getCompoundTag(SETTINGS_TAG)
            .getCompoundTag(module.id);
    }

    public static NBTTagCompound settingsForWrite(ItemStack stack, CelestweaveModule module) {
        NBTTagCompound root = rootForWrite(stack);
        if (!root.hasKey(SETTINGS_TAG, Constants.NBT.TAG_COMPOUND)) root.setTag(SETTINGS_TAG, new NBTTagCompound());
        NBTTagCompound all = root.getCompoundTag(SETTINGS_TAG);
        if (!all.hasKey(module.id, Constants.NBT.TAG_COMPOUND)) all.setTag(module.id, new NBTTagCompound());
        return all.getCompoundTag(module.id);
    }

    public static Set<String> idList(ItemStack stack, String key) {
        NBTTagList list = root(stack).getTagList(key, Constants.NBT.TAG_STRING);
        if (list.tagCount() == 0) return Collections.emptySet();
        Set<String> cached = ID_LISTS.get(list);
        if (cached != null) return cached;
        Set<String> set = new ObjectOpenHashSet<>();
        for (int i = 0; i < list.tagCount(); i++) set.add(list.getStringTagAt(i));
        Set<String> result = Collections.unmodifiableSet(set);
        ID_LISTS.put(list, result);
        return result;
    }

    public static void writeIdList(ItemStack stack, String key, Set<String> ids) {
        NBTTagList list = new NBTTagList();
        for (String id : ids) list.appendTag(new NBTTagString(id));
        rootForWrite(stack).setTag(key, list);
    }

    public static void writeFlag(ItemStack stack, String key, CelestweaveModule module, boolean present) {
        Set<String> ids = new ObjectOpenHashSet<>(idList(stack, key));
        if (present ? ids.add(module.id) : ids.remove(module.id)) writeIdList(stack, key, ids);
    }

    public static boolean active(ItemStack stack, CelestweaveModule module) {
        return installed(stack).contains(module) && !idList(stack, DISABLED).contains(module.id);
    }

    public static boolean shown(ItemStack stack, CelestweaveModule module) {
        return installed(stack).contains(module) && (!module.hideable() || !idList(stack, HIDDEN).contains(module.id));
    }

    public static int energyTier(ItemStack stack) {
        if (stack == null) return 0;
        NBTTagCompound root = root(stack);
        if (!root.hasKey(MODULES, Constants.NBT.TAG_LIST)) return 0;
        NBTTagList list = root.getTagList(MODULES, Constants.NBT.TAG_COMPOUND);
        Integer cached = ENERGY_TIERS.get(list);
        if (cached != null) return cached;
        int tier = EquipmentModules.energyTier(moduleStacks(stack));
        ENERGY_TIERS.put(list, tier);
        return tier;
    }

    public static CelestweaveCosmetic cosmetic(ItemStack stack) {
        NBTTagCompound root = root(stack);
        return root.hasKey(COSMETIC, Constants.NBT.TAG_COMPOUND)
            ? CelestweaveCosmetic.read(root.getCompoundTag(COSMETIC))
            : CelestweaveCosmetic.DEFAULT;
    }

    public static void writeCosmetic(ItemStack stack, CelestweaveCosmetic cosmetic) {
        rootForWrite(stack).setTag(COSMETIC, cosmetic.write());
    }

    public static boolean setEnabled(ItemStack stack, CelestweaveModule module, boolean enabled) {
        if (!hasInstalled(stack, module)) return false;
        writeFlag(stack, DISABLED, module, !enabled);
        return true;
    }

    public static boolean setShown(ItemStack stack, CelestweaveModule module, boolean shown) {
        if (!hasInstalled(stack, module) || !module.hideable()) return false;
        writeFlag(stack, HIDDEN, module, !shown);
        return true;
    }

    public static boolean setValue(ItemStack stack, CelestweaveModuleSetting setting, float value) {
        if (!Float.isFinite(value) || !hasInstalled(stack, setting.module)) return false;
        setting.write(stack, value);
        return true;
    }

    public static boolean setCosmetic(ItemStack stack, CelestweaveCosmeticOption option, float value) {
        if (stack == null || !Float.isFinite(value)) return false;
        writeCosmetic(stack, cosmetic(stack).with(option, value));
        return true;
    }

    private static boolean hasInstalled(ItemStack stack, CelestweaveModule module) {
        return stack != null && module != null && installed(stack).contains(module);
    }
}
