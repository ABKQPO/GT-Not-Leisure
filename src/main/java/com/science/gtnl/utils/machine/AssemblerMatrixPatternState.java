package com.science.gtnl.utils.machine;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.utils.DireCraftingPatternDetails;
import com.science.gtnl.utils.LargeInventoryCrafting;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
public class AssemblerMatrixPatternState {

    private final Map<ItemStack, DireCraftingPatternDetails> patterns = new Reference2ObjectOpenHashMap<>();
    private final Set<IAEItemStack> possibleOutputs = new ObjectOpenHashSet<>();
    private final Queue<IAEItemStack> outputs = new ArrayDeque<>();
    private final Queue<IAEItemStack> inputs = new ArrayDeque<>();
    @Setter
    private IAEItemStack[] cachedOutputItems;
    private int patternMultiply = 1;

    public void setPatternMultiply(int patternMultiply) {
        this.patternMultiply = Math.max(1, patternMultiply);
        for (DireCraftingPatternDetails pattern : patterns.values()) {
            pattern.setMultiply(this.patternMultiply);
        }
        rebuildPossibleOutputs();
    }

    public void addPattern(ItemStack stack, DireCraftingPatternDetails details) {
        DireCraftingPatternDetails previous = patterns.put(stack, details);
        if (previous != null) {
            rebuildPossibleOutputs();
        } else {
            addPossibleOutputs(details);
        }
    }

    public boolean onPatternInventoryChanged(AssemblerMatrix machine, ItemStack removedStack, ItemStack newStack) {
        boolean changed = false;
        if (removedStack != null) {
            DireCraftingPatternDetails removedPattern = patterns.remove(removedStack);
            if (removedPattern != null) {
                rebuildPossibleOutputs();
            }
            changed = true;
        }
        if (newStack != null && newStack.getItem() instanceof ICraftingPatternItem craftingPatternItem) {
            ICraftingPatternDetails pattern = craftingPatternItem.getPatternForItem(
                newStack,
                machine.getBaseMetaTileEntity()
                    .getWorld());
            if (pattern != null && pattern.isCraftable()) {
                pattern = new DireCraftingPatternDetails(pattern);
            }
            if (pattern instanceof DireCraftingPatternDetails details) {
                details.setMultiply(patternMultiply);
                addPattern(newStack, details);
                changed = true;
            }
        }
        return changed;
    }

    public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
        if (!(patternDetails instanceof DireCraftingPatternDetails direPattern)) {
            return false;
        }

        long assemblerSize = Math.max(1, ((LargeInventoryCrafting) table).getAssemblerSize());
        IAEItemStack[] patternInputs = direPattern.getInputs();
        for (int slot = 0; slot < table.getSizeInventory(); slot++) {
            ItemStack stack = table.getStackInSlot(slot);
            if (stack != null) {
                ItemStack containerItem = AssemblerMatrix.resolveContainerItem(stack);
                if (containerItem != null) {
                    IAEItemStack patternInput = slot < patternInputs.length ? patternInputs[slot] : null;
                    long containerAmount = patternInput == null ? assemblerSize
                        : multiplyStackSize(patternInput.getStackSize(), assemblerSize);
                    inputs.add(
                        AEItemStack.create(containerItem)
                            .setStackSize(containerAmount));
                }
                stack.stackSize = 1;
            }
        }
        for (IAEItemStack output : patternDetails.getCondensedOutputs()) {
            if (output == null) continue;
            outputs.add(
                output.copy()
                    .setStackSize(multiplyStackSize(output.getStackSize(), assemblerSize)));
        }
        return true;
    }

    public void clearPatternData() {
        patterns.clear();
        possibleOutputs.clear();
    }

    public void clearRuntimeData() {
        outputs.clear();
        inputs.clear();
        cachedOutputItems = new IAEItemStack[0];
    }

    private void addPossibleOutputs(DireCraftingPatternDetails details) {
        for (IAEItemStack output : details.getCondensedOutputs()) {
            if (output != null) {
                possibleOutputs.add(output);
            }
        }
    }

    private void rebuildPossibleOutputs() {
        possibleOutputs.clear();
        for (DireCraftingPatternDetails details : patterns.values()) {
            addPossibleOutputs(details);
        }
    }

    private static long multiplyStackSize(long stackSize, long multiplier) {
        if (stackSize <= 0 || multiplier <= 0) return 0;
        if (stackSize > Long.MAX_VALUE / multiplier) return Long.MAX_VALUE;
        return stackSize * multiplier;
    }
}
