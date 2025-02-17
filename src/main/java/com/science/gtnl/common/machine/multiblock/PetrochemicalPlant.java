package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.api.util.GTUtility.validMTEList;
import static gtPlusPlus.core.block.ModBlocks.*;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.Utils.item.TextLocalization;
import com.science.gtnl.Utils.item.TextUtils;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;
import com.science.gtnl.common.recipe.RecipeRegister;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchMaintenance;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings10;
import kekztech.common.Blocks;

public class PetrochemicalPlant extends MultiMachineBase<PetrochemicalPlant> implements ISurvivalConstructable {

    private HeatingCoilLevel mHeatingCapacity;
    private int mLevel = 0;
    private int mCasing;

    private static IStructureDefinition<PetrochemicalPlant> STRUCTURE_DEFINITION = null;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static String[][] shape;
    public static final String PP_STRUCTURE_FILE_PATH = "sciencenotleisure:multiblock/petrochemical_plant";
    public final int horizontalOffSet = 22;
    public final int verticalOffSet = 56;
    public final int depthOffSet = 0;
    protected static final int CASING_INDEX = ((BlockCasings10) sBlockCasings10).getTextureIndex(3);

    public PetrochemicalPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        shape = StructureUtils.readStructureFromFile(PP_STRUCTURE_FILE_PATH);
    }

    public PetrochemicalPlant(String aName) {
        super(aName);
        shape = StructureUtils.readStructureFromFile(PP_STRUCTURE_FILE_PATH);
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    @Override
    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new PetrochemicalPlant(this.mName);
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

    public int getCasingTextureID() {
        return CASING_INDEX;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeRegister.PetrochemicalPlantRecipes;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(TextLocalization.PetrochemicalPlantRecipeType)
            .addInfo(TextLocalization.Tooltip_PetrochemicalPlant_00)
            .addInfo(TextLocalization.Tooltip_PetrochemicalPlant_01)
            .addInfo(TextLocalization.Tooltip_PetrochemicalPlant_02)
            .addInfo(TextLocalization.Tooltip_PetrochemicalPlant_03)
            .addSeparator()
            .addInfo(TextLocalization.StructureTooComplex)
            .addInfo(TextLocalization.BLUE_PRINT_INFO)
            .beginStructureBlock(28, 60, 65, true)
            .addInputHatch(TextLocalization.Tooltip_PetrochemicalPlant_Casing)
            .addOutputHatch(TextLocalization.Tooltip_PetrochemicalPlant_Casing)
            .addInputBus(TextLocalization.Tooltip_PetrochemicalPlant_Casing)
            .addOutputBus(TextLocalization.Tooltip_PetrochemicalPlant_Casing)
            .addEnergyHatch(TextLocalization.Tooltip_PetrochemicalPlant_Casing)
            .addMaintenanceHatch(TextLocalization.Tooltip_PetrochemicalPlant_Casing)
            .addMufflerHatch(TextLocalization.Tooltip_PetrochemicalPlant_Muffler, 8)
            .toolTipFinisher(TextUtils.SCIENCE_NOT_LEISURE + TextUtils.SQY);
        return tt;
    }

    @Override
    public IStructureDefinition<PetrochemicalPlant> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<PetrochemicalPlant>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlockAnyMeta(Blocks.yszUnit))
                .addElement('B', Muffler.newAny(CASING_INDEX, 8))
                .addElement('C', ofBlock(sBlockCasings2, 0))
                .addElement('D', ofBlock(sBlockCasings2, 12))
                .addElement('E', ofBlock(sBlockCasings2, 13))
                .addElement('F', ofBlock(sBlockCasings2, 14))
                .addElement('G', ofBlock(sBlockCasings4, 2))
                .addElement('H', ofBlock(sBlockCasings4, 1))
                .addElement('I', ofBlock(sBlockCasings4, 9))
                .addElement('J', ofBlock(sBlockCasings4, 10))
                .addElement('K', ofBlock(blockCasings3Misc, 2))
                .addElement('L', ofCoil(PetrochemicalPlant::setCoilLevel, PetrochemicalPlant::getCoilLevel))
                .addElement('M', ofBlock(sBlockCasings8, 1))
                .addElement('N', ofBlock(blockCasingsTieredGTPP, 4))
                .addElement(
                    'O',
                    buildHatchAdder(PetrochemicalPlant.class)
                        .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                        .casingIndex(CASING_INDEX)
                        .dot(1)
                        .buildAndChain(onElementPass(x -> ++x.mCasing, ofBlock(sBlockCasings10, 3))))
                .addElement('P', ofBlock(sBlockCasings10, 4))
                .addElement('Q', ofBlock(blockCasingsMisc, 14))
                .addElement('R', ofBlock(sBlockCasings9, 0))
                .addElement('S', ofFrame(Materials.NiobiumTitanium))
                .addElement('T', ofFrame(Materials.StainlessSteel))
                .addElement('U', ofFrame(Materials.Steel))
                .addElement('V', ofFrame(Materials.RedstoneAlloy))
                .addElement('W', ofFrame(Materials.Vanadium))
                .addElement('X', ofBlock(blockCasings2Misc, 4))
                .addElement('Y', ofBlock(blockCasingsMisc, 11))
                .addElement('Z', ofBlock(blockCustomMachineCasings, 1))
                .addElement('0', ofBlockAnyMeta(GameRegistry.findBlock(IndustrialCraft2.ID, "blockAlloyGlass")))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            horizontalOffSet,
            verticalOffSet,
            depthOffSet,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mCasing = 0;
        mLevel = 0;
        setCoilLevel(HeatingCoilLevel.None);

        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet) && checkHatch()) {
            return false;
        }

        IMetaTileEntity aMetaTileEntity = aBaseMetaTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        return mCasing >= 5 && getCoilLevel() != HeatingCoilLevel.None
            && this.mMufflerHatches.size() == 8
            && (mLevel = getCoilLevel().getTier() + 1) > 0;
    }

    @Override
    public int getMaxParallelRecipes() {
        return (this.mLevel * 16);
    }

    public HeatingCoilLevel getCoilLevel() {
        return mHeatingCapacity;
    }

    public void setCoilLevel(HeatingCoilLevel aCoilLevel) {
        mHeatingCapacity = aCoilLevel;
    }

    @Override
    public void checkMaintenance() {
        if (!shouldCheckMaintenance()) return;

        if (getRepairStatus() != getIdealStatus()) {
            for (MTEHatchMaintenance tHatch : validMTEList(mMaintenanceHatches)) {
                if (tHatch.mAuto) tHatch.autoMaintainance();
                if (tHatch.mWrench) mWrench = true;
                if (tHatch.mScrewdriver) mScrewdriver = true;
                if (tHatch.mSoftHammer) mSoftHammer = true;
                if (tHatch.mHardHammer) mHardHammer = true;
                if (tHatch.mSolderingTool) mSolderingTool = true;
                if (tHatch.mCrowbar) mCrowbar = true;

                tHatch.mWrench = false;
                tHatch.mScrewdriver = false;
                tHatch.mSoftHammer = false;
                tHatch.mHardHammer = false;
                tHatch.mSolderingTool = false;
                tHatch.mCrowbar = false;
            }
        }
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return true;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return true;
    }

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        if (processingLogic == null) {
            return checkRecipe(mInventory[1]) ? CheckRecipeResultRegistry.SUCCESSFUL
                : CheckRecipeResultRegistry.NO_RECIPE;
        }

        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = processingLogic.getDuration();
        setEnergyUsage(processingLogic);

        // 获取输出物品并进行 null 检查
        ItemStack[] outputItems = processingLogic.getOutputItems();
        if (outputItems != null) {
            for (ItemStack itemStack : outputItems) {
                if (itemStack != null) {
                    itemStack.stackSize *= this.mLevel * GTUtility.getTier(this.getMaxInputVoltage()) * 10;
                }
            }
        }
        mOutputItems = outputItems;

        // 获取输出流体并进行 null 检查
        FluidStack[] outputFluids = processingLogic.getOutputFluids();
        if (outputFluids != null) {
            for (FluidStack fluidStack : outputFluids) {
                if (fluidStack != null) {
                    fluidStack.amount *= this.mLevel * GTUtility.getTier(this.getMaxInputVoltage()) * 10;
                }
            }
        }
        mOutputFluids = outputFluids;

        return result;
    }
}
