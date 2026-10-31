package com.science.gtnl.common.machine.basicMachine;

import net.minecraft.util.StatCollector;

import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.FallbackableSteamTexture;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineBronze;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.common.modularui2.util.SteamTexture;

public class SteamAssemblerBronze extends MTEBasicMachineBronze {

    public SteamAssemblerBronze(int aID, String aName, String aNameRegional) {
        super(
            aID,
            aName,
            aNameRegional,
            StatCollector.translateToLocal("gtnl.machine.steam_assembler.tooltip.0"),
            6,
            1,
            false);
    }

    public SteamAssemblerBronze(String aName, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aDescription, aTextures, 6, 1, false);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamAssemblerBronze(this.mName, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.assemblerRecipes;
    }

    @Override
    protected BasicUIProperties getUIProperties() {
        return applyTo(super.getUIProperties());
    }

    @Override
    public void startProcess() {
        sendLoopStart((byte) 1);
    }

    @Override
    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        if (aIndex == 1) {
            GTUtility.doSoundAtClient(SoundResource.GT_MACHINES_MULTI_PRECISE_LOOP, 10, 1.0F, aX, aY, aZ);
        }
    }

    @Override
    public ITexture[] getSideFacingActive(byte aColor) {
        return new ITexture[] { super.getSideFacingActive(aColor)[0],
            TextureFactory.of(Textures.BlockIcons.OVERLAY_SIDE_DISASSEMBLER_ACTIVE), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_SIDE_DISASSEMBLER_ACTIVE_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getSideFacingInactive(byte aColor) {
        return new ITexture[] { super.getSideFacingInactive(aColor)[0],
            TextureFactory.of(Textures.BlockIcons.OVERLAY_SIDE_DISASSEMBLER), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_SIDE_DISASSEMBLER_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getFrontFacingActive(byte aColor) {
        return new ITexture[] { super.getFrontFacingActive(aColor)[0],
            TextureFactory.of(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER_ACTIVE), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER_ACTIVE_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getFrontFacingInactive(byte aColor) {
        return new ITexture[] { super.getFrontFacingInactive(aColor)[0],
            TextureFactory.of(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getTopFacingActive(byte aColor) {
        return new ITexture[] { super.getTopFacingActive(aColor)[0],
            TextureFactory.of(Textures.BlockIcons.OVERLAY_TOP_DISASSEMBLER_ACTIVE), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_TOP_DISASSEMBLER_ACTIVE_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getTopFacingInactive(byte aColor) {
        return new ITexture[] { super.getTopFacingInactive(aColor)[0],
            TextureFactory.of(Textures.BlockIcons.OVERLAY_TOP_DISASSEMBLER), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_TOP_DISASSEMBLER_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getBottomFacingActive(byte aColor) {
        return new ITexture[] { super.getBottomFacingActive(aColor)[0],
            TextureFactory.of(Textures.BlockIcons.OVERLAY_BOTTOM_DISASSEMBLER_ACTIVE), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_BOTTOM_DISASSEMBLER_ACTIVE_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getBottomFacingInactive(byte aColor) {
        return new ITexture[] { super.getBottomFacingInactive(aColor)[0],
            TextureFactory.of(Textures.BlockIcons.OVERLAY_BOTTOM_DISASSEMBLER), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_BOTTOM_DISASSEMBLER_GLOW)
                .glow()
                .build() };
    }

    public static BasicUIProperties applyTo(BasicUIProperties baseProperties) {
        return baseProperties.toBuilder()
            .progressBarTextureSteam(new FallbackableSteamTexture(GTUITextures.PROGRESSBAR_ARROW_STEAM))
            .progressBarTextureSteamMUI2(GTGuiTextures.PROGRESSBAR_ARROW_STEAM)
            .slotOverlaysSteamMUI2(SteamAssemblerBronze::getSlotOverlay)
            .build();
    }

    private static SteamTexture getSlotOverlay(int index, boolean isFluid, boolean isOutput, boolean isSpecial) {
        if (isFluid) {
            return GTGuiTextures.OVERLAY_SLOT_CANISTER_STEAM;
        }
        return isOutput ? GTGuiTextures.OVERLAY_SLOT_OUT_STEAM : GTGuiTextures.OVERLAY_SLOT_IN_STEAM;
    }
}
