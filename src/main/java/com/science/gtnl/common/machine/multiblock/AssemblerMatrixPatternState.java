package com.science.gtnl.common.machine.multiblock;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.github.bsideup.jabel.Desugar;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.utils.DireCraftingPatternDetails;
import com.science.gtnl.utils.LargeInventoryCrafting;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

public class AssemblerMatrixPatternState {

    private static final String INVALID_PATTERN_TAG = "InvalidPattern";

    private final Map<ItemStack, DireCraftingPatternDetails> patterns = new Reference2ObjectOpenHashMap<>();
    private final Set<IAEItemStack> possibleOutputs = new ObjectOpenHashSet<>();
    private int patternMultiply = 1;

    @Desugar
    public record AcceptedBatch(List<IAEItemStack> containerReturns, List<IAEItemStack> outputs) {}

    public Map<ItemStack, DireCraftingPatternDetails> getPatterns() {
        return Collections.unmodifiableMap(patterns);
    }

    public Set<IAEItemStack> getPossibleOutputs() {
        return Collections.unmodifiableSet(possibleOutputs);
    }

    public int getPatternMultiply() {
        return patternMultiply;
    }

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
        DireCraftingPatternDetails details = decodePattern(
            newStack,
            machine.getBaseMetaTileEntity()
                .getWorld());
        if (details != null) {
            details.setMultiply(patternMultiply);
            addPattern(newStack, details);
            changed = true;
        }
        return changed;
    }

    public static DireCraftingPatternDetails decodePattern(ItemStack stack, World world) {
        if (world == null || stack == null || !(stack.getItem() instanceof ICraftingPatternItem patternItem)) {
            return null;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) tag.removeTag(INVALID_PATTERN_TAG);

        ICraftingPatternDetails pattern = patternItem.getPatternForItem(stack, world);
        if (pattern != null && pattern.isCraftable()) {
            pattern = new DireCraftingPatternDetails(pattern);
        }
        return pattern instanceof DireCraftingPatternDetails details ? details : null;
    }

    public AcceptedBatch prepareBatch(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
        if (!(patternDetails instanceof DireCraftingPatternDetails direPattern)) {
            return null;
        }

        if (!(table instanceof LargeInventoryCrafting largeInventory)) {
            ScienceNotLeisure.LOG.error("Assembler Matrix received a crafting inventory without long batch metadata");
            return null;
        }
        long assemblerSize = largeInventory.getAssemblerSize();
        if (assemblerSize < 1) {
            ScienceNotLeisure.LOG.error("Assembler Matrix received an invalid crafting batch size: {}", assemblerSize);
            return null;
        }

        ObjectArrayList<IAEItemStack> batchInputs = new ObjectArrayList<>();
        ObjectArrayList<IAEItemStack> batchOutputs = new ObjectArrayList<>();
        IAEItemStack[] patternInputs = direPattern.getInputs();
        try {
            for (int slot = 0; slot < table.getSizeInventory(); slot++) {
                ItemStack stack = table.getStackInSlot(slot);
                if (stack != null) {
                    ItemStack containerItem = AssemblerMatrix.resolveContainerItem(stack);
                    if (containerItem != null) {
                        IAEItemStack patternInput = slot < patternInputs.length ? patternInputs[slot] : null;
                        long containerAmount = patternInput == null ? assemblerSize
                            : multiplyStackSize(patternInput.getStackSize(), assemblerSize);
                        batchInputs.add(
                            AEItemStack.create(containerItem)
                                .setStackSize(containerAmount));
                    }
                }
            }
            for (IAEItemStack output : patternDetails.getCondensedOutputs()) {
                if (output == null) continue;
                batchOutputs.add(
                    output.copy()
                        .setStackSize(multiplyStackSize(output.getStackSize(), assemblerSize)));
            }
        } catch (ArithmeticException exception) {
            ScienceNotLeisure.LOG.error("Assembler Matrix stack overflowed after an accepted AE batch plan", exception);
            return null;
        }

        for (int slot = 0; slot < table.getSizeInventory(); slot++) {
            ItemStack stack = table.getStackInSlot(slot);
            if (stack != null) stack.stackSize = 1;
        }
        return new AcceptedBatch(batchInputs, batchOutputs);
    }

    public void clearPatternData() {
        patterns.clear();
        possibleOutputs.clear();
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
        if (stackSize <= 0 || multiplier <= 0) {
            throw new ArithmeticException("Assembler Matrix stack quantities must be positive");
        }
        return Math.multiplyExact(stackSize, multiplier);
    }
}
