package com.science.gtnl.utils.machine;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.utils.DireCraftingPatternDetails;
import com.science.gtnl.utils.LargeInventoryCrafting;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

@Getter
public class AssemblerMatrixPatternState {

    private static final String INVALID_PATTERN_TAG = "InvalidPattern";

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

    public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
        if (!(patternDetails instanceof DireCraftingPatternDetails direPattern)) {
            return false;
        }

        if (!(table instanceof LargeInventoryCrafting largeInventory)) {
            ScienceNotLeisure.LOG.error("Assembler Matrix received a crafting inventory without long batch metadata");
            return false;
        }
        long assemblerSize = largeInventory.getAssemblerSize();
        if (assemblerSize < 1) {
            ScienceNotLeisure.LOG.error("Assembler Matrix received an invalid crafting batch size: {}", assemblerSize);
            return false;
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
            return false;
        }

        for (int slot = 0; slot < table.getSizeInventory(); slot++) {
            ItemStack stack = table.getStackInSlot(slot);
            if (stack != null) stack.stackSize = 1;
        }
        inputs.addAll(batchInputs);
        outputs.addAll(batchOutputs);
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
        if (stackSize <= 0 || multiplier <= 0) {
            throw new ArithmeticException("Assembler Matrix stack quantities must be positive");
        }
        return Math.multiplyExact(stackSize, multiplier);
    }
}
