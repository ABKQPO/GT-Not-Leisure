package com.science.gtnl.mixins.early.Minecraft;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiFlatPresets;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.FlatLayerInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiFlatPresets.class, remap = true)
public abstract class MixinGuiFlatPresets {

    @Shadow
    private static void func_146421_a(String p_146421_0_, Item p_146421_1_, BiomeGenBase p_146421_2_, List p_146421_3_,
        FlatLayerInfo... p_146421_4_) {}

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onInit(CallbackInfo ci) {
        func_146421_a(
            "The Void",
            Items.glass_bottle,
            BiomeGenBase.plains,
            Arrays.asList("decoration"),
            new FlatLayerInfo(1, Blocks.air));
    }
}
