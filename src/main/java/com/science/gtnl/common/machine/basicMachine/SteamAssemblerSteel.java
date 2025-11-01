package com.science.gtnl.common.machine.basicMachine;

import static gregtech.api.enums.Textures.BlockIcons.*;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.FluidSlotWidget;
import com.gtnewhorizons.modularui.common.widget.ProgressBar;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineSteel;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;

public class SteamAssemblerSteel extends MTEBasicMachineSteel {

    public SteamAssemblerSteel(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, StatCollector.translateToLocal("Tooltip_SteamAssembler_00"), 6, 1, true);
    }

    public SteamAssemblerSteel(String aName, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aDescription, aTextures, 6, 1, true);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamAssemblerSteel(this.mName, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_STEAM_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.assemblerRecipes;
    }

    @Override
    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        if (aIndex == 1) {
            GTUtility.doSoundAtClient(SoundResource.GT_MACHINES_MULTI_PRECISE_LOOP, 10, 1.0F, aX, aY, aZ);
        }
    }

    @Override
    public void startProcess() {
        sendLoopStart((byte) 1);
    }

    @Override
    public ITexture[] getSideFacingActive(byte aColor) {
        return new ITexture[] { super.getSideFacingActive(aColor)[0],
            TextureFactory.of(OVERLAY_SIDE_DISASSEMBLER_ACTIVE), TextureFactory.builder()
                .addIcon(OVERLAY_SIDE_DISASSEMBLER_ACTIVE_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getSideFacingInactive(byte aColor) {
        return new ITexture[] { super.getSideFacingInactive(aColor)[0], TextureFactory.of(OVERLAY_SIDE_DISASSEMBLER),
            TextureFactory.builder()
                .addIcon(OVERLAY_SIDE_DISASSEMBLER_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getFrontFacingActive(byte aColor) {
        return new ITexture[] { super.getFrontFacingActive(aColor)[0],
            TextureFactory.of(OVERLAY_FRONT_DISASSEMBLER_ACTIVE), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_DISASSEMBLER_ACTIVE_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getFrontFacingInactive(byte aColor) {
        return new ITexture[] { super.getFrontFacingInactive(aColor)[0], TextureFactory.of(OVERLAY_FRONT_DISASSEMBLER),
            TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_DISASSEMBLER_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getTopFacingActive(byte aColor) {
        return new ITexture[] { super.getTopFacingActive(aColor)[0], TextureFactory.of(OVERLAY_TOP_DISASSEMBLER_ACTIVE),
            TextureFactory.builder()
                .addIcon(OVERLAY_TOP_DISASSEMBLER_ACTIVE_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getTopFacingInactive(byte aColor) {
        return new ITexture[] { super.getTopFacingInactive(aColor)[0], TextureFactory.of(OVERLAY_TOP_DISASSEMBLER),
            TextureFactory.builder()
                .addIcon(OVERLAY_TOP_DISASSEMBLER_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getBottomFacingActive(byte aColor) {
        return new ITexture[] { super.getBottomFacingActive(aColor)[0],
            TextureFactory.of(OVERLAY_BOTTOM_DISASSEMBLER_ACTIVE), TextureFactory.builder()
                .addIcon(OVERLAY_BOTTOM_DISASSEMBLER_ACTIVE_GLOW)
                .glow()
                .build() };
    }

    @Override
    public ITexture[] getBottomFacingInactive(byte aColor) {
        return new ITexture[] { super.getBottomFacingInactive(aColor)[0],
            TextureFactory.of(OVERLAY_BOTTOM_DISASSEMBLER), TextureFactory.builder()
                .addIcon(OVERLAY_BOTTOM_DISASSEMBLER_GLOW)
                .glow()
                .build() };
    }

    @Override
    public FluidSlotWidget createFluidInputSlot(IDrawable[] backgrounds, Pos2d pos) {
        return (FluidSlotWidget) new FluidSlotWidget(fluidTank).setBackground(backgrounds)
            .setPos(pos);
    }

    @Override
    public void addProgressBar(ModularWindow.Builder builder, BasicUIProperties uiProperties) {
        builder.widget(
            setNEITransferRect(
                new ProgressBar()
                    .setProgress(() -> maxProgresstime() != 0 ? (float) getProgresstime() / maxProgresstime() : 0)
                    .setTexture(uiProperties.progressBarTexture.get(), uiProperties.progressBarImageSize)
                    .setDirection(uiProperties.progressBarDirection)
                    .setPos(uiProperties.progressBarPos)
                    .setSize(uiProperties.progressBarSize),
                uiProperties.neiTransferRectId));
        addProgressBarSpecialTextures(builder, uiProperties);
    }

    @Override
    public void addProgressBarSpecialTextures(ModularWindow.Builder builder, BasicUIProperties uiProperties) {

        for (Pair<IDrawable, Pair<Size, Pos2d>> specialTexture : uiProperties.specialTextures) {
            builder.widget(
                new DrawableWidget().setDrawable(specialTexture.getLeft())
                    .setSize(
                        specialTexture.getRight()
                            .getLeft())
                    .setPos(
                        specialTexture.getRight()
                            .getRight()));
        }

    }

    @Override
    public int getCapacity() {
        return 16000;
    }

    @Override
    public int checkRecipe() {
        GTRecipe tRecipe = getRecipeMap().findRecipeQuery()
            .items(getAllInputs())
            .fluids(getFillableStack())
            .voltage(TierEU.MV)
            .find();
        if ((tRecipe != null) && (canOutput(tRecipe.mOutputs))
            && (tRecipe.isRecipeInputEqual(true, new FluidStack[] { getFillableStack() }, getAllInputs()))) {
            this.mOutputItems[0] = tRecipe.getOutput(0);
            calculateCustomOverclock(tRecipe);
            return FOUND_AND_SUCCESSFULLY_USED_RECIPE;
        }
        return DID_NOT_FIND_RECIPE;
    }
}
