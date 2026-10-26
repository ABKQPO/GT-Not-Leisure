package com.science.gtnl.compat.newhorizons;

import net.minecraft.block.Block;

import com.dreammaster.block.BlockList;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;

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
