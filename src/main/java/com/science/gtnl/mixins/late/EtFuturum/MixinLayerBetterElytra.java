package com.science.gtnl.mixins.late.EtFuturum;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.reavaritia.ReAvaritia;
import com.reavaritia.common.items.InfinityElytra;

import ganymedes01.etfuturum.client.renderer.entity.elytra.LayerBetterElytra;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;

@Mixin(value = LayerBetterElytra.class, remap = false)
public class MixinLayerBetterElytra {

    @Unique
    private static final ResourceLocation TEXTURE_INFINITY_ELYTRA = new ResourceLocation(
        ReAvaritia.MODID,
        "textures/entity/infinity_elytra.png");

    @Redirect(
        method = "doRenderLayer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V",
            ordinal = 0))
    private static void gtnl$redirectInfinityElytraTexture(TextureManager textureManager, ResourceLocation location,
        EntityLivingBase entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
        float scale) {
        ItemStack itemstack = ItemArmorElytra.getElytra(entityIn);
        if (itemstack != null && itemstack.getItem() instanceof InfinityElytra) {
            textureManager.bindTexture(TEXTURE_INFINITY_ELYTRA);
        } else {
            textureManager.bindTexture(location);
        }
    }
}
