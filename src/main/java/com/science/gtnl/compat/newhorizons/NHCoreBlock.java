package com.science.gtnl.compat.newhorizons;

import net.minecraft.block.Block;

import com.dreammaster.block.BlockList;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

// Blocks of the NH core mod that GTNL uses when it is installed. Callable without the mod: its classes are only touched
// inside the optional method.
public enum NHCoreBlock {

    BloodyIchorium,
    BloodyThaumium,
    BloodyVoid,
    CompressedGraphite;

    public Block get(Block fallback) {
        return Mods.NewHorizonsCoreMod.isModLoaded() ? block() : fallback;
    }

    @Optional.Method(modid = "dreamcraft")
    private Block block() {
        BlockList block = switch (this) {
            case BloodyIchorium -> BlockList.BloodyIchorium;
            case BloodyThaumium -> BlockList.BloodyThaumium;
            case BloodyVoid -> BlockList.BloodyVoid;
            case CompressedGraphite -> BlockList.CompressedGraphite;
        };
        return block.block;
    }
}
