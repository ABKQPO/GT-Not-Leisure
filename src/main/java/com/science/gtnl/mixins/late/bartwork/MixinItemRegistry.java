package com.science.gtnl.mixins.late.bartwork;

import java.util.Arrays;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.science.gtnl.utils.enums.ModList;

import bartworks.common.loaders.ItemRegistry;
import goodgenerator.items.GGMaterial;

@Mixin(value = ItemRegistry.class, remap = false)
public class MixinItemRegistry {

    /**
     * @see gregtech.api.enums.Materials#QuarkGluonPlasma
     */
    @ModifyArgs(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lbartworks/common/blocks/BWBlocksGlass2;<init>(Ljava/lang/String;[Ljava/lang/String;[[SLnet/minecraft/creativetab/CreativeTabs;ZZ)V",
            ordinal = 0))
    private static void modifyRealGlass2Textures(Args args) {
        String[] originalTextures = args.get(1);
        String[] newTextures = Arrays.copyOf(originalTextures, originalTextures.length + 2);
        newTextures[originalTextures.length] = ModList.ScienceNotLeisure.ID
            + ":ShirabonReinforcedBoronSilicateGlassBlock";
        newTextures[originalTextures.length + 1] = ModList.ScienceNotLeisure.ID
            + ":QuarkGluonPlasmaReinforcedBoronSilicateGlassBlock";
        args.set(1, newTextures);

        short[][] originalColors = args.get(2);
        short[][] newColors = Arrays.copyOf(originalColors, originalColors.length + 2);

        newColors[originalColors.length] = GGMaterial.shirabon.getRGBA();

        newColors[originalColors.length + 1] = new short[] { 251, 239, 154 };
        args.set(2, newColors);
    }
}
