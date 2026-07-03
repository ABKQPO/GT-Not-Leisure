package com.science.gtnl.utils;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.science.gtnl.container.ContainerDirePatternEncoder;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;
import lombok.Getter;

public class DireCraftingPatternDetails implements ICraftingPatternDetails {

    private final ItemStack pattern;
    private final IAEItemStack[] inputs;
    private IAEItemStack[] condensedInputs;
    private final long[] baseInputSizes;
    private final IAEItemStack[] outputs;
    private final long[] baseOutputSizes;
    @Getter
    private int multiply = 1;

    public DireCraftingPatternDetails(ItemStack is) {
        pattern = is;

        if (is.hasTagCompound()) {
            inputs = new IAEItemStack[81];
            baseInputSizes = new long[inputs.length];
            outputs = new IAEItemStack[1];
            baseOutputSizes = new long[outputs.length];
            var list = new ItemList();
            var tag = is.getTagCompound();
            var in = tag.getTagList("in", Constants.NBT.TAG_COMPOUND);
            for (var i = 0; i < in.tagCount(); i++) {
                var item = fromTagCreateAEItem(in.getCompoundTagAt(i));
                inputs[i] = item;
                if (item != null) {
                    baseInputSizes[i] = item.getStackSize();
                    list.addStorage(item);
                }
            }
            outputs[0] = fromTagCreateAEItem(tag.getCompoundTag("out"));
            if (outputs[0] != null) {
                baseOutputSizes[0] = outputs[0].getStackSize();
            }

            this.condensedInputs = list.toArray(new IAEItemStack[0]);
        } else {
            throw new IllegalArgumentException("No pattern here!");
        }
    }

    public DireCraftingPatternDetails(ICraftingPatternDetails is) {
        pattern = is.getPattern()
            .copy();
        inputs = copyStacks(is.getInputs());
        baseInputSizes = captureStackSizes(inputs);
        var list = new ItemList();
        for (IAEItemStack ii : inputs) {
            if (ii == null) continue;
            list.addStorage(ii);
        }
        condensedInputs = list.toArray(new IAEItemStack[0]);
        outputs = copyStacks(is.getCondensedOutputs());
        baseOutputSizes = captureStackSizes(outputs);
    }

    public void setMultiply(int multiply) {
        this.multiply = Math.max(1, multiply);
        for (var i = 0; i < inputs.length; i++) {
            var input = inputs[i];
            if (input == null) continue;
            input.setStackSize(multiplyStackSize(baseInputSizes[i], this.multiply));
        }
        var list = new ItemList();
        for (var i = 0; i < inputs.length; i++) {
            var ii = inputs[i];
            if (ii == null) continue;
            inputs[i] = ii.copy();
            list.addStorage(ii);
        }
        condensedInputs = list.toArray(new IAEItemStack[0]);
        for (var i = 0; i < outputs.length; i++) {
            var output = outputs[i];
            if (output == null) continue;
            output.setStackSize(multiplyStackSize(baseOutputSizes[i], this.multiply));
        }
    }

    private static IAEItemStack[] copyStacks(IAEItemStack[] stacks) {
        IAEItemStack[] copy = stacks.clone();
        for (var i = 0; i < copy.length; i++) {
            if (copy[i] != null) {
                copy[i] = copy[i].copy();
            }
        }
        return copy;
    }

    private static long[] captureStackSizes(IAEItemStack[] stacks) {
        long[] sizes = new long[stacks.length];
        for (var i = 0; i < stacks.length; i++) {
            if (stacks[i] != null) {
                sizes[i] = stacks[i].getStackSize();
            }
        }
        return sizes;
    }

    private static long multiplyStackSize(long stackSize, int multiplier) {
        if (stackSize <= 0) return stackSize;
        if (stackSize > Long.MAX_VALUE / multiplier) return Long.MAX_VALUE;
        return stackSize * multiplier;
    }

    private static AEItemStack fromTagCreateAEItem(final NBTTagCompound i) {
        if (ContainerDirePatternEncoder.empty.equals(i)) return null;
        return AEItemStack.create(Platform.loadItemStackFromNBT(i));
    }

    @Override
    public ItemStack getPattern() {
        return pattern;
    }

    @Override
    public boolean isValidItemForSlot(int slotIndex, ItemStack itemStack, World world) {
        return false;
    }

    @Override
    public boolean isCraftable() {
        return false;
    }

    @Override
    public IAEItemStack[] getInputs() {
        return inputs;
    }

    @Override
    public IAEItemStack[] getCondensedInputs() {
        return condensedInputs;
    }

    @Override
    public IAEItemStack[] getCondensedOutputs() {
        return outputs;
    }

    @Override
    public IAEItemStack[] getOutputs() {
        return outputs;
    }

    @Override
    public boolean canSubstitute() {
        return false;
    }

    @Override
    public ItemStack getOutput(InventoryCrafting craftingInv, World world) {
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void setPriority(int priority) {

    }
}
