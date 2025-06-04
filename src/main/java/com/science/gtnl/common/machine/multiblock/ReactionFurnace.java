package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;
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
import com.science.gtnl.common.machine.multiMachineClasses.GTMMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.blocks.BlockCasings8;
import gregtech.common.blocks.BlockCasings9;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;

public class ReactionFurnace extends GTMMultiMachineBase<ReactionFurnace> implements ISurvivalConstructable {

    private static final long RECIPE_EUT = 4;
    private static final int RECIPE_DURATION = 128;
    public static final String STRUCTURE_PIECE_MAIN = "main";
    private static IStructureDefinition<ReactionFurnace> STRUCTURE_DEFINITION = null;
    public static final String RF_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/reaction_furnace";
    public static final int CASING_INDEX = ((BlockCasings8) GregTechAPI.sBlockCasings8).getTextureIndex(7);
    public final int HORIZONTAL_OFF_SET = 15;
    public final int VERTICAL_OFF_SET = 18;
    public final int DEPTH_OFF_SET = 3;
    public static String[][] shape = StructureUtils.readStructureFromFile(RF_STRUCTURE_FILE_PATH);

    public ReactionFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ReactionFurnace(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ReactionFurnace(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_ON)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return ((BlockCasings9) GregTechAPI.sBlockCasings9).getTextureIndex(11);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.furnaceRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ReactionFurnaceRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ReactionFurnace_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_GTMMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(31, 21, 29, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_ReactionFurnace_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ReactionFurnace_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_ReactionFurnace_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_ReactionFurnace_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<ReactionFurnace> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<ReactionFurnace>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
                .addElement('A', ofBlock(sBlockCasings9, 11))
                .addElement('B', ofBlock(BlockLoader.MetaCasing, 14))
                .addElement('C', ofBlock(sBlockCasings9, 7))
                .addElement('D', ofBlock(sBlockCasings10, 3))
                .addElement('E', ofBlock(sBlockCasings8, 10))
                .addElement(
                    'F',
                    buildHatchAdder(ReactionFurnace.class).casingIndex(CASING_INDEX)
                        .dot(1)
                        .atLeast(InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                        .buildAndChain(onElementPass(x -> ++x.tCountCasing, ofBlock(sBlockCasings8, 7))))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        tCountCasing = 0;
        mParallelTier = 0;

        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) && checkHatch()) {
            return false;
        }

        energyHatchTier = checkEnergyHatchTier();
        mParallelTier = getParallelTier(aStack);
        return tCountCasing >= 115;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        List<ItemStack> tInput = getAllStoredInputs();
        long availableEUt = GTUtility.roundUpVoltage(getMaxInputVoltage());
        if (availableEUt < 4) {
            return CheckRecipeResultRegistry.insufficientPower(4);
        }
        if (tInput.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }
        int maxParallel = getMaxParallelRecipes();
        int originalMaxParallel = getMaxParallelRecipes();

        OverclockCalculator calculator = new OverclockCalculator().setEUt(availableEUt)
            .setRecipeEUt(4)
            .setDuration(64)
            .setParallel(originalMaxParallel);

        maxParallel = GTUtility.safeInt((long) (maxParallel * calculator.calculateMultiplierUnderOneTick()), 0);

        int maxParallelBeforeBatchMode = maxParallel;
        if (isBatchModeEnabled()) {
            maxParallel = GTUtility.safeInt((long) maxParallel * getMaxBatchSize(), 0);
        }

        int currentParallel = (int) Math.min(maxParallel, availableEUt / 4);
        int itemParallel = 0;
        for (ItemStack item : tInput) {
            ItemStack smeltedOutput = GTModHandler.getSmeltingOutput(item, false, null);
            if (smeltedOutput != null) {
                int parallelsLeft = currentParallel - itemParallel;
                if (parallelsLeft <= 0) break;
                itemParallel += Math.min(item.stackSize, parallelsLeft);
            }
        }

        currentParallel = itemParallel;
        if (currentParallel <= 0) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }
        int currentParallelBeforeBatchMode = Math.min(currentParallel, maxParallelBeforeBatchMode);
        calculator.setCurrentParallel(currentParallelBeforeBatchMode)
            .calculate();

        double batchMultiplierMax = 1;
        // In case batch mode enabled
        if (currentParallel > maxParallelBeforeBatchMode && calculator.getDuration() < getMaxBatchSize()) {
            batchMultiplierMax = (double) getMaxBatchSize() / calculator.getDuration();
            batchMultiplierMax = Math.min(batchMultiplierMax, (double) currentParallel / maxParallelBeforeBatchMode);
        }

        int finalParallel = (int) (batchMultiplierMax * currentParallelBeforeBatchMode);

        // Copy the getItemOutputSlots as to not mutate the output busses' slots.
        List<ItemStack> outputSlots = new ArrayList<>();
        for (ItemStack stack : getItemOutputSlots(null)) {
            if (stack != null) {
                outputSlots.add(stack.copy());
            } else {
                outputSlots.add(null);
            }
        }

        boolean hasMEOutputBus = false;
        for (final MTEHatch bus : validMTEList(mOutputBusses)) {
            if (bus instanceof MTEHatchOutputBusME meBus) {
                if (!meBus.isLocked() && meBus.canAcceptItem()) {
                    hasMEOutputBus = true;
                    break;
                }
            }
        }
        // Consume items and generate outputs
        ArrayList<ItemStack> smeltedOutputs = new ArrayList<>();
        int toSmelt = finalParallel;
        for (ItemStack item : tInput) {
            ItemStack smeltedOutput = GTModHandler.getSmeltingOutput(item, false, null);
            if (smeltedOutput != null) {
                int maxOutput = 0;
                int remainingToSmelt = Math.min(toSmelt, item.stackSize);

                if (hasMEOutputBus) {
                    // Has an unlocked ME Output Bus and therefore can always fit the full stack
                    maxOutput = remainingToSmelt;
                } else {

                    // Calculate how many of this output can fit in the output slots
                    int needed = remainingToSmelt;
                    ItemStack outputType = smeltedOutput.copy();
                    outputType.stackSize = 1;

                    for (int i = 0; i < outputSlots.size(); i++) {
                        ItemStack slot = outputSlots.get(i);
                        if (slot == null) {
                            // Empty slot: can fit a full stack
                            int canFit = Math.min(needed, outputType.getMaxStackSize());
                            ItemStack newStack = outputType.copy();
                            newStack.stackSize = canFit;
                            outputSlots.set(i, newStack); // Fill the slot
                            maxOutput += canFit;
                            needed -= canFit;
                        } else if (slot.isItemEqual(outputType)) {
                            int canFit;
                            // Check for locked ME Output bus
                            if (slot.stackSize == 65) {
                                canFit = needed;
                            } else {
                                // Same type: can fit up to max stack size
                                int space = outputType.getMaxStackSize() - slot.stackSize;
                                canFit = Math.min(needed, space);
                            }
                            slot.stackSize += canFit;
                            maxOutput += canFit;
                            needed -= canFit;
                            // No need to set, since slot is a reference
                        }
                        if (needed <= 0) break;
                    }
                }

                // If void protection is enabled, only process what fits
                int toProcess = protectsExcessItem() ? maxOutput : remainingToSmelt;

                if (toProcess > 0) {
                    ItemStack outputStack = smeltedOutput.copy();
                    outputStack.stackSize *= toProcess;
                    smeltedOutputs.add(outputStack);

                    item.stackSize -= toProcess;
                    toSmelt -= toProcess;
                    if (toSmelt <= 0) break;
                }
            }
        }
        if (smeltedOutputs.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        this.mOutputItems = smeltedOutputs.toArray(new ItemStack[0]);

        this.mEfficiency = 10000 - (getIdealStatus() - getRepairStatus()) * 1000;
        this.mEfficiencyIncrease = 10000;
        this.mMaxProgresstime = (int) (calculator.getDuration() * batchMultiplierMax);
        this.lEUt = calculator.getConsumption();
        if (this.lEUt > 0) {
            this.lEUt = -this.lEUt;
        }
        this.updateSlots();

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public int getMaxParallelRecipes() {
        if (mParallelTier <= 2) {
            return 8;
        } else {
            return (int) Math.pow(4, mParallelTier - 3) * 512 - 1;
        }
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
}
