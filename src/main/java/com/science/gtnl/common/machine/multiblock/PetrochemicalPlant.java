package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.util.GTStructureUtility.*;
import static gtPlusPlus.core.block.ModBlocks.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.Utils.StructureUtils;
import com.science.gtnl.common.machine.multiMachineClasses.MultiMachineBase;
import com.science.gtnl.loader.RecipeRegister;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
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

    private static IStructureDefinition<PetrochemicalPlant> STRUCTURE_DEFINITION = null;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    public static final String PP_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/petrochemical_plant";
    public static String[][] shape = StructureUtils.readStructureFromFile(PP_STRUCTURE_FILE_PATH);
    public final int HORIZONTAL_OFF_SET = 22;
    public final int VERTICAL_OFF_SET = 56;
    public final int DEPTH_OFF_SET = 0;
    protected static final int CASING_INDEX = ((BlockCasings10) sBlockCasings10).getTextureIndex(3);

    public PetrochemicalPlant(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public PetrochemicalPlant(String aName) {
        super(aName);
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

    @Override
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
        tt.addMachineType(StatCollector.translateToLocal("PetrochemicalPlantRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PetrochemicalPlant_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PetrochemicalPlant_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PetrochemicalPlant_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PerfectOverclock"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(28, 60, 65, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_PetrochemicalPlant_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_PetrochemicalPlant_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_PetrochemicalPlant_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_PetrochemicalPlant_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_PetrochemicalPlant_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_PetrochemicalPlant_Casing"))
            .addMufflerHatch(StatCollector.translateToLocal("Tooltip_PetrochemicalPlant_Muffler"), 8)
            .toolTipFinisher();
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
                .addElement(
                    'L',
                    withChannel("coil", ofCoil(PetrochemicalPlant::setCoilLevel, PetrochemicalPlant::getCoilLevel)))
                .addElement('M', ofBlock(sBlockCasings8, 1))
                .addElement('N', ofBlock(blockCasingsTieredGTPP, 4))
                .addElement(
                    'O',
                    buildHatchAdder(PetrochemicalPlant.class)
                        .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                        .casingIndex(CASING_INDEX)
                        .dot(1)
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasings10, 3))))
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

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatch()) {
            return false;
        }

        IMetaTileEntity aMetaTileEntity = aBaseMetaTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        return tCountCasing >= 5 && getCoilLevel() != HeatingCoilLevel.None
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
        List<FluidStack> expandedFluids = new ArrayList<>();

        if (outputFluids != null) {
            for (FluidStack fluidStack : outputFluids) {
                if (fluidStack != null) {
                    // 计算放大后的总量
                    long totalAmount = (long) fluidStack.amount * this.mLevel
                        * GTUtility.getTier(this.getMaxInputVoltage())
                        * 10;

                    // 拆分为多个 FluidStack，避免 amount 超过 int 上限
                    while (totalAmount > 0) {
                        int stackSize = (int) Math.min(totalAmount, Integer.MAX_VALUE);
                        expandedFluids.add(new FluidStack(fluidStack.getFluid(), stackSize));
                        totalAmount -= stackSize;
                    }
                }
            }
        }

        mOutputFluids = expandedFluids.toArray(new FluidStack[0]);

        return result;
    }
}
