package com.science.gtnl.mixins.late.gregtech;

import net.minecraft.item.ItemRecord;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import gregtech.common.tileentities.machines.basic.MTEBetterJukebox;

@Mixin(value = MTEBetterJukebox.class, remap = false)
public class MixinMTEBetterJukebox {

    @Redirect(
        method = "onPostTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemRecord;getRecordResource(Ljava/lang/String;)Lnet/minecraft/util/ResourceLocation;"),
        remap = false)
    private ResourceLocation redirectGetRecordResource(ItemRecord record, String name) {
        ResourceLocation resource = record.getRecordResource(name);

        if ("minecraft".equals(resource.getResourceDomain()) && resource.getResourcePath()
            .startsWith("records.")) {
            return new ResourceLocation(
                resource.getResourceDomain(),
                resource.getResourcePath()
                    .substring(8));
        }

        return resource;
    }
}
