package com.science.gtnl.mixins.late.inventoryBogoSorter;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.cleanroommc.bogosorter.ShortcutHandler;
import com.cleanroommc.bogosorter.mixins.early.minecraft.SlotAccessor;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.api.IInfinitySlot;

@Mixin(value = ShortcutHandler.class, remap = false)
public class MixinShortcutHandler {

    @ModifyArg(
        method = "moveItemStack(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/inventory/Container;Lcom/cleanroommc/bogosorter/mixins/early/minecraft/SlotAccessor;Z I)V",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"),
        index = 0)
    private static int modifyMoveAmount(int originalAmount, @Local(name = "slot") SlotAccessor slot,
        @Local(name = "stack") ItemStack stack) {
        if (slot instanceof IInfinitySlot) {
            return Math.min(originalAmount, Integer.MAX_VALUE - stack.stackSize);
        }
        return originalAmount;
    }

    @ModifyArg(
        method = "insert(Lcom/cleanroommc/bogosorter/mixins/early/minecraft/SlotAccessor;Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/item/ItemStack;",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 0),
        index = 1)
    private static int modifyAmountEmpty(int originalAmount, @Local(name = "slot") SlotAccessor slot,
        @Local(name = "stack") ItemStack stack, @Local(name = "stackInSlot") ItemStack stackInSlot) {
        if (slot instanceof IInfinitySlot) {
            return Integer.MAX_VALUE;
        }
        return Math.min(stack.getMaxStackSize(), slot.callGetSlotStackLimit());
    }

    @ModifyArg(
        method = "insert(Lcom/cleanroommc/bogosorter/mixins/early/minecraft/SlotAccessor;Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/item/ItemStack;",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 1),
        index = 1)
    private static int modifyAmount1(int originalAmount, @Local(name = "slot") SlotAccessor slot,
        @Local(name = "stack") ItemStack stack, @Local(name = "stackInSlot") ItemStack stackInSlot) {
        if (slot instanceof IInfinitySlot) {
            return Math.min(stack.stackSize, Integer.MAX_VALUE - stackInSlot.stackSize);
        }
        return originalAmount;
    }

    @ModifyArg(
        method = "insert(Lcom/cleanroommc/bogosorter/mixins/early/minecraft/SlotAccessor;Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/item/ItemStack;",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 2),
        index = 0)
    private static int modifyAmount2(int originalAmount, @Local(name = "slot") SlotAccessor slot,
        @Local(name = "stack") ItemStack stack, @Local(name = "stackInSlot") ItemStack stackInSlot) {
        if (slot instanceof IInfinitySlot) {
            return Math.min(stack.stackSize, Integer.MAX_VALUE - stackInSlot.stackSize);
        }
        return originalAmount;
    }
}
