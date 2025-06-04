package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.blocks.BlockCasings4;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;

public class Desulfurizer extends MultiMachineBase<Desulfurizer> implements ISurvivalConstructable {

    private HeatingCoilLevel mHeatingCapacity;
    private int mLevel = 0;
    private static IStructureDefinition<Desulfurizer> STRUCTURE_DEFINITION = null;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String Desu_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/desulfurizer";
    public static String[][] shape = StructureUtils.readStructureFromFile(Desu_STRUCTURE_FILE_PATH);
    public final int HORIZONTAL_OFF_SET = 3;
    public final int VERTICAL_OFF_SET = 4;
    public final int DEPTH_OFF_SET = 0;

    public Desulfurizer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public Desulfurizer(String aName) {
        super(aName);
    }

    @Override
    public boolean isEnablePerfectOverclock() {
        return true;
    }

    @Override
    public float getSpeedBonus() {
        return 1;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new Desulfurizer(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return ((BlockCasings4) GregTechAPI.sBlockCasings4).getTextureIndex(1);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.DesulfurizerRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("DesulfurizerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Desulfurizer_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Desulfurizer_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_04"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(12, 6, 6, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_Desulfurizer_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_Desulfurizer_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_Desulfurizer_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_Desulfurizer_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_Desulfurizer_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<Desulfurizer> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<Desulfurizer>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(sBlockCasings1, 11))
                .addElement('B', ofBlock(sBlockCasings2, 0))
                .addElement('C', ofBlock(sBlockCasings2, 12))
                .addElement('D', ofBlock(sBlockCasings2, 13))
                .addElement(
                    'E',
                    buildHatchAdder(Desulfurizer.class)
                        .atLeast(InputHatch, OutputHatch, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                        .casingIndex(((BlockCasings4) sBlockCasings4).getTextureIndex(1))
                        .dot(1)
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasings4, 1))))
                .addElement('F', withChannel("coil", ofCoil(Desulfurizer::setCoilLevel, Desulfurizer::getCoilLevel)))
                .addElement('G', ofBlock(sBlockCasings6, 2))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        tCountCasing = 0;
        mLevel = 0;
        setCoilLevel(HeatingCoilLevel.None);

        if (!this.checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) return false;

        energyHatchTier = checkEnergyHatchTier();
        if (MainConfig.enableMachineAmpLimit) {
            for (MTEHatch hatch : getExoticEnergyHatches()) {
                if (hatch instanceof MTEHatchEnergyTunnel) {
                    return false;
                }
            }
            if (getMaxInputAmps() > 64) return false;
        }

        return tCountCasing >= 20 && getCoilLevel() != HeatingCoilLevel.None
            && (mLevel = getCoilLevel().getTier() + 1) > 0
            && checkHatch();
    }

    @Override
    public int getMaxParallelRecipes() {
        return (this.mLevel * 8);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            public OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setRecipeEUt(recipe.mEUt)
                    .setAmperage(availableAmperage)
                    .setEUt(availableVoltage)
                    .setDuration(recipe.mDuration)
                    .setAmperageOC(true)
                    .setDurationDecreasePerOC(4)
                    .setEUtIncreasePerOC(4)
                    .setDurationModifier(100.0 / (100 + 10 * mLevel))
                    .setHeatOC(true)
                    .setRecipeHeat(0)
                    .setMachineHeat((int) (getCoilLevel().getHeat() * 2));
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 2;
        logic.setAvailableVoltage(getMachineVoltageLimit());
        logic.setAvailableAmperage(
            useSingleAmp ? 1
                : ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(getExoticAndNormalEnergyHatchList()));
        logic.setAmperageOC(!mExoticEnergyHatches.isEmpty() || mEnergyHatches.size() != 1);
    }

    public HeatingCoilLevel getCoilLevel() {
        return mHeatingCapacity;
    }

    public void setCoilLevel(HeatingCoilLevel aCoilLevel) {
        mHeatingCapacity = aCoilLevel;
    }

    @Override
    public long getMaxInputAmps() {
        return getMaxInputAmpsHatch(getExoticAndNormalEnergyHatchList());
    }

    public static long getMaxInputAmpsHatch(Collection<? extends MTEHatch> hatches) {
        List<Long> ampsList = new ArrayList<>();
        for (MTEHatch tHatch : validMTEList(hatches)) {
            long currentAmp = tHatch.getBaseMetaTileEntity()
                .getInputAmperage();
            ampsList.add(currentAmp);
        }

        if (ampsList.isEmpty()) {
            return 0L;
        }

        return Collections.max(ampsList);
    }
}
