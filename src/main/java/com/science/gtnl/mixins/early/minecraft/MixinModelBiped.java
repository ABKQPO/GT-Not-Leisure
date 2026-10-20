package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;

@Mixin(value = ModelBiped.class, remap = true)
public class MixinModelBiped {

    @Shadow
    public ModelRenderer bipedHead;
    @Shadow
    public ModelRenderer bipedRightArm;
    @Shadow
    public ModelRenderer bipedLeftArm;

    @Inject(method = "setRotationAngles", at = @At("RETURN"))
    public void mixin$poseRailgunArms(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
        float headPitch, float scale, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof EntityLivingBase living)) return;
        ItemStack held = living.getHeldItem();
        if (!ElectromagneticRailgun.is(held)) return;

        bipedRightArm.rotateAngleX = -1.48F + bipedHead.rotateAngleX;
        bipedRightArm.rotateAngleY = bipedHead.rotateAngleY;
        bipedRightArm.rotateAngleZ = 0.0F;

        bipedLeftArm.rotateAngleX = -1.42F + bipedHead.rotateAngleX;
        bipedLeftArm.rotateAngleY = bipedHead.rotateAngleY + 0.42F;
        bipedLeftArm.rotateAngleZ = -0.14F;
    }
}
