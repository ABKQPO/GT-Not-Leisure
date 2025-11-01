package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.science.gtnl.ScienceNotLeisure.*;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.dreammaster.block.BlockList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.ElectrocellGeneratorSpecialValue;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;

public class ElectrocellGenerator extends MultiMachineBase<ElectrocellGenerator> implements ISurvivalConstructable {

    private static final int HORIZONTAL_OFF_SET = 5;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String EG_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/electrocell_generator";
    private static final String[][] shape = StructureUtils.readStructureFromFile(EG_STRUCTURE_FILE_PATH);
    public double generatorValue = 1;
    public FluidStack matchedFluid;
    public FluidStack outputFluid;
    public static final Random random = new Random();
    public long lastEUt;

    public static final double[] FLUID_MULTIPLIERS = { 1.0, 1.8, 2.5, 3.5 };

    public MTEHatchInputBus mLeftInputBusses = null;
    public MTEHatchInputBus mRightInputBusses = null;

    public ElectrocellGenerator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ElectrocellGenerator(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ElectrocellGenerator(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_CHEMICAL_REACTOR_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings2, 0);
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public IStructureDefinition<ElectrocellGenerator> getStructureDefinition() {
        return StructureDefinition.<ElectrocellGenerator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                ofChain(
                    buildHatchAdder(ElectrocellGenerator.class)
                        .atLeast(Maintenance, OutputHatch, OutputBus, Dynamo.or(ExoticDynamo))
                        .dot(1)
                        .casingIndex(getCasingTextureID())
                        .build(),
                    onElementPass(x -> x.mCountCasing++, ofBlock(sBlockCasings2, 0))))
            .addElement('B', ofBlock(sBlockCasings3, 11))
            .addElement('C', ofFrame(Materials.Steel))
            .addElement('D', ofBlock(sBlockMetal4, 2))
            .addElement('E', ofBlock(BlockList.CompressedGraphite.getBlock(), 0))
            .addElement(
                'F',
                ofChain(
                    buildHatchAdder(ElectrocellGenerator.class).hatchClass(MTEHatchInputBus.class)
                        .shouldReject(t -> t.mRightInputBusses != null)
                        .adder(ElectrocellGenerator::addRightBusToMachineList)
                        .casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    onElementPass(x -> x.mCountCasing++, ofBlock(sBlockCasings1, 12))))
            .addElement(
                'G',
                ofChain(
                    buildHatchAdder(ElectrocellGenerator.class).hatchClass(MTEHatchInputBus.class)
                        .shouldReject(t -> t.mLeftInputBusses != null)
                        .adder(ElectrocellGenerator::addLeftBusToMachineList)
                        .casingIndex(getCasingTextureID())
                        .dot(1)
                        .build(),
                    onElementPass(x -> x.mCountCasing++, ofBlock(sBlockCasings1, 12))))
            .addElement(
                'H',
                buildHatchAdder(ElectrocellGenerator.class).atLeast(InputHatch)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings1, 12))))
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
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (!aBaseMetaTileEntity.isServerSide()) return;
        if (mMaxProgresstime > 0 && aTick % 20 == 0) {
            FluidStack matchedFluidExtra = matchedFluid.copy();
            matchedFluidExtra.amount = (int) (matchedFluidExtra.amount * generatorValue);
            if (depleteInput(matchedFluidExtra)) {
                FluidStack outputFluidExtra = outputFluid.copy();
                outputFluidExtra.amount = (int) (outputFluidExtra.amount * generatorValue);
                addOutput(outputFluidExtra);
                lEUt = (long) (lastEUt * generatorValue);
            } else if (depleteInput(matchedFluid)) {
                addOutput(outputFluid);
                lEUt = lastEUt;
            } else {
                lEUt -= (long) (1000 / generatorValue);
            }
        }
        if (lEUt <= 0) {
            stopMachine(ShutDownReasonRegistry.NONE);
        }
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        generatorValue = 1;
        lastEUt = 0;
        matchedFluid = null;
        outputFluid = null;

        Pair<ItemStack, ItemStack> inputItems = getStoredInputsItems();
        ItemStack leftItem = inputItems.getLeft();
        ItemStack rightItem = inputItems.getRight();
        ArrayList<FluidStack> fluidStacks = getStoredFluids();

        for (GTRecipe recipe : RecipePool.ElectrocellGeneratorRecipes.getAllRecipes()) {
            if (GTUtility.areStacksEqual(recipe.mInputs[0], leftItem)
                && GTUtility.areStacksEqual(recipe.mInputs[1], rightItem)) {
                if (recipe.mFluidInputs != null && !fluidStacks.isEmpty()) {
                    double multiplier = 1;

                    for (int i = 0; i < recipe.mFluidInputs.length; i++) {
                        for (FluidStack stored : fluidStacks) {
                            if (GTUtility.areFluidsEqual(recipe.mFluidInputs[i], stored)) {
                                matchedFluid = stored.copy();
                                if (i < FLUID_MULTIPLIERS.length) {
                                    multiplier = FLUID_MULTIPLIERS[i];
                                    break;
                                }
                            }
                        }
                    }

                    if (matchedFluid != null) {
                        if (depleteInput(leftItem) && depleteInput(rightItem)) {
                            mMaxProgresstime = recipe.mDuration;
                            generatorValue = recipe.mSpecialValue / 100D * multiplier;
                            lEUt = Objects
                                .requireNonNull(recipe.getMetadata(ElectrocellGeneratorSpecialValue.INSTANCE));
                            lastEUt = lEUt;
                            outputFluid = recipe.mFluidOutputs[0];

                            List<ItemStack> outputList = new ArrayList<>();

                            for (int i = 0; i < recipe.mOutputs.length; i++) {
                                int chance = i < recipe.mChances.length ? recipe.mChances[i] : 10000;

                                if (random.nextInt(10000) < chance) {
                                    outputList.add(recipe.mOutputs[i]);
                                }
                            }

                            mOutputItems = outputList.toArray(new ItemStack[0]);

                            return CheckRecipeResultRegistry.SUCCESSFUL;
                        }
                    }
                }
            }
        }

        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    public Pair<ItemStack, ItemStack> getStoredInputsItems() {
        ItemStack leftStack = null;
        ItemStack rightStack = null;

        if (mLeftInputBusses != null) {
            mLeftInputBusses.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = mLeftInputBusses.getBaseMetaTileEntity();
            int lastIndex = tileEntity.getSizeInventory() - 1;
            if (lastIndex >= 0) {
                leftStack = tileEntity.getStackInSlot(lastIndex);
            }
        }

        if (mRightInputBusses != null) {
            mRightInputBusses.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = mRightInputBusses.getBaseMetaTileEntity();
            int lastIndex = tileEntity.getSizeInventory() - 1;
            if (lastIndex >= 0) {
                rightStack = tileEntity.getStackInSlot(lastIndex);
            }
        }

        return Pair.of(leftStack, rightStack);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        aNBT.setDouble("generatorValue", generatorValue);

        if (matchedFluid != null) {
            NBTTagCompound fluidTag = new NBTTagCompound();
            matchedFluid.writeToNBT(fluidTag);
            aNBT.setTag("matchedFluid", fluidTag);
        }

        if (outputFluid != null) {
            NBTTagCompound fluidTag = new NBTTagCompound();
            outputFluid.writeToNBT(fluidTag);
            aNBT.setTag("outputFluid", fluidTag);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        generatorValue = aNBT.getDouble("generatorValue");

        if (aNBT.hasKey("matchedFluid")) {
            matchedFluid = FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag("matchedFluid"));
        }

        if (aNBT.hasKey("outputFluid")) {
            outputFluid = FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag("outputFluid"));
        }
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 55;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && mLeftInputBusses != null
            && mRightInputBusses != null
            && !mInputHatches.isEmpty()
            && !mOutputHatches.isEmpty();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.ElectrocellGeneratorRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ElectrocellGeneratorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_05"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(11, 5, 3, true)
            .addDynamoHatch(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        this.mLeftInputBusses = null;
        this.mRightInputBusses = null;
    }

    public boolean addLeftBusToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity != null) {
            final IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof MTEHatchInputBus inputBus) {
                if (inputBus.getTierForStructure() > 0) return false;
                if (mLeftInputBusses != null) return false;
                mLeftInputBusses = inputBus;
                mLeftInputBusses.updateTexture(aBaseCasingIndex);
                return true;
            }
        }
        return false;
    }

    public boolean addRightBusToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity != null) {
            final IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof MTEHatchInputBus inputBus) {
                if (inputBus.getTierForStructure() > 0) return false;
                if (mRightInputBusses != null) return false;
                mRightInputBusses = inputBus;
                mRightInputBusses.updateTexture(aBaseCasingIndex);
                return true;
            }
        }
        return false;
    }
}
