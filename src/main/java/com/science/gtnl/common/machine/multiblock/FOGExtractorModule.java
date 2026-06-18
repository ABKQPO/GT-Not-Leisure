package com.science.gtnl.common.machine.multiblock;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.IWidgetBuilder;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.science.gtnl.common.gui.modularui.FOGModuleGui;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.util.EternalGregTechWorkshopTextures;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.misc.WirelessNetworkManager;
import tectech.TecTech;
import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;

public class FOGExtractorModule extends MTEBaseModule {

    private long EUt = 0;
    private int currentParallel = 0;
    private boolean fluidMode = false;
    private long wirelessEUt = 0;

    public FOGExtractorModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public FOGExtractorModule(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new FOGExtractorModule(mName);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {

                if (recipe.mEUt > getProcessingVoltage()) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }

                wirelessEUt = (long) recipe.mEUt * getActualParallel();
                if (WirelessNetworkManager.getUserEU(userUUID)
                    .compareTo(BigInteger.valueOf(wirelessEUt * recipe.mDuration)) < 0) {
                    return CheckRecipeResultRegistry.insufficientPower(wirelessEUt * recipe.mDuration);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setEUt(getSafeProcessingVoltage())
                    .setRecipeHeat(recipe.mSpecialValue)
                    .setHeatOC(true)
                    .setHeatDiscount(true)
                    .setMachineHeat(Math.max(recipe.mSpecialValue, getHeatForOC()))
                    .setHeatDiscountMultiplier(getHeatEnergyDiscount())
                    .setDurationDecreasePerOC(getOverclockTimeFactor());

            }

            @NotNull
            @Override
            public CheckRecipeResult onRecipeStart(@NotNull GTRecipe recipe) {
                if (!WirelessNetworkManager.addEUToGlobalEnergyMap(userUUID, -calculatedEut * duration)) {
                    return CheckRecipeResultRegistry.insufficientPower(calculatedEut * duration);
                }
                addToPowerTally(
                    BigInteger.valueOf(calculatedEut)
                        .multiply(BigInteger.valueOf(duration)));
                addToRecipeTally(calculatedParallels);
                currentParallel = calculatedParallels;
                EUt = calculatedEut;
                overwriteCalculatedEut(0);
                setCurrentRecipeHeat(recipe.mSpecialValue);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        };
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        logic.setAvailableVoltage(Long.MAX_VALUE);
        logic.setAvailableAmperage(Integer.MAX_VALUE);
        logic.setAmperageOC(false);
        logic.setUnlimitedTierSkips();
        logic.setMaxParallel(getActualParallel());
        logic.setSpeedBonus(getSpeedBonus());
        logic.setEuModifier(getEnergyDiscount());
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return fluidMode ? RecipeMaps.fluidExtractionRecipes : RecipeMaps.extractorRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(RecipeMaps.fluidExtractionRecipes, RecipeMaps.extractorRecipes);
    }

    @Override
    public int getRecipeCatalystPriority() {
        return -10;
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> str = new ArrayList<>();
        str.add(
            StatCollector.translateToLocalFormatted(
                "GT5U.infodata.progress",
                EnumChatFormatting.GREEN + NumberFormatUtil.formatNumber(mProgresstime / 20) + EnumChatFormatting.RESET,
                EnumChatFormatting.YELLOW + NumberFormatUtil.formatNumber(mMaxProgresstime / 20)
                    + EnumChatFormatting.RESET));
        str.add(
            StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.currently_using",
                EnumChatFormatting.RED + (getBaseMetaTileEntity().isActive() ? NumberFormatUtil.formatNumber(EUt) : "0")
                    + EnumChatFormatting.RESET));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.max_parallel",
                EnumChatFormatting.RESET + NumberFormatUtil.formatNumber(getActualParallel())));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "GT5U.infodata.parallel.current",
                EnumChatFormatting.RESET
                    + (getBaseMetaTileEntity().isActive() ? NumberFormatUtil.formatNumber(currentParallel) : "0")));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.multiplier.recipe_time",
                EnumChatFormatting.RESET + NumberFormatUtil.formatNumber(getSpeedBonus())));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.multiplier.energy",
                EnumChatFormatting.RESET + NumberFormatUtil.formatNumber(getEnergyDiscount())));
        str.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted(
                "tt.infodata.multi.divisor.recipe_time.non_perfect_oc",
                EnumChatFormatting.RESET + NumberFormatUtil.formatNumber(getOverclockTimeFactor())));
        return str.toArray(new String[0]);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("FOGExtractorModuleRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGExtractorModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGExtractorModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGExtractorModule_02"))
            .addSeparator(EnumChatFormatting.AQUA, 74)
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGExtractorModule_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGExtractorModule_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGExtractorModule_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_FOGExtractorModule_06"))
            .beginStructureBlock(7, 7, 13, false)
            .addStructureInfo(
                EnumChatFormatting.GOLD + "20"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGModule_Casing_00"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "20"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGModule_Casing_01"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "5"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGModule_Casing_02"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "5"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGModule_Casing_03"))
            .addStructureInfo(
                EnumChatFormatting.GOLD + "1"
                    + EnumChatFormatting.GRAY
                    + StatCollector.translateToLocal("Tooltip_FOGModule_Casing_04"))
            .toolTipFinisher(EnumChatFormatting.AQUA, 74);
        return tt;
    }

    @Override
    @Deprecated
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        // TODO: Remove this mui1 fallback after every FOG module GUI action is covered by mui2.
        super.addUIWidgets(builder, buildContext);
        builder.widget(createFluidModeButton(builder));
    }

    @Deprecated
    public ButtonWidget createFluidModeButton(IWidgetBuilder<?> builder) {
        // TODO: Remove this mui1 fallback after every FOG module GUI action is covered by mui2.
        Widget button = new ButtonWidget().setOnClick((clickData, widget) -> {
            TecTech.proxy.playSound(getBaseMetaTileEntity(), "fx_click");
            fluidMode = !fluidMode;
            widget.notifyTooltipChange();
        })
            .setPlayClickSound(false)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(EternalGregTechWorkshopTextures.BUTTON_CELESTIAL_32x32);
                if (isFluidModeOn()) {
                    ret.add(EternalGregTechWorkshopTextures.OVERLAY_BUTTON_FURNACE_MODE);
                } else {
                    ret.add(EternalGregTechWorkshopTextures.OVERLAY_BUTTON_FURNACE_MODE_OFF);
                }
                return ret.toArray(new IDrawable[0]);
            })
            .attachSyncer(new FakeSyncWidget.BooleanSyncer(this::isFluidModeOn, this::setFluidMode), builder)
            .dynamicTooltip(
                () -> Collections.singletonList(
                    StatCollector.translateToLocal(
                        fluidMode ? "fog.button.fluidmode.tooltip.02" : "fog.button.fluidmode.tooltip.01")))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(174, 91)
            .setSize(16, 16);
        return (ButtonWidget) button;
    }

    public boolean isFluidModeOn() {
        return fluidMode;
    }

    public void setFluidMode(boolean enabled) {
        fluidMode = enabled;
    }

    @Override
    public void saveNBTData(NBTTagCompound NBT) {
        NBT.setBoolean("fluidMode", fluidMode);
        super.saveNBTData(NBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound NBT) {
        fluidMode = NBT.getBoolean("fluidMode");
        super.loadNBTData(NBT);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new FOGModuleGui(this);
    }

    @Override
    public double getSpeedBonus() {
        return processingSpeedBonus / 2;
    }

    @Override
    public double getEnergyDiscount() {
        return processingSpeedBonus / 2;
    }

}
