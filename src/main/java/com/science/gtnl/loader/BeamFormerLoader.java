package com.science.gtnl.loader;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import com.science.gtnl.common.block.blocks.tile.TileEntityBeamFormer;
import com.science.gtnl.common.render.beamformer.BeamFormerItemRenderer;

import appeng.client.render.TESRWrapper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BeamFormerLoader implements IContentLoader {

    @Override
    public String name() {
        return "beamformer";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientInit() {
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileEntityBeamFormer.class,
            new TESRWrapper(BlockLoader.beamFormer.getRenderer()));
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(BlockLoader.beamFormer), new BeamFormerItemRenderer());
    }
}
