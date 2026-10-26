package com.science.gtnl.compat.extrautilities;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;

import com.rwtema.extrautils.gui.GuiTradingPost;
import com.rwtema.extrautils.worldgen.Underdark.ChunkProviderUnderdark;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Mods;

public class ExtraUtilitiesContent {

    private ExtraUtilitiesContent() {}

    public static Block greenScreen() {
        return Mods.ExtraUtilities.isModLoaded() ? Block.getBlockFromName("ExtraUtilities:greenscreen") : null;
    }

    public static boolean isUnderdark(World world) {
        return Mods.ExtraUtilities.isModLoaded() && world != null && underdark(world);
    }

    @SideOnly(Side.CLIENT)
    public static boolean isTradingPost(GuiScreen screen) {
        return Mods.ExtraUtilities.isModLoaded() && tradingPost(screen);
    }

    @Optional.Method(modid = "ExtraUtilities")
    private static boolean underdark(World world) {
        if (!(world.getChunkProvider() instanceof ChunkProviderServer chunkProviderServer)) return false;
        return chunkProviderServer.currentChunkProvider instanceof ChunkProviderUnderdark;
    }

    @Optional.Method(modid = "ExtraUtilities")
    @SideOnly(Side.CLIENT)
    private static boolean tradingPost(GuiScreen screen) {
        return screen instanceof GuiTradingPost;
    }
}
