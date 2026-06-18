package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.BlockIcons;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
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
import gregtech.api.structure.error.StructureError;
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

    public final ArrayList<ItemStack> dimensionRecipeInputs = new ArrayList<>(2);

    public LargeGasCollector(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeGasCollector(String aName) {
        super(aName);
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d == ForgeDirection.UP;
    }

    @Override
    public IStructureDefinition<LargeGasCollector> getStructureDefinition() {
        return StructureDefinition.<LargeGasCollector>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                buildHatchAdder(LargeGasCollector.class).casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.OutputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy))
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 0))))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 15))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings3, 10))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings6, 5))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 20);
    }

    @Override
    @NotNull
    public CheckRecipeResult doCheckRecipe() {
        if (hasIntegratedCircuitInStoredInputs()) {
            return super.doCheckRecipe();
        }

        int dimID = getBaseMetaTileEntity().getWorld().provider.dimensionId;
        fillDimensionRecipeInputs(dimID);

        CheckRecipeResult result = CheckRecipeResultRegistry.NO_RECIPE;

        for (IDualInputHatch dualInputHatch : mDualInputHatches) {
            ItemStack[] sharedItems = dualInputHatch.getSharedItems();
            for (var it = dualInputHatch.inventories(); it.hasNext();) {
                IDualInputInventory slot = it.next();

                if (!slot.isEmpty()) {
                    if (slot instanceof IDualInputInventoryWithPattern withPattern) {
                        if (!processingLogic.tryCachePossibleRecipesFromPattern(withPattern)) {
                            continue;
                        }
                    }

                    loadGasCollectorRecipeInputs(sharedItems, slot, dimensionRecipeInputs);
                    CheckRecipeResult foundResult = processRecipeSearch();
                    if (foundResult.wasSuccessful()) {
                        return foundResult;
                    }
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) {
                        result = foundResult;
                    }
                }
            }
        }

        result = checkRecipeForCustomHatches(result);
        if (result.wasSuccessful()) {
            return result;
        }

        short hatchColors = getHatchColors();
        boolean doColorChecking = hatchColors != 0;
        if (!doColorChecking) hatchColors = 0b1;

        for (byte color = 0; color < (doColorChecking ? 16 : 1); color++) {
            if (isColorAbsent(hatchColors, color)) continue;
            replaceRecipeSearchFluids(getStoredFluidsForColor(Optional.of(color)));
            setProcessingInputFluids(recipeSearchFluidInputs);
            if (isInputSeparationEnabled()) {
                if (mInputBusses.isEmpty()) {
                    replaceRecipeSearchItems(dimensionRecipeInputs);
                    setProcessingInputItems(recipeSearchItemInputs);
                    CheckRecipeResult foundResult = processRecipeSearch();
                    if (foundResult.wasSuccessful()) return foundResult;
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                } else {
                    for (MTEHatchInputBus bus : mInputBusses) {
                        if (bus instanceof MTEHatchCraftingInputME || bus instanceof SuperCraftingInputHatchME)
                            continue;
                        byte busColor = bus.getColor();
                        if (busColor != -1 && busColor != color) continue;
                        recipeSearchItemInputs.clear();
                        recipeSearchItemInputs
                            .ensureCapacity(bus.getSizeInventory() + dimensionRecipeInputs.size() + 1);
                        collectBusInputs(bus, recipeSearchItemInputs);
                        recipeSearchItemInputs.addAll(dimensionRecipeInputs);
                        if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                            recipeSearchItemInputs.add(getControllerSlot());
                        }
                        setProcessingInputItems(recipeSearchItemInputs);
                        CheckRecipeResult foundResult = processRecipeSearch();
                        if (foundResult.wasSuccessful()) return foundResult;
                        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                    }
                }
            } else {
                replaceRecipeSearchItems(getStoredInputsForColor(Optional.of(color)));
                recipeSearchItemInputs.addAll(dimensionRecipeInputs);
                if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                    recipeSearchItemInputs.add(getControllerSlot());
                }
                setProcessingInputItems(recipeSearchItemInputs);
                CheckRecipeResult foundResult = processRecipeSearch();
                if (foundResult.wasSuccessful()) return foundResult;
                if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
            }
        }
        return result;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1000000;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_LARGE_GAS_COLLECTOR_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(BlockIcons.OVERLAY_FRONT_LARGE_GAS_COLLECTOR)
                    .extFacing()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings2, 0);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.GasCollectorRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("LargeGasCollectorRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeGasCollector_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeGasCollector_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_LargeGasCollector_02"))
            .addTecTechHatchInfo()
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

    public boolean hasIntegratedCircuitInStoredInputs() {
        for (ItemStack item : getAllStoredInputs()) {
            if (item != null && Objects.equals(item.getItem(), ItemList.Circuit_Integrated.getItem())) {
                return true;
            }
        }
        return false;
    }

    public void fillDimensionRecipeInputs(int dimID) {
        dimensionRecipeInputs.clear();
        if (dimID == 0) {
            dimensionRecipeInputs.add(GTUtility.getIntegratedCircuit(1));
            return;
        }
        if (dimID == 1) {
            dimensionRecipeInputs.add(GTUtility.getIntegratedCircuit(3));
            dimensionRecipeInputs.add(new ItemStack(ModBlocks.getBlock("ED"), 1));
            return;
        }
        if (dimID == -1) {
            dimensionRecipeInputs.add(GTUtility.getIntegratedCircuit(5));
            dimensionRecipeInputs.add(new ItemStack(ModBlocks.getBlock("Ne"), 1));
        }
    }

    public void loadGasCollectorRecipeInputs(ItemStack[] sharedItems, IDualInputInventory inventory,
        List<ItemStack> additionalItems) {
        loadDualInputBuffers(sharedItems, inventory);
        if (additionalItems.isEmpty()) {
            return;
        }
        recipeSearchItemInputs.ensureCapacity(recipeSearchItemInputs.size() + additionalItems.size());
        for (ItemStack additionalItem : additionalItems) {
            if (additionalItem != null) {
                recipeSearchItemInputs.add(additionalItem);
            }
        }
        setProcessingInputItems(recipeSearchItemInputs);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeGasCollector(this.mName);
    }
}
