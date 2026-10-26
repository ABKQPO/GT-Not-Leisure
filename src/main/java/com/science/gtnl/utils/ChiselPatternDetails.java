package com.science.gtnl.utils;

import java.util.Collection;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.github.bsideup.jabel.Desugar;
import com.science.gtnl.utils.machine.MachineUtils;

import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;

@Desugar
public class ChiselPatternDetails implements ICraftingPatternDetails {

    // 我无法理解这个样板的作用，但是我写了，就这样
    private final ItemStack stack;
    private final IAEItemStack[] inputs, outputs;

    private ChiselPatternDetails(@NotNull IAEItemStack input, @NotNull IAEItemStack output) {
        this.inputs = new IAEItemStack[] { input };
        this.outputs = new IAEItemStack[] { output };

        stack = AEApi.instance()
            .definitions()
            .items()
            .encodedPattern()
            .maybeStack(1)
            .orNull();
        final NBTTagCompound encodedValue = new NBTTagCompound();

        final var in = input.getItemStack()
            .writeToNBT(new NBTTagCompound());
        in.setInteger("Count", 1);
        final var out = output.getItemStack()
            .writeToNBT(new NBTTagCompound());
        out.setInteger("Count", 1);

        final NBTTagList tagIn = new NBTTagList();
        tagIn.appendTag(in);
        final NBTTagList tagOut = new NBTTagList();
        tagOut.appendTag(out);

        encodedValue.setTag("in", tagIn);
        encodedValue.setTag("out", tagOut);
        encodedValue.setBoolean("crafting", false);
        encodedValue.setBoolean("substitute", false);
        encodedValue.setBoolean("beSubstitute", false);
        stack.setTagCompound(encodedValue);
    }

    public static boolean addChiselPatterns(@Nullable IAEItemStack input, @Nullable Collection<ItemStack> outputs,
        @NotNull Collection<ChiselPatternDetails> patterns, int parallel) {
        if (input == null) return false;
        if (outputs == null || outputs.isEmpty()) return false;
        for (var itemStack : outputs) {
            var out = AEItemStack.create(itemStack);
            if (out != null && !MachineUtils.equlsItemStackAndAEItemStack(input, itemStack)) {
                patterns.add(
                    new ChiselPatternDetails(
                        input.copy()
                            .setStackSize(parallel),
                        out.setStackSize(parallel)));
            }
        }
        return true;
    }

    public void setParallel(int parallel) {
        inputs[0].setStackSize(parallel);
        outputs[0].setStackSize(parallel);
    }

    @Override
    public ItemStack getPattern() {
        return stack;
    }

    @Override
    public boolean isValidItemForSlot(int i, ItemStack itemStack, World world) {
        throw new IllegalStateException("Only crafting recipes supported.");
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
        return inputs;
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
    public boolean canBeSubstitute() {
        return false;
    }

    @Override
    public ItemStack getOutput(InventoryCrafting inventoryCrafting, World world) {
        throw new IllegalStateException("Only crafting recipes supported.");
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void setPriority(int i) {

    }
}
