package com.science.gtnl.mixins.late.Gregtech;

import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import gregtech.common.tileentities.machines.basic.MTEBetterJukebox;

@SuppressWarnings("UnusedMixin")
@Mixin(value = MTEBetterJukebox.class, remap = false)
public class MTEBetterJukebox_Mixin {

    @Redirect(
        method = "onPostTick",
        at = @At(value = "NEW", target = "net/minecraft/util/ResourceLocation"),
        remap = false)
    private ResourceLocation redirectSetRecordResource(String domain, String path) {
        if (!"minecraft".equals(domain)) {
            String originalPath = path.replaceFirst("records\\.", "");
            return new ResourceLocation(domain, originalPath);
        }
        return new ResourceLocation(domain, path);
    }
}
