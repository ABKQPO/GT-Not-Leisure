package com.science.gtnl.mixins.late.appliedEnergistics;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.science.gtnl.common.yogsothoth.YogTerminalService;

import appeng.util.Platform;

@Mixin(value = Platform.class, remap = false)
public abstract class MixinPlatformYogTerminal {

    @ModifyVariable(
        method = "openGUI(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraftforge/common/util/ForgeDirection;Lappeng/core/sync/GuiBridge;I)V",
        at = @At("HEAD"),
        argsOnly = true)
    private static int gtnl$openStoredTerminalInPlace(int slotIndex) {
        return YogTerminalService.redirectSlot(slotIndex);
    }
}
