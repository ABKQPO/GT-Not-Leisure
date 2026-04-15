package com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.GregTechAPI.sBlockCasings8;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;

public class ChemicalPlant extends GTMMultiMachineBase<ChemicalPlant> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String CP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/chemical_plant";
    private static final int HORIZONTAL_OFF_SET = 0;
    private static final int VERTICAL_OFF_SET = 3;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(CP_STRUCTURE_FILE_PATH);

    public ChemicalPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ChemicalPlant(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ChemicalPlant(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings8, 0);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.multiblockChemicalReactorRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ChemicalPlantRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ChemicalPlant_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ChemicalPlant_01"))
            .addPerfectOCInfo()
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addMultiAmpHatchInfo()
            .beginStructureBlock(5, 5, 5, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_ChemicalPlant_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_ChemicalPlant_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_ChemicalPlant_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ChemicalPlant_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_ChemicalPlant_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_ChemicalPlant_Casing"))
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<ChemicalPlant> getStructureDefinition() {
        return StructureDefinition.<ChemicalPlant>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility.ofCoil(ChemicalPlant::setMCoilLevel, ChemicalPlant::getMCoilLevel))))
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(ChemicalPlant.class)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasings8, 0))))
            .addElement('C', StructureUtility.ofBlock(sBlockCasings8, 1))
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 50;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && getMCoilLevel() != HeatingCoilLevel.None;
    }

    @Override
    public double getEUtDiscount() {
        return 0.8 * Math.pow(0.95, getMCoilLevel().getTier());
    }

    @Override
    public double getDurationModifier() {
        return 1 / 1.67 * Math.pow(0.95, getMCoilLevel().getTier());
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public int getMaxParallelRecipes() {
        resetParallelTier();
        if (mParallelControllerHatches.size() == 1) {
            ParallelControllerHatch module = mParallelControllerHatches.get(0);
            mParallelTier = module.mTier;
            return Math.min(module.getParallel(), 1024);
        }

        int maxRecipes;
        if (mParallelTier <= 2) {
            maxRecipes = 8;
        } else {
            maxRecipes = 1 << (2 * (mParallelTier - 2));
        }
        return Math.min(maxRecipes, 1024);
    }
}
