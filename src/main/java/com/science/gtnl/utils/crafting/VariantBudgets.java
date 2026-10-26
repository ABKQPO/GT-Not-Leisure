package com.science.gtnl.utils.crafting;

import java.util.List;
import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.Nullable;

import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.util.inv.MEInventoryCrafting;
import appeng.util.item.AEItemStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public final class VariantBudgets {

    private final Map<IAEItemStack, Map<IAEStack<?>, IItemList<IAEStack<?>>>> budgets = new Object2ObjectOpenHashMap<>();

    public boolean isEmpty() {
        return this.budgets.isEmpty();
    }

    public void clear() {
        this.budgets.clear();
    }

    public void add(final ICraftingPatternDetails details, final IAEStack<?> input, final List<IAEStack<?>> variants) {
        final IAEItemStack pattern = patternKey(details);
        if (pattern == null) return;
        add(pattern, input, variants);
    }

    private void add(final IAEItemStack pattern, final IAEStack<?> input, final Iterable<IAEStack<?>> variants) {
        final IItemList<IAEStack<?>> budget = this.budgets
            .computeIfAbsent(pattern, k -> new Object2ObjectOpenHashMap<>())
            .computeIfAbsent(
                input.copy()
                    .setStackSize(1),
                k -> AEApi.instance()
                    .storage()
                    .createAEStackList());
        for (IAEStack<?> stack : variants) budget.add(stack.copy());
    }

    public void addAll(final VariantBudgets other) {
        for (Map.Entry<IAEItemStack, Map<IAEStack<?>, IItemList<IAEStack<?>>>> pattern : other.budgets.entrySet()) {
            for (Map.Entry<IAEStack<?>, IItemList<IAEStack<?>>> input : pattern.getValue()
                .entrySet()) {
                add(pattern.getKey(), input.getKey(), input.getValue());
            }
        }
    }

    public boolean has(final ICraftingPatternDetails details) {
        if (this.budgets.isEmpty()) return false;
        final IAEItemStack pattern = patternKey(details);
        return pattern != null && this.budgets.containsKey(pattern);
    }

    @Nullable
    public IItemList<IAEStack<?>> get(final ICraftingPatternDetails details, final IAEStack<?> input) {
        if (this.budgets.isEmpty()) return null;
        final IAEItemStack pattern = patternKey(details);
        final Map<IAEStack<?>, IItemList<IAEStack<?>>> inputs = pattern == null ? null : this.budgets.get(pattern);
        return inputs == null ? null : inputs.get(input);
    }

    public static long left(final IItemList<IAEStack<?>> budget, final IAEStack<?> variant) {
        final IAEStack<?> left = findPrecise(budget, variant);
        return left == null ? 0 : left.getStackSize();
    }

    public static long total(final IItemList<IAEStack<?>> budget) {
        long total = 0;
        for (IAEStack<?> stack : budget) {
            total += Math.max(0, stack.getStackSize());
        }
        return total;
    }

    public void consume(final ICraftingPatternDetails details, final InventoryCrafting table,
        final List<IAEStack<?>> inputs) {
        if (this.budgets.isEmpty() || !(table instanceof MEInventoryCrafting crafting) || inputs == null) return;
        for (int slot = 0; slot < crafting.getSizeInventory() && slot < inputs.size(); slot++) {
            final IAEStack<?> used = crafting.getAEStackInSlot(slot);
            final IAEStack<?> input = inputs.get(slot);
            if (used == null || input == null) continue;
            final IItemList<IAEStack<?>> budget = get(details, input);
            if (budget == null) continue;
            final IAEStack<?> left = findPrecise(budget, used);
            if (left != null) left.setStackSize(Math.max(0, left.getStackSize() - used.getStackSize()));
        }
    }

    public NBTTagList writeToNBT() {
        final NBTTagList list = new NBTTagList();
        for (Map.Entry<IAEItemStack, Map<IAEStack<?>, IItemList<IAEStack<?>>>> pattern : this.budgets.entrySet()) {
            for (Map.Entry<IAEStack<?>, IItemList<IAEStack<?>>> input : pattern.getValue()
                .entrySet()) {
                final NBTTagCompound tag = new NBTTagCompound();
                tag.setTag(
                    "pattern",
                    pattern.getKey()
                        .getItemStack()
                        .writeToNBT(new NBTTagCompound()));
                tag.setTag(
                    "input",
                    input.getKey()
                        .toNBTGeneric());
                final NBTTagList variants = new NBTTagList();
                for (IAEStack<?> stack : input.getValue()) {
                    if (stack.getStackSize() > 0) variants.appendTag(stack.toNBTGeneric());
                }
                tag.setTag("variants", variants);
                list.appendTag(tag);
            }
        }
        return list;
    }

    public void readFromNBT(final NBTTagList list) {
        this.budgets.clear();
        for (int i = 0; i < list.tagCount(); i++) {
            final NBTTagCompound tag = list.getCompoundTagAt(i);
            final ItemStack pattern = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("pattern"));
            final IAEStack<?> input = IAEStack.fromNBTGeneric(tag.getCompoundTag("input"));
            if (pattern == null || input == null) continue;
            final NBTTagList variants = tag.getTagList("variants", Constants.NBT.TAG_COMPOUND);
            final List<IAEStack<?>> stacks = new ObjectArrayList<>();
            for (int v = 0; v < variants.tagCount(); v++) {
                final IAEStack<?> stack = IAEStack.fromNBTGeneric(variants.getCompoundTagAt(v));
                if (stack != null) stacks.add(stack);
            }
            add(AEItemStack.create(pattern), input, stacks);
        }
    }

    @Nullable
    private static IAEItemStack patternKey(final ICraftingPatternDetails details) {
        final ItemStack pattern = details.getPattern();
        return pattern == null ? null : AEItemStack.create(pattern);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Nullable
    private static IAEStack<?> findPrecise(final IItemList<IAEStack<?>> list, final IAEStack<?> stack) {
        return (IAEStack<?>) ((IItemList) list).findPrecise((IAEStack) stack);
    }
}
