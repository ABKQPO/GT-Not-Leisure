package com.science.gtnl.common.machine.basicMachine;

import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicGenerator;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

public class SteamTurbine extends MTEBasicGenerator implements IAddGregtechLogo {

    public SteamTurbine(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            new String[] { StatCollector.translateToLocal("Tooltip_SteamTurbine_00"),
                StatCollector.translateToLocal("Tooltip_SteamTurbine_01"), "", "" });
        mDescriptionArray[2] = StatCollector
            .translateToLocalFormatted("Tooltip_SteamTurbine_02", 800 / getEfficiency());
        mDescriptionArray[3] = StatCollector
            .translateToLocalFormatted("Tooltip_SteamTurbine_03", GTUtility.formatNumbers(getCapacity()));
    }

    public SteamTurbine(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
        mDescriptionArray[2] = StatCollector
            .translateToLocalFormatted("Tooltip_SteamTurbine_02", 800 / getEfficiency());
        mDescriptionArray[3] = StatCollector
            .translateToLocalFormatted("Tooltip_SteamTurbine_03", GTUtility.formatNumbers(getCapacity()));
    }

    @Override
    public boolean isOutputFacing(ForgeDirection side) {
        return side == getBaseMetaTileEntity().getFrontFacing();
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new SteamTurbine(this.mName, this.mTier, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_LOGO)
                .setSize(18, 18)
                .setPos(151, 62));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return null;
    }

    @Override
    public String[] getDescription() {
        return mDescriptionArray;
    }

    @Override
    public int getCapacity() {
        return 640000 * this.mTier;
    }

    @Override
    public int getEfficiency() {
        return 7 + this.mTier;
    }

    @Override
    public int consumedFluidPerOperation(FluidStack aLiquid) {
        return getEfficiency();
    }

    @Override
    public long getFuelValue(FluidStack aLiquid, boolean aLong) {
        return getFuelValue(aLiquid);
    }

    @Override
    public int getFuelValue(FluidStack aLiquid) {
        if (aLiquid == null) return 0;
        return GTModHandler.isAnySteam(aLiquid) ? 4 : 0;
    }

    @Override
    public ITexture[] getFront(byte aColor) {
        return new ITexture[] { super.getFront(aColor)[0],
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.STEAM_TURBINE_FRONT),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.STEAM_TURBINE_FRONT_GLOW)
                    .glow()
                    .build()),
            Textures.BlockIcons.OVERLAYS_ENERGY_OUT[this.mTier + 1] };
    }

    @Override
    public ITexture[] getBack(byte aColor) {
        return new ITexture[] { super.getBack(aColor)[0],
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.STEAM_TURBINE_BACK),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.STEAM_TURBINE_BACK_GLOW)
                    .glow()
                    .build()) };
    }

    @Override
    public ITexture[] getBottom(byte aColor) {
        return new ITexture[] { super.getBottom(aColor)[0],
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.STEAM_TURBINE_BOTTOM),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.STEAM_TURBINE_BOTTOM_GLOW)
                    .glow()
                    .build()) };
    }

    @Override
    public ITexture[] getTop(byte aColor) {
        return new ITexture[] { super.getTop(aColor)[0],
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.STEAM_TURBINE_TOP),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.STEAM_TURBINE_TOP_GLOW)
                    .glow()
                    .build()) };
    }

    @Override
    public ITexture[] getSides(byte aColor) {
        return new ITexture[] { super.getSides(aColor)[0],
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.STEAM_TURBINE_SIDE),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.STEAM_TURBINE_SIDE_GLOW)
                    .glow()
                    .build()) };
    }

    @Override
    public ITexture[] getFrontActive(byte aColor) {
        return new ITexture[] { super.getFrontActive(aColor)[0],
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.STEAM_TURBINE_FRONT_ACTIVE),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.STEAM_TURBINE_FRONT_ACTIVE_GLOW)
                    .glow()
                    .build()),
            Textures.BlockIcons.OVERLAYS_ENERGY_OUT[this.mTier + 1] };
    }

    @Override
    public ITexture[] getBackActive(byte aColor) {
        return new ITexture[] { super.getBackActive(aColor)[0],
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.STEAM_TURBINE_BACK_ACTIVE),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.STEAM_TURBINE_BACK_ACTIVE_GLOW)
                    .glow()
                    .build()) };
    }

    @Override
    public ITexture[] getBottomActive(byte aColor) {
        return new ITexture[] { super.getBottomActive(aColor)[0],
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.STEAM_TURBINE_BOTTOM_ACTIVE),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.STEAM_TURBINE_BOTTOM_ACTIVE_GLOW)
                    .glow()
                    .build()) };
    }

    @Override
    public ITexture[] getTopActive(byte aColor) {
        return new ITexture[] { super.getTopActive(aColor)[0],
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.STEAM_TURBINE_TOP_ACTIVE),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.STEAM_TURBINE_TOP_ACTIVE_GLOW)
                    .glow()
                    .build()) };
    }

    @Override
    public ITexture[] getSidesActive(byte aColor) {
        return new ITexture[] { super.getSidesActive(aColor)[0],
            TextureFactory.of(
                TextureFactory.of(Textures.BlockIcons.STEAM_TURBINE_SIDE_ACTIVE),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.STEAM_TURBINE_SIDE_ACTIVE_GLOW)
                    .glow()
                    .build()) };
    }

    @Override
    public int getPollution() {
        return 0;
    }

    @Override
    public long maxAmperesOut() {
        return 8;
    }

    @Override
    public long maxEUStore() {
        return Math.max(getEUVar(), GTValues.V[mTier] * 128L + getMinimumStoredEU());
    }
}
