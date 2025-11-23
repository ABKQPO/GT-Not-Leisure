package com.science.gtnl.utils;

import com.science.gtnl.utils.gui.ContainerDirePatternEncoder;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;

public class DireCraftingPatternDetails implements ICraftingPatternDetails {

    private final ItemStack pattern;
    private final IAEItemStack[] inputs;
    private final IAEItemStack[] condensedInputs;
    private final IAEItemStack[] output;

    public DireCraftingPatternDetails(ItemStack is) {
        pattern = is;

        if (is.hasTagCompound()) {
            inputs = new IAEItemStack[81];
            output = new IAEItemStack[1];
            var list = new ItemList();
            var tag = is.getTagCompound();
            var in = ((GTNLNBTTagList) tag.getTagList("in", Constants.NBT.TAG_COMPOUND)).snl$getNbtList();
            for (var i = 0; i < in.length; i++) {
                var item = fromTagCreateAEItem((NBTTagCompound) in[i]);
                inputs[i] = item;
                list.addStorage(item);
            }
            output[0] = fromTagCreateAEItem(tag.getCompoundTag("out"));

            this.condensedInputs = list.toArray(new IAEItemStack[0]);
        } else {
            throw new IllegalArgumentException("No pattern here!");
        }
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
        return output;
    }

    @Override
    public IAEItemStack[] getOutputs() {
        return output;
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
