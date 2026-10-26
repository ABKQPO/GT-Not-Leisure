package com.science.gtnl.compat.ae2fc;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class FluidPacketContainer {

    private FluidPacketContainer() {}

    public static int fill(ItemStack container, FluidStack existing, FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) return 0;

        if (!doFill) {
            if (existing == null) return resource.amount;
            if (!existing.isFluidEqual(resource)) return 0;
            return Math.min(Integer.MAX_VALUE - existing.amount, resource.amount);
        }

        if (container.stackTagCompound == null) container.setTagCompound(new NBTTagCompound());

        NBTTagCompound tag = container.getTagCompound();
        NBTTagCompound fluidTag = existing != null ? existing.writeToNBT(new NBTTagCompound()) : new NBTTagCompound();

        if (existing == null) {
            FluidStack copy = resource.copy();
            copy.amount = resource.amount;
            tag.setTag("FluidStack", copy.writeToNBT(new NBTTagCompound()));
            return copy.amount;
        }

        if (!existing.isFluidEqual(resource)) return 0;

        int fillable = Integer.MAX_VALUE - existing.amount;
        int toFill = Math.min(fillable, resource.amount);
        existing.amount += toFill;
        tag.setTag("FluidStack", existing.writeToNBT(fluidTag));
        return toFill;
    }

    public static FluidStack drain(ItemStack container, FluidStack existing, int maxDrain, boolean doDrain) {
        if (existing == null || maxDrain <= 0) return null;

        FluidStack drained = existing.copy();
        drained.amount = Math.min(maxDrain, existing.amount);

        if (doDrain) {
            existing.amount -= drained.amount;
            if (existing.amount <= 0) {
                container.getTagCompound()
                    .removeTag("FluidStack");
                if (container.getTagCompound()
                    .hasNoTags()) container.setTagCompound(null);
            } else {
                container.getTagCompound()
                    .setTag("FluidStack", existing.writeToNBT(new NBTTagCompound()));
            }
        }

        return drained;
    }
}
