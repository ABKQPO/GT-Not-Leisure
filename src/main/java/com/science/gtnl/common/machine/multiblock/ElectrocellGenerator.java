package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ExoticDynamo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.dreammaster.block.BlockList;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.api.IConfigurationMaintenance;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.metadata.ElectrocellGeneratorMetadata;

import cpw.mods.fml.common.Optional;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchMaintenance;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class ElectrocellGenerator extends MultiMachineBase<ElectrocellGenerator> implements ISurvivalConstructable {

    private static final int HORIZONTAL_OFF_SET = 5;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String EG_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/electrocell_generator";
    private static final String[][] shape = StructureUtils.readStructureFromFile(EG_STRUCTURE_FILE_PATH);
    public static final Random random = new Random();
    public static final double[] FLUID_MULTIPLIERS = { 1.0, 1.8, 2.5, 3.5 };
    public static final Block COMPRESSED_GRAPHITE = Mods.NewHorizonsCoreMod.isModLoaded() ? getCompressedGraphite()
        : Blocks.coal_block;

    public double generatorValue = 1;
    public FluidStack matchedFluid;
    public FluidStack outputFluid;
    public long lastEUt;

    public MTEHatchInputBus mLeftInputBusses = null;
    public MTEHatchInputBus mRightInputBusses = null;

    public ElectrocellGenerator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ElectrocellGenerator(String aName) {
        super(aName);
    }

    @Optional.Method(modid = "dreamcraft")
    public static Block getCompressedGraphite() {
        return BlockList.CompressedGraphite.block;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ElectrocellGenerator(this.mName);
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        this.mLeftInputBusses = null;
        this.mRightInputBusses = null;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (mStartUpCheck > 0 || !aBaseMetaTileEntity.isServerSide()) return;
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
        if (mMaxProgresstime > 0 && lEUt <= 0) {
            stopMachine(ShutDownReasonRegistry.NONE);
        }
    }

    @Override
    public IStructureDefinition<ElectrocellGenerator> getStructureDefinition() {
        return StructureDefinition.<ElectrocellGenerator>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(ElectrocellGenerator.class)
                        .atLeast(
                            HatchElement.Maintenance,
                            HatchElement.OutputHatch,
                            HatchElement.OutputBus,
                            HatchElement.Dynamo.or(ExoticDynamo))
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    StructureUtility
                        .onElementPass(x -> x.mCountCasing++, StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 0))))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 11))
            .addElement('C', GTStructureUtility.ofFrame(Materials.Steel))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockMetal4, 2))
            .addElement('E', StructureUtility.ofBlock(COMPRESSED_GRAPHITE, 0))
            .addElement(
                'F',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(ElectrocellGenerator.class)
                        .hatchClass(MTEHatchInputBus.class)
                        .shouldReject(t -> t.mRightInputBusses != null)
                        .adder(ElectrocellGenerator::addRightBusToMachineList)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    StructureUtility
                        .onElementPass(x -> x.mCountCasing++, StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 0))))
            .addElement(
                'G',
                StructureUtility.ofChain(
                    GTStructureUtility.buildHatchAdder(ElectrocellGenerator.class)
                        .hatchClass(MTEHatchInputBus.class)
                        .shouldReject(t -> t.mLeftInputBusses != null)
                        .adder(ElectrocellGenerator::addLeftBusToMachineList)
                        .casingIndex(getCasingTextureID())
                        .hint(1)
                        .build(),
                    StructureUtility
                        .onElementPass(x -> x.mCountCasing++, StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 0))))
            .addElement(
                'H',
                GTStructureUtility.buildHatchAdder(ElectrocellGenerator.class)
                    .atLeast(HatchElement.InputHatch)
                    .casingIndex(getCasingTextureID())
                    .hint(1)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 0))))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 55);
        checkHatchMin(errors, HatchElement.OutputHatch, 1);
        if (mLeftInputBusses == null || mRightInputBusses == null) {
            errors.add(StructureErrors.missingHatch(HatchElement.InputBus));
        }
        checkHasInputHatch(errors);
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
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.ElectrocellGeneratorRecipes;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (this.lEUt > 0) {
            addEnergyOutput((this.lEUt * mEfficiency) / 10000);
            return true;
        }
        return true;
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

        ArrayList<FluidStack> fluidStacks = getStoredFluids();
        Object2IntOpenHashMap<Fluid> availableFluids = getAvailableFluidAmounts(fluidStacks);

        for (GTRecipe recipe : GTNLRecipeMaps.ElectrocellGeneratorRecipes.getAllRecipes()) {
            if (depleteInput(mLeftInputBusses, recipe.mInputs[0], true)
                && depleteInput(mRightInputBusses, recipe.mInputs[1], true)) {
                if (recipe.mFluidInputs != null && !availableFluids.isEmpty()) {
                    double multiplier = resolveFluidMultiplier(recipe, availableFluids);

                    if (matchedFluid != null) {
                        depleteInput(mLeftInputBusses, recipe.mInputs[0]);
                        depleteInput(mRightInputBusses, recipe.mInputs[1]);
                        mMaxProgresstime = recipe.mDuration;
                        generatorValue = recipe.mSpecialValue / 100D * multiplier;
                        lEUt = recipe.getMetadataOrDefault(ElectrocellGeneratorMetadata.INSTANCE, 0L);
                        lastEUt = lEUt;
                        outputFluid = recipe.mFluidOutputs[0];

                        List<ItemStack> outputList = new ArrayList<>(recipe.mOutputs.length);
                        for (int i = 0; i < recipe.mOutputs.length; i++) {
                            int chance = recipe.getOutputChance(i);
                            if (random.nextInt(10000) < chance) {
                                outputList.add(recipe.mOutputs[i]);
                            }
                        }
                        mOutputItems = outputList.toArray(new ItemStack[outputList.size()]);

                        for (MTEHatchMaintenance maintenance : mMaintenanceHatches) {
                            if (maintenance instanceof IConfigurationMaintenance customMaintenance
                                && customMaintenance.isConfiguration()) {
                                mMaxProgresstime = (int) Math
                                    .max(1, mMaxProgresstime * customMaintenance.getConfigTime() / 100.0);
                                break;
                            }
                        }

                        return CheckRecipeResultRegistry.SUCCESSFUL;
                    }
                }
            }
        }

        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings2, 0);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
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
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ElectrocellGeneratorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_05"))
            .beginStructureBlock(11, 5, 3, true)
            .addDynamoHatch(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_ElectrocellGenerator_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        aNBT.setDouble("generatorValue", generatorValue);
        aNBT.setLong("lastEUt", lastEUt);

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
        lastEUt = aNBT.getLong("lastEUt");

        if (aNBT.hasKey("matchedFluid")) {
            matchedFluid = FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag("matchedFluid"));
        }

        if (aNBT.hasKey("outputFluid")) {
            outputFluid = FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag("outputFluid"));
        }
    }

    public boolean addLeftBusToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity != null) {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof MTEHatchInputBus inputBus) {
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
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof MTEHatchInputBus inputBus) {
                if (mRightInputBusses != null) return false;
                mRightInputBusses = inputBus;
                mRightInputBusses.updateTexture(aBaseCasingIndex);
                return true;
            }
        }
        return false;
    }

    public Object2IntOpenHashMap<Fluid> getAvailableFluidAmounts(List<FluidStack> fluidStacks) {
        Object2IntOpenHashMap<Fluid> availableFluids = new Object2IntOpenHashMap<>(fluidStacks.size());
        for (FluidStack storedFluid : fluidStacks) {
            if (storedFluid != null && storedFluid.getFluid() != null) {
                availableFluids.addTo(storedFluid.getFluid(), storedFluid.amount);
            }
        }
        return availableFluids;
    }

    public double resolveFluidMultiplier(GTRecipe recipe, Object2IntOpenHashMap<Fluid> availableFluids) {
        double multiplier = 1;
        matchedFluid = null;
        for (int i = 0; i < recipe.mFluidInputs.length; i++) {
            FluidStack recipeFluid = recipe.mFluidInputs[i];
            if (recipeFluid == null || recipeFluid.getFluid() == null) {
                continue;
            }
            if (availableFluids.getInt(recipeFluid.getFluid()) <= 0) {
                continue;
            }
            matchedFluid = recipeFluid.copy();
            if (i < FLUID_MULTIPLIERS.length) {
                multiplier = FLUID_MULTIPLIERS[i];
            }
        }
        return multiplier;
    }

    public boolean depleteInput(MTEHatchInputBus hatchInput, ItemStack aStack) {
        return depleteInput(hatchInput, aStack, false);
    }

    public boolean depleteInput(MTEHatchInputBus hatchInput, ItemStack aStack, boolean simulate) {
        if (GTUtility.isStackInvalid(aStack) || hatchInput == null) return false;

        hatchInput.mRecipeMap = getRecipeMap();
        IGregTechTileEntity baseMetaTileEntity = hatchInput.getBaseMetaTileEntity();
        if (baseMetaTileEntity == null) return false;

        int size = baseMetaTileEntity.getSizeInventory();
        if (size <= 0) return false;

        for (int i = 0; i < size; i++) {
            ItemStack stackInSlot = baseMetaTileEntity.getStackInSlot(i);
            if (GTUtility.areStacksEqual(aStack, stackInSlot)) {
                if (stackInSlot.stackSize >= aStack.stackSize) {
                    if (simulate) {
                        return true;
                    }
                    baseMetaTileEntity.decrStackSize(i, aStack.stackSize);
                    return true;
                }
            }
        }
        return false;
    }
}
