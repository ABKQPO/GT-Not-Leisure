package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftRailgunClick {

    @Inject(method = "func_147116_af", at = @At("HEAD"), cancellable = true)
    private void gtnl$railgunClick(CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (ElectromagneticRailgun.isHeld(minecraft.thePlayer)) ci.cancel();
    }

    @Inject(method = "func_147115_a", at = @At("HEAD"), cancellable = true)
    private void gtnl$railgunBlock(boolean held, CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (ElectromagneticRailgun.isHeld(minecraft.thePlayer)) {
            minecraft.playerController.resetBlockRemoving();
            ci.cancel();
        }
    }
}
