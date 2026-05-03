package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gtPlusPlus.core.block.ModBlocks.blockCasings2Misc;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.api.IControllerUpgrade;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchMultiInput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.recipe.metadata.PCBFactoryTierKey;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.tileentities.machines.IRecipeProcessingAwareHatch;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import lombok.Getter;
import lombok.Setter;
import tectech.thing.CustomItemList;

public class PCBFactory extends WirelessEnergyMultiMachineBase<PCBFactory>
    implements ISurvivalConstructable, IControllerUpgrade {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String STRUCTURE_PIECE_MAIN_T2 = "main_t2";
    private static final String STRUCTURE_PIECE_MAIN_T3 = "main_t3";
    private static final String PCBF_T1_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/pcb_factory_t1";
    private static final String PCBF_T2_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/pcb_factory_t2";
    private static final String PCBF_T3_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/pcb_factory_t3";
    private static final int HORIZONTAL_OFF_SET = 6;
    private static final int VERTICAL_OFF_SET = 12;
    private static final int DEPTH_OFF_SET = 1;
    private static final int HORIZONTAL_OFF_SET_T2 = 9;
    private static final int VERTICAL_OFF_SET_T2 = 26;
    private static final int DEPTH_OFF_SET_T2 = -14;
    private static final int HORIZONTAL_OFF_SET_T3 = 23;
    private static final int VERTICAL_OFF_SET_T3 = 16;
    private static final int DEPTH_OFF_SET_T3 = -17;
    private static final String[][] shape = StructureUtils.readStructureFromFile(PCBF_T1_STRUCTURE_FILE_PATH);
    private static final String[][] shape_t2 = StructureUtils.readStructureFromFile(PCBF_T2_STRUCTURE_FILE_PATH);
    private static final String[][] shape_t3 = StructureUtils.readStructureFromFile(PCBF_T3_STRUCTURE_FILE_PATH);

    public static final ItemStack[] REQUIRED_ITEMS = new ItemStack[] {
        GTUtility.copyAmountUnsafe(256, ItemList.InfinityCooledCasing.get(1)),
        GTUtility.copyAmountUnsafe(256, ItemList.ReinforcedPhotolithographicFrameworkCasing.get(1)),
        GTUtility.copyAmountUnsafe(128, CustomItemList.Godforge_HarmonicPhononTransmissionConduit.get(1)),
        GTUtility.copyAmountUnsafe(128, CustomItemList.EOH_Reinforced_Spatial_Casing.get(1)),
        GTUtility.copyAmountUnsafe(128, GregtechItemList.SpaceTimeContinuumRipper.get(1)),
        GTOreDictUnificator.get(OrePrefixes.wireGt16, Materials.SuperconductorUMV, 64),
        GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Netherite, 16),
        GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.TranscendentMetal, 16),
        GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.ProtoHalkonite, 16),
        GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.SixPhasedCopper, 16),
        GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.SpaceTime, 16), ItemList.ZPM6.get(8),
        ItemList.Robot_Arm_UIV.get(64), ItemList.Conveyor_Module_UIV.get(64), ItemList.Field_Generator_UEV.get(64) };

    public static FluidStack DISTILLED_WATER = GTModHandler.getDistilledWater(7500);
    public static ItemStack T1NANITE = GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 1);
    public static ItemStack T2NANITE = GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 1);
    public static ItemStack T3NANITE = GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Gold, 1);
    public static FluidStack[] PURIFIED_WATER = new FluidStack[] { Materials.Grade1PurifiedWater.getFluid(1),
        Materials.Grade3PurifiedWater.getFluid(1), Materials.Grade5PurifiedWater.getFluid(1),
        Materials.Grade7PurifiedWater.getFluid(1), Materials.Grade2PurifiedWater.getFluid(1),
        Materials.Grade4PurifiedWater.getFluid(1), Materials.Grade6PurifiedWater.getFluid(1),
        Materials.Grade8PurifiedWater.getFluid(1) };

    @Getter
    public ItemStack[] storedUpgradeWindowItems = new ItemStack[16];
    @Getter
    public ItemStackHandler upgradeInputSlotHandler = new ItemStackHandler(16);
    @Getter
    public int[] upgradePaidCosts = new int[REQUIRED_ITEMS.length];

    @Getter
    @Setter
    public boolean upgradeConsumed = false;

    public ArrayList<MTEHatchInput> mWaterInputHatches = new ArrayList<>();
    public int machineTier;

    public PCBFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public PCBFactory(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new PCBFactory(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }

    @Override
    public void updateHatchTexture() {
        super.updateHatchTexture();
        for (MTEHatch h : mWaterInputHatches)
            h.updateTexture(StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings10, 3));
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 7);
    }

    @Override
    public void startRecipeProcessing() {
        for (MTEHatchInput hatch : GTUtility.validMTEList(mWaterInputHatches)) {
            if (hatch instanceof IRecipeProcessingAwareHatch aware) {
                aware.startRecipeProcessing();
            }
        }
        super.startRecipeProcessing();
    }

    @Override
    public void endRecipeProcessing() {
        for (MTEHatchInput hatch : GTUtility.validMTEList(mWaterInputHatches)) {
            if (hatch instanceof IRecipeProcessingAwareHatch aware) {
                setResultIfFailure(aware.endRecipeProcessing(this));
            }
        }
        super.endRecipeProcessing();
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        CheckRecipeResult result = super.checkProcessing();
        if (!result.wasSuccessful()) return result;

        if (!wirelessMode) {
            depletePurifiedWater();
        }

        List<ItemStack> expandedOutputs = new ArrayList<>();

        for (ItemStack is : mOutputItems) {
            if (is == null) continue;

            long doubledAmount = (long) is.stackSize * 2;
            while (doubledAmount > 0) {
                ItemStack copy = is.copy();
                int currentTransfer = (int) Math.min(doubledAmount, Integer.MAX_VALUE);
                copy.stackSize = currentTransfer;
                expandedOutputs.add(copy);
                doubledAmount -= currentTransfer;
            }
        }

        mOutputItems = expandedOutputs.toArray(new ItemStack[0]);

        return result;
    }

    @Override
    public CheckRecipeResult wirelessModeProcessOnce(ItemStack stack) {
        CheckRecipeResult result = super.wirelessModeProcessOnce(null);
        if (!result.wasSuccessful()) return result;
        depletePurifiedWater();
        return result;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @Override
            public @NotNull CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                Materials naniteMaterial = recipe.getMetadata(GTRecipeConstants.PCB_NANITE_MATERIAL);
                if (naniteMaterial != null) {
                    ItemStack nanite = GTOreDictUnificator.get(OrePrefixes.nanite, naniteMaterial, 1);
                    boolean nanitesFound = false;
                    for (ItemStack stored : getAllStoredInputs()) {
                        if (GTUtility.areStacksEqual(stored, nanite)) {
                            nanitesFound = true;
                            break;
                        }
                    }
                    if (!nanitesFound) {
                        return SimpleCheckRecipeResult.ofFailure("nanites_missing");
                    }
                }

                int requiredPCBTier = recipe.getMetadataOrDefault(PCBFactoryTierKey.INSTANCE, 1);
                if (requiredPCBTier > machineTier)
                    return CheckRecipeResultRegistry.insufficientMachineTier(requiredPCBTier);

                long parallel = 0;

                for (FluidStack fluidStack : getStoredWater()) {
                    if (GTUtility.areFluidsEqual(fluidStack, PURIFIED_WATER[requiredPCBTier - 1])) {
                        parallel += (long) (fluidStack.amount / 100d
                            * GTUtility.powInt(2, machineTier - requiredPCBTier));
                    }
                    if (GTUtility.areFluidsEqual(fluidStack, PURIFIED_WATER[requiredPCBTier + 3])) {
                        parallel += (long) (fluidStack.amount / 50d
                            * GTUtility.powInt(2, machineTier - requiredPCBTier));
                    }
                }

                maxParallel = (int) GTUtility.min(parallel, getTrueParallel());

                if (maxParallel <= 0) return CheckRecipeResultRegistry.NO_RECIPE;

                return super.validateRecipe(recipe);
            }

            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setHeatOC(getHeatOC())
                    .setMachineHeat(getMachineHeat())
                    .setHeatDiscount(getHeatDiscount())
                    .setAmperageOC(getAmperageOC())
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setPerfectOC(getPerfectOC())
                    .setMaxTierSkips(getMaxTierSkip())
                    .setMaxOverclocks(getMaxOverclocks());
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @Override
    public boolean getPerfectOC() {
        return machineTier >= 3;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.pcbFactoryRecipes;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("PCBFactoryRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PCBFactory_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PCBFactory_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PCBFactory_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PCBFactory_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PCBFactory_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PCBFactory_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PCBFactory_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PCBFactory_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PCBFactory_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PCBFactory_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PCBFactory_10"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addTecTechHatchInfo()
            .beginVariableStructureBlock(13, 47, 16, 30, 14, 46, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_PCBFactory_Casing"))
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_PCBFactory_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_PCBFactory_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_PCBFactory_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_PCBFactory_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_PCBFactory_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<PCBFactory> getStructureDefinition() {
        return StructureDefinition.<PCBFactory>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addShape(STRUCTURE_PIECE_MAIN_T2, StructureUtility.transpose(shape_t2))
            .addShape(STRUCTURE_PIECE_MAIN_T3, StructureUtility.transpose(shape_t3))
            .addElement('A', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 3))
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(PCBFactory.class)
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
                    .buildAndChain(StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7)))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings6, 7))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 0))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))
            .addElement('F', GTStructureUtility.chainAllGlasses())
            .addElement('G', GTStructureUtility.ofFrame(Materials.Neutronium))
            .addElement(
                'H',
                StructureUtility.ofBlockAnyMeta(
                    Block.getBlockFromItem(
                        MaterialsAlloy.MARAGING300.getFrameBox(1)
                            .getItem())))
            .addElement(
                'I',
                GTStructureUtility.buildHatchAdder(PCBFactory.class)
                    .casingIndex(StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings10, 3))
                    .dot(1)
                    .atLeast(CustomHatchElement.WaterInputHatch)
                    .buildAndChain(GregTechAPI.sBlockCasings10, 3))
            .addElement('J', StructureUtility.ofBlock(sBlockCasingsTT, 8))
            .addElement('K', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 5))
            .addElement('L', StructureUtility.ofBlock(blockCasings2Misc, 12))
            .addElement('M', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 5))
            .addElement('N', StructureUtility.ofBlock(sBlockCasingsTT, 0))
            .addElement('O', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 12))
            .addElement('P', StructureUtility.ofBlock(sBlockCasingsTT, 7))
            .addElement('Q', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 13))
            .addElement('R', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 7))
            .addElement('S', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 11))
            .addElement('T', StructureUtility.ofBlock(sBlockCasingsTT, 6))
            .addElement('U', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        machineTier = getMachineTier();
        if (machineTier == 0) return false;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET)) {
            return false;
        }
        if (machineTier >= 2
            && !checkPiece(STRUCTURE_PIECE_MAIN_T2, HORIZONTAL_OFF_SET_T2, VERTICAL_OFF_SET_T2, DEPTH_OFF_SET_T2)) {
            machineTier = 1;
        }
        if (machineTier >= 3
            && !checkPiece(STRUCTURE_PIECE_MAIN_T3, HORIZONTAL_OFF_SET_T3, VERTICAL_OFF_SET_T3, DEPTH_OFF_SET_T3)) {
            machineTier = 2;
        }
        if (!checkHatch()) {
            return false;
        }
        setupParameters();
        return true;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mWaterInputHatches.clear();
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        dropStoredUpgradeItems(getBaseMetaTileEntity());
    }

    public int getMachineTier() {
        ItemStack stack = getControllerSlot();
        if (GTUtility.areStacksEqual(stack, T1NANITE)) return 1;
        if (GTUtility.areStacksEqual(stack, T2NANITE)) return 2;
        if (GTUtility.areStacksEqual(stack, T3NANITE)) {
            if (upgradeConsumed) {
                return 4;
            } else {
                return 3;
            }
        }
        return 0;
    }

    @Override
    public double getEUtDiscount() {
        return (wirelessUpgrade ? 0.6 : 0.8) - 0.1 * (machineTier - 1);
    }

    @Override
    public double getDurationModifier() {
        double modifier = 1.0 / (wirelessUpgrade ? 8.0 : 3.0 + (machineTier - 1) * 0.1)
            - (Math.max(0, mParallelTier - 1) / 50.0);
        return Math.max(0.00001, modifier);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (mStartUpCheck > 0 || !getBaseMetaTileEntity().isServerSide()) return;
        if (isAllowedToWork() && aTick % 100 == 0) {
            startRecipeProcessing();
            if (!depleteWaterInput(DISTILLED_WATER)) {
                stopMachine(ShutDownReasonRegistry.NONE);
            }
            endRecipeProcessing();
        }
    }

    public ArrayList<FluidStack> getStoredWater() {
        ArrayList<FluidStack> rList = new ArrayList<>();
        Map<Fluid, FluidStack> inputsFromME = new HashMap<>();
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mWaterInputHatches)) {
            if (tHatch instanceof MTEHatchMultiInput multiInputHatch) {
                for (FluidStack tFluid : multiInputHatch.getStoredFluid()) {
                    if (tFluid != null) {
                        rList.add(tFluid);
                    }
                }
            } else if (tHatch instanceof MTEHatchInputME meHatch) {
                for (FluidStack fluidStack : meHatch.getStoredFluids()) {
                    if (fluidStack != null) {
                        // Prevent the same fluid from different ME hatches from being recognized
                        inputsFromME.put(fluidStack.getFluid(), fluidStack);
                    }
                }
            } else {
                FluidStack fillableStack = tHatch.getFillableStack();
                if (fillableStack != null) {
                    rList.add(fillableStack);
                }
            }
        }

        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;
    }

    public void depletePurifiedWater() {
        int tier = ((GTNLProcessingLogic) processingLogic).getLastRecipe()
            .getMetadataOrDefault(PCBFactoryTierKey.INSTANCE, 1);
        int parallel = processingLogic.getCurrentParallels();

        for (FluidStack fluidStack : getStoredWater()) {
            if (parallel <= 0) break;
            // 先扣“高级净化水”（tier + 3）
            if (GTUtility.areFluidsEqual(fluidStack, PURIFIED_WATER[tier + 3])) {
                int deductAmount = 50 / (int) GTUtility.powInt(2, machineTier - tier);
                deductAmount = Math.max(1, deductAmount);

                int timesToDeduct = fluidStack.amount / deductAmount;
                timesToDeduct = Math.min(parallel, timesToDeduct); // 严格按本次并行

                if (timesToDeduct > 0) {
                    fluidStack.amount -= deductAmount * timesToDeduct;
                    parallel -= timesToDeduct;
                }
            }

            if (parallel <= 0) break;

            // 再扣“低级净化水”（tier - 1）
            if (GTUtility.areFluidsEqual(fluidStack, PURIFIED_WATER[tier - 1])) {
                int deductAmount = 100 / (int) GTUtility.powInt(2, machineTier - tier);
                deductAmount = Math.max(1, deductAmount);

                int timesToDeduct = fluidStack.amount / deductAmount;
                timesToDeduct = Math.min(parallel, timesToDeduct); // 严格按本次并行

                if (timesToDeduct > 0) {
                    fluidStack.amount -= deductAmount * timesToDeduct;
                    parallel -= timesToDeduct;
                }
            }
        }
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mWaterInputHatches)) tHatch.updateSlots();
    }

    public boolean depleteWaterInput(FluidStack fluidStack) {
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mWaterInputHatches)) {
            FluidStack tLiquid = tHatch.drain(ForgeDirection.UNKNOWN, fluidStack, false);
            if (tLiquid != null && tLiquid.amount >= fluidStack.amount) {
                tLiquid = tHatch.drain(ForgeDirection.UNKNOWN, fluidStack, true);
                return tLiquid != null && tLiquid.amount >= fluidStack.amount;
            }
        }
        return false;
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        super.setItemNBT(aNBT);
        saveUpgradeNBTData(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        saveUpgradeNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        loadUpgradeNBTData(aNBT);
    }

    @Override
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);
        screenElements.widget(
            TextWidget.dynamicText(() -> new Text(StatCollector.translateToLocal("Info_PCBFactory_00") + machineTier))
                .setDefaultColor(COLOR_TEXT_WHITE.get()))
            .widget(
                new FakeSyncWidget.IntegerSyncer(() -> machineTier, tier -> machineTier = tier).setSynced(true, false));
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        createUpgradeButton(builder, buildContext);
    }

    @Override
    public void createUpgradeButton(ModularWindow.Builder builder, UIBuildContext buildContext) {
        buildContext.addSyncedWindow(getUpgradeWindowId(), this::createConsumeWindow);
        builder.widget(new FakeSyncWidget.BooleanSyncer(this::isUpgradeConsumed, this::setUpgradeConsumed));

        builder.widget(new ButtonWidget().setOnClick((click, widget) -> {
            if (!widget.isClient()) {
                widget.getContext()
                    .openSyncedWindow(getUpgradeWindowId());
            }
        })
            .setBackground(
                () -> new IDrawable[] {
                    isUpgradeConsumed() ? GTUITextures.BUTTON_STANDARD_PRESSED : GTUITextures.BUTTON_STANDARD,
                    GTUITextures.OVERLAY_BUTTON_ARROW_GREEN_UP })
            .addTooltip(getUpgradeButtonTooltip())
            .setTooltipShowUpDelay(5)
            .setEnabled(widget -> machineTier >= 3)
            .setPos(getUpgradeButtonPos())
            .setSize(16, 16));
    }

    @Override
    public ItemStack[] getUpgradeRequiredItems() {
        return REQUIRED_ITEMS;
    }

    @Override
    public String getUpgradeButtonTooltip() {
        return StatCollector.translateToLocal("Info_PCBFactory_01");
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
        if (stackSize.stackSize >= 2) buildPiece(
            STRUCTURE_PIECE_MAIN_T2,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET_T2,
            VERTICAL_OFF_SET_T2,
            DEPTH_OFF_SET_T2);
        if (stackSize.stackSize >= 3) buildPiece(
            STRUCTURE_PIECE_MAIN_T3,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET_T3,
            VERTICAL_OFF_SET_T3,
            DEPTH_OFF_SET_T3);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        int built;

        built = survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            elementBudget,
            env,
            false,
            true);

        if (built >= 0) return built;

        if (stackSize.stackSize >= 2) {
            built = survivalBuildPiece(
                STRUCTURE_PIECE_MAIN_T2,
                stackSize,
                HORIZONTAL_OFF_SET_T2,
                VERTICAL_OFF_SET_T2,
                DEPTH_OFF_SET_T2,
                elementBudget,
                env,
                false,
                true);
        }

        if (built >= 0) return built;

        if (stackSize.stackSize >= 3) {
            built = survivalBuildPiece(
                STRUCTURE_PIECE_MAIN_T3,
                stackSize,
                HORIZONTAL_OFF_SET_T3,
                VERTICAL_OFF_SET_T3,
                DEPTH_OFF_SET_T3,
                elementBudget,
                env,
                false,
                true);
        }

        return built;
    }

    public boolean addWaterInputHatchToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof MTEHatchInput hatch) {
            hatch.updateTexture(aBaseCasingIndex);
            hatch.updateCraftingIcon(this.getMachineCraftingIcon());
            return mWaterInputHatches.add(hatch);
        }
        return false;
    }

    public enum CustomHatchElement implements IHatchElement<PCBFactory> {

        WaterInputHatch("GT5U.MBTT.InputHatch", PCBFactory::addWaterInputHatchToMachineList, MTEHatchInput.class) {

            @Override
            public long count(PCBFactory t) {
                return t.mWaterInputHatches.size();
            }
        };

        private final String name;
        private final List<Class<? extends IMetaTileEntity>> mteClasses;
        private final IGTHatchAdder<PCBFactory> adder;

        @SafeVarargs
        CustomHatchElement(String name, IGTHatchAdder<PCBFactory> adder,
            Class<? extends IMetaTileEntity>... mteClasses) {
            this.name = name;
            this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
            this.adder = adder;
        }

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return mteClasses;
        }

        @Override
        public String getDisplayName() {
            return GTUtility.translate(name);
        }

        public IGTHatchAdder<? super PCBFactory> adder() {
            return adder;
        }
    }

}
