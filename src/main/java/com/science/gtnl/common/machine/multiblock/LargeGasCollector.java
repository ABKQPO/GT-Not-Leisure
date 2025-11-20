package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_LARGE_GAS_COLLECTOR;
import static com.science.gtnl.utils.enums.BlockIcons.OVERLAY_FRONT_LARGE_GAS_COLLECTOR_ACTIVE;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.ArrayUtils;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.StructureUtils;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.IDualInputInventoryWithPattern;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gtneioreplugin.plugin.block.ModBlocks;

public class LargeGasCollector extends MultiMachineBase<LargeGasCollector> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String LGC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/large_gas_collector";
    private static final int HORIZONTAL_OFF_SET = 2;
    private static final int VERTICAL_OFF_SET = 2;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[][] shape = StructureUtils.readStructureFromFile(LGC_STRUCTURE_FILE_PATH);

    public LargeGasCollector(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeGasCollector(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeGasCollector(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_GAS_COLLECTOR_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_LARGE_GAS_COLLECTOR)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1000000;
    }

    @Override
    @Nonnull
    public CheckRecipeResult doCheckRecipe() {
        for (ItemStack item : getAllStoredInputs()) {
            if (item != null) {
                if (Objects.equals(item.getItem(), ItemList.Circuit_Integrated.getItem())) {
                    return super.doCheckRecipe();
                }
            }
        }

        List<ItemStack> itemInputs = new ArrayList<>();
        int dimID = getBaseMetaTileEntity().getWorld().provider.dimensionId;

        if (dimID == 0) {
            itemInputs.add(GTUtility.getIntegratedCircuit(1));
        } else if (dimID == 1) {
            itemInputs.add(GTUtility.getIntegratedCircuit(3));
            itemInputs.add(new ItemStack(ModBlocks.getBlock("ED"), 1));
        } else if (dimID == -1) {
            itemInputs.add(GTUtility.getIntegratedCircuit(5));
            itemInputs.add(new ItemStack(ModBlocks.getBlock("Ne"), 1));
        }

        CheckRecipeResult result = CheckRecipeResultRegistry.NO_RECIPE;

        // check crafting input hatches first
        for (IDualInputHatch dualInputHatch : mDualInputHatches) {
            ItemStack[] sharedItems = dualInputHatch.getSharedItems();
            for (var it = dualInputHatch.inventories(); it.hasNext();) {
                IDualInputInventory slot = it.next();

                if (!slot.isEmpty()) {
                    // try to cache the possible recipes from pattern
                    if (slot instanceof IDualInputInventoryWithPattern withPattern) {
                        if (!processingLogic.tryCachePossibleRecipesFromPattern(withPattern)) {
                            // move on to next slots if it returns false, which means there is no possible recipes with
                            // given pattern.
                            continue;
                        }
                    }

                    ArrayUtils.addAll(sharedItems, slot.getItemInputs());
                    ArrayUtils.addAll(sharedItems, itemInputs.toArray(new ItemStack[0]));

                    processingLogic.setInputItems(sharedItems);
                    processingLogic.setInputFluids(slot.getFluidInputs());

                    CheckRecipeResult foundResult = processingLogic.process();
                    if (foundResult.wasSuccessful()) {
                        return foundResult;
                    }
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) {
                        // Recipe failed in interesting way, so remember that and continue searching
                        result = foundResult;
                    }
                }
            }
        }

        result = checkRecipeForCustomHatches(result);
        if (result.wasSuccessful()) {
            return result;
        }

        // Use hatch colors if any; fallback to color 1 otherwise.
        short hatchColors = getHatchColors();
        boolean doColorChecking = hatchColors != 0;
        if (!doColorChecking) hatchColors = 0b1;

        for (byte color = 0; color < (doColorChecking ? 16 : 1); color++) {
            if (isColorAbsent(hatchColors, color)) continue;
            processingLogic.setInputFluids(getStoredFluidsForColor(Optional.of(color)));
            if (isInputSeparationEnabled()) {
                if (mInputBusses.isEmpty()) {
                    processingLogic.setInputItems(itemInputs);
                    CheckRecipeResult foundResult = processingLogic.process();
                    if (foundResult.wasSuccessful()) return foundResult;
                    // Recipe failed in interesting way, so remember that and continue searching
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                } else {
                    for (MTEHatchInputBus bus : mInputBusses) {
                        if (bus instanceof MTEHatchCraftingInputME || bus instanceof SuperCraftingInputHatchME)
                            continue;
                        byte busColor = bus.getColor();
                        if (busColor != -1 && busColor != color) continue;
                        List<ItemStack> inputItems = new ArrayList<>();
                        for (int i = bus.getSizeInventory() - 1; i >= 0; i--) {
                            ItemStack stored = bus.getStackInSlot(i);
                            if (stored != null) inputItems.add(stored);
                        }
                        if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                            inputItems.add(getControllerSlot());
                        }
                        ArrayUtils.addAll(inputItems.toArray(new ItemStack[0]), itemInputs.toArray(new ItemStack[0]));
                        processingLogic.setInputItems(inputItems);
                        CheckRecipeResult foundResult = processingLogic.process();
                        if (foundResult.wasSuccessful()) return foundResult;
                        // Recipe failed in interesting way, so remember that and continue searching
                        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                    }
                }
            } else {
                List<ItemStack> inputItems = getStoredInputsForColor(Optional.of(color));
                if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                    inputItems.add(getControllerSlot());
                }
                ArrayUtils.addAll(inputItems.toArray(new ItemStack[0]), itemInputs.toArray(new ItemStack[0]));
                processingLogic.setInputItems(inputItems);
                CheckRecipeResult foundResult = processingLogic.process();
                if (foundResult.wasSuccessful()) return foundResult;
                // Recipe failed in interesting way, so remember that
                if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
            }
        }
        return result;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings2, 0);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipePool.GasCollectorRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeGasCollectorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeGasCollector_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeGasCollector_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeGasCollector_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(5, 5, 5, true)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_LargeGasCollector_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_LargeGasCollector_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_LargeGasCollector_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_LargeGasCollector_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_LargeGasCollector_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<LargeGasCollector> getStructureDefinition() {
        return StructureDefinition.<LargeGasCollector>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement(
                'A',
                buildHatchAdder(LargeGasCollector.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(Maintenance, OutputHatch, InputBus, OutputBus, Maintenance, Energy.or(ExoticEnergy))
                    .buildAndChain(onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings2, 0))))
            .addElement('B', ofBlock(sBlockCasings2, 15))
            .addElement('C', ofBlock(sBlockCasings3, 10))
            .addElement('D', ofBlock(sBlockCasings6, 5))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 20;
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
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d == ForgeDirection.UP;
    }
}
