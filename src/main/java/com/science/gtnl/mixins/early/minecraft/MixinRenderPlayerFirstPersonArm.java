package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.client.celestweave.CelestweaveArmorModel;
import com.science.gtnl.client.celestweave.CelestweaveSkinLayers;

@Mixin(value = RenderPlayer.class, remap = true)
public class MixinRenderPlayerFirstPersonArm {

    @Shadow
    public ModelBiped modelBipedMain;

    @Inject(method = "renderFirstPersonArm", at = @At("HEAD"))
    public void gtnl$celestweaveHideSleeve(EntityPlayer player, CallbackInfo ci) {
        CelestweaveSkinLayers.hideArm(modelBipedMain.bipedRightArm, player);
    }

    @Inject(method = "renderFirstPersonArm", at = @At("RETURN"))
    public void gtnl$celestweaveArm(EntityPlayer player, CallbackInfo ci) {
        CelestweaveSkinLayers.restore();
        CelestweaveArmorModel.renderFirstPersonArm(player, modelBipedMain.bipedRightArm);
    }
}
