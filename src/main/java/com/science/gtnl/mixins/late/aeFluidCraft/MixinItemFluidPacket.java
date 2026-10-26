package com.science.gtnl.mixins.late.aeFluidCraft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.glodblock.github.common.item.ItemFluidPacket;
import com.science.gtnl.compat.ae2fc.FluidPacketContainer;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(value = ItemFluidPacket.class, remap = false)
public abstract class MixinItemFluidPacket implements IFluidContainerItem {

    @Shadow
    public static FluidStack getFluidStack(ItemStack stack) {
        throw new IllegalStateException("Mixin not applied");
    }

    @Override
    public FluidStack getFluid(ItemStack container) {
        return getFluidStack(container);
    }

    @Override
    public int getCapacity(ItemStack container) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        return FluidPacketContainer.fill(container, getFluid(container), resource, doFill);
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        return FluidPacketContainer.drain(container, getFluid(container), maxDrain, doDrain);
    }
}
