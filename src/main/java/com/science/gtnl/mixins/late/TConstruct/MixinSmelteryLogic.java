package com.science.gtnl.mixins.late.TConstruct;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.block.blocks.BlockSearedLadder;

import tconstruct.smeltery.logic.SmelteryLogic;

@Mixin(value = SmelteryLogic.class, remap = false)
public class MixinSmelteryLogic {

    @Inject(method = "validBlockID", at = @At("HEAD"), cancellable = true)
    public void injectValidBlockID(Block blockID, CallbackInfoReturnable<Boolean> cir) {
        if (blockID instanceof BlockSearedLadder) cir.setReturnValue(true);
    }
}
