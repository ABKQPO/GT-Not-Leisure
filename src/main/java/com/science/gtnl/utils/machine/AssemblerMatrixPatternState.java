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

public class AssemblerMatrixPatternState {

    private final Map<ItemStack, DireCraftingPatternDetails> patterns = new Reference2ObjectOpenHashMap<>();
    private final Set<IAEItemStack> possibleOutputs = new ObjectOpenHashSet<>();
    private final Queue<IAEItemStack> outputs = new ArrayDeque<>();
    private final Queue<IAEItemStack> inputs = new ArrayDeque<>();
    private IAEItemStack[] cachedOutputItems;
    private int patternMultiply = 1;

    public Map<ItemStack, DireCraftingPatternDetails> getPatterns() {
        return patterns;
    }

    public Set<IAEItemStack> getPossibleOutputs() {
        return possibleOutputs;
    }

    public Queue<IAEItemStack> getOutputs() {
        return outputs;
    }

    public Queue<IAEItemStack> getInputs() {
        return inputs;
    }

    public IAEItemStack[] getCachedOutputItems() {
        return cachedOutputItems;
    }

    public void setCachedOutputItems(IAEItemStack[] cachedOutputItems) {
        this.cachedOutputItems = cachedOutputItems;
    }

    public int getPatternMultiply() {
        return patternMultiply;
    }

    public void setPatternMultiply(int patternMultiply) {
        this.patternMultiply = Math.max(1, patternMultiply);
        for (DireCraftingPatternDetails pattern : patterns.values()) {
            pattern.setMultiply(this.patternMultiply);
        }
    }

    public boolean onPatternInventoryChanged(AssemblerMatrix machine, ItemStack removedStack, ItemStack newStack) {
        boolean changed = false;
        if (removedStack != null) {
            DireCraftingPatternDetails removedPattern = patterns.remove(removedStack);
            if (removedPattern != null) {
                possibleOutputs.remove(removedPattern.getCondensedOutputs()[0]);
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
                patterns.put(newStack, details);
                possibleOutputs.add(details.getCondensedOutputs()[0]);
                changed = true;
            }
        }
        return changed;
    }

    public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
        if (!(patternDetails instanceof DireCraftingPatternDetails direPattern)) {
            return false;
        }

        IAEItemStack output = patternDetails.getCondensedOutputs()[0];
        long assemblerSize = ((LargeInventoryCrafting) table).getAssemblerSize();
        for (int slot = 0; slot < table.getSizeInventory(); slot++) {
            ItemStack stack = table.getStackInSlot(slot);
            if (stack != null) {
                ItemStack containerItem = AssemblerMatrix.resolveContainerItem(stack);
                if (containerItem != null) {
                    inputs.add(
                        AEItemStack.create(containerItem)
                            .setStackSize(assemblerSize * direPattern.getMultiply()));
                }
                stack.stackSize = 1;
            }
        }
        outputs.add(
            output.copy()
                .setStackSize(output.getStackSize() * assemblerSize));
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
}
