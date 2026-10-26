package com.science.gtnl.mixins.late.gtneioreplugin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.science.gtnl.common.world.GTNLVeinCatalog;

import gtneioreplugin.util.GT5OreLayerHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@Mixin(value = GT5OreLayerHelper.class, remap = false)
public abstract class MixinGT5OreLayerHelper {

    @WrapOperation(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;",
            ordinal = 0))
    private static ImmutableMap<String, ?> gtnl$addVeins(Map<String, ?> map,
        Operation<ImmutableMap<String, ?>> operation) {
        Map<String, Object> copied = new Object2ObjectOpenHashMap<>(map);
        GTNLVeinCatalog.fillOreLayerWrappers(copied);
        return operation.call(copied);
    }
}
