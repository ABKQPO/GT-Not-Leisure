package com.science.gtnl.mixins.late.inventoryBogoSorter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.cleanroommc.bogosorter.common.sort.SortHandler;
import com.cleanroommc.bogosorter.mixins.early.minecraft.SlotAccessor;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.api.IInfinitySlot;

@Mixin(value = SortHandler.class, remap = false)
public class MixinSortHandler {

    @ModifyArg(method = "sortHorizontal", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), index = 0)
    private int modifyMoveAmount1(int originalAmount, @Local(name = "slot") SlotAccessor slot) {
        if (slot instanceof IInfinitySlot) {
            return Integer.MAX_VALUE;
        }
        return originalAmount;
    }

    @ModifyArg(method = "sortHorizontal", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"), index = 1)
    private int modifyMoveAmount2(int originalAmount, @Local(name = "slot") SlotAccessor slot) {
        if (slot instanceof IInfinitySlot) {
            return Integer.MAX_VALUE;
        }
        return originalAmount;
    }
}
