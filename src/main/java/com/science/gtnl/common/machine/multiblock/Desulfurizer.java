package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class Desulfurizer extends MultiMachineBase<Desulfurizer> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String Desu_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/desulfurizer";
    private static final String[][] shape = StructureUtils.readStructureFromFile(Desu_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 3;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 0;

    public Desulfurizer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public Desulfurizer(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new Desulfurizer(this.mName);
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings4, 1);
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
        return GTNLRecipeMaps.DesulfurizerRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("DesulfurizerRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Desulfurizer_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Desulfurizer_01"))
            .addPerfectOCInfo()
            .addMultiAmpHatchInfo()
            .beginStructureBlock(12, 6, 6, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_Desulfurizer_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_Desulfurizer_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_Desulfurizer_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_Desulfurizer_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_Desulfurizer_Casing"))
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<Desulfurizer> getStructureDefinition() {
        return StructureDefinition.<Desulfurizer>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 11))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 0))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 12))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 13))
            .addElement(
                'E',
                GTStructureUtility.buildHatchAdder(Desulfurizer.class)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy))
                    .casingIndex(StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings4, 1))
                    .dot(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 1))))
            .addElement(
                'F',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility.ofCoil(Desulfurizer::setMCoilLevel, Desulfurizer::getMCoilLevel))))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings6, 2))
            .build();
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
        if (!this.checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)
            || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 20;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && checkEnergyHatch() && getMCoilLevel() != HeatingCoilLevel.None;
    }

    @Override
    public int getMaxParallelRecipes() {
        return getMCoilLevel().getTier() * 8;
    }

    @Override
    public boolean getHeatOC() {
        return true;
    }

    @Override
    public int getMachineHeat() {
        return Math.toIntExact(getMCoilLevel().getHeat() * 2);
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public double getDurationModifier() {
        return 100.0 / (100 + 10 * getMCoilLevel().getTier());
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty() && getMaxInputAmps() <= 4;
        logic.setAvailableVoltage(getMachineVoltageLimit());
        logic.setAvailableAmperage(
            useSingleAmp ? 1
                : ExoticEnergyInputHelper.getMaxWorkingInputAmpsMulti(getExoticAndNormalEnergyHatchList()));
        logic.setAmperageOC(!useSingleAmp);
    }
}
