package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.multiMachineBase.GTMMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class WoodDistillation extends GTMMultiMachineBase<WoodDistillation> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String WD_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/wood_distillation";
    private static final String[][] shape = StructureUtils.readStructureFromFile(WD_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 11;
    private static final int VERTICAL_OFF_SET = 18;
    private static final int DEPTH_OFF_SET = 2;

    public WoodDistillation(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public WoodDistillation(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new WoodDistillation(this.mName);
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
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings1, 11);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.WoodDistillationRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("WoodDistillationRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WoodDistillation_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addTecTechHatchInfo()
            .beginStructureBlock(23, 20, 15, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_WoodDistillation_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_WoodDistillation_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_WoodDistillation_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_WoodDistillation_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_WoodDistillation_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<WoodDistillation> getStructureDefinition() {
        return StructureDefinition.<WoodDistillation>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(BlockLoader.metaCasing, 2))
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(WoodDistillation.class)
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
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 11))))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 1))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 13))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 11))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 14))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 1))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings4, 10))
            .addElement('I', StructureUtility.ofBlock(GregTechAPI.sBlockCasings6, 3))
            .addElement('J', GTStructureUtility.ofFrame(Materials.StainlessSteel))
            .addElement('K', HatchElement.Muffler.newAny(getCasingTextureID(), 2))
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
        return mCountCasing >= 220;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && mMufflerHatches.size() == 2;
    }

    @Override
    public boolean checkEnergyHatch() {
        return true;
    }

    @Override
    public double getEUtDiscount() {
        return 1 - (mParallelTier / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return 1 - (Math.max(0, mParallelTier - 1) / 50.0);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        if (processingLogic == null) {
            return checkRecipe(mInventory[1]) ? CheckRecipeResultRegistry.SUCCESSFUL
                : CheckRecipeResultRegistry.NO_RECIPE;
        }

        resetParallelTier();
        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = processingLogic.getDuration();
        setEnergyUsage(processingLogic);

        ItemStack[] outputItems = processingLogic.getOutputItems();
        if (outputItems != null) {
            for (ItemStack itemStack : outputItems) {
                if (itemStack != null) {
                    itemStack.stackSize *= GTUtility.getTier(this.getMaxInputVoltage()) * 2;
                }
            }
        }
        mOutputItems = outputItems;

        FluidStack[] outputFluids = processingLogic.getOutputFluids();

        mOutputFluids = outputFluids;
        if (outputFluids != null) {
            List<FluidStack> expandedFluids = new ArrayList<>();
            for (FluidStack fluidStack : outputFluids) {
                if (fluidStack != null) {
                    long totalAmount = (long) fluidStack.amount * GTUtility.getTier(this.getMaxInputVoltage()) * 2;

                    while (totalAmount > 0) {
                        int stackSize = (int) Math.min(totalAmount, Integer.MAX_VALUE);
                        expandedFluids.add(new FluidStack(fluidStack.getFluid(), stackSize));
                        totalAmount -= stackSize;
                    }
                }
            }
            mOutputFluids = expandedFluids.toArray(new FluidStack[0]);
        }

        return result;
    }
}
