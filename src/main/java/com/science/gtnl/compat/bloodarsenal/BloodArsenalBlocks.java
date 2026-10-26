package com.science.gtnl.compat.bloodarsenal;

import net.minecraft.block.Block;

import com.arc.bloodarsenal.common.block.ModBlocks;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

// Callable without Blood Arsenal installed: its classes are only touched inside the optional methods.
public class BloodArsenalBlocks {

    private BloodArsenalBlocks() {}

    public static Block bloodLamp(Block fallback) {
        return Mods.BloodArsenal.isModLoaded() ? lamp() : fallback;
    }

    public static Block lpMaterializer(Block fallback) {
        return Mods.BloodArsenal.isModLoaded() ? materializer() : fallback;
    }

    @Optional.Method(modid = "BloodArsenal")
    private static Block lamp() {
        return ModBlocks.blood_lamp;
    }

    @Optional.Method(modid = "BloodArsenal")
    private static Block materializer() {
        return ModBlocks.lp_materializer;
    }
}
