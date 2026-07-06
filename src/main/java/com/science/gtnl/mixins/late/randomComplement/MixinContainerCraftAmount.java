package com.science.gtnl.mixins.late.randomComplement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.utils.RCAEBaseContainer;

import appeng.container.implementations.ContainerCraftAmount;

@Mixin(value = ContainerCraftAmount.class, remap = false)
public abstract class MixinContainerCraftAmount implements RCAEBaseContainer {

    @Inject(method = "openConfirmationGUI", at = @At("TAIL"))
    public void openConfirmationGUI(EntityPlayer player, TileEntity te, CallbackInfo ci) {
        if (player.openContainer instanceof RCAEBaseContainer c) {
            c.rc$setOldContainer(this.rc$getOldContainer());
        }
    }

}
