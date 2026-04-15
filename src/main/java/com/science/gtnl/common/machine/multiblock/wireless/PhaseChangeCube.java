package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.science.gtnl.common.machine.hatch.HumongousSolidifierHatch;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.objects.GTDualInputPattern;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.GTStructureChannels;
import gregtech.common.tileentities.machines.IDualInputInventoryWithPattern;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSolidifier;
import tectech.thing.casing.BlockGTCasingsTT;

public class PhaseChangeCube extends WirelessEnergyMultiMachineBase<PhaseChangeCube> {

    private static final int MACHINEMODE_EXTRA = 0;
    private static final int MACHINEMODE_FLUID = 1;
    private static final int MACHINEMODE_SOLID = 2;
    private static final int HORIZONTAL_OFF_SET = 9;
    private static final int VERTICAL_OFF_SET = 9;
    private static final int DEPTH_OFF_SET = 6;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String PCC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/phase_change_cube";
    private static final String[][] shape = StructureUtils.readStructureFromFile(PCC_STRUCTURE_FILE_PATH);

    public PhaseChangeCube(String aName) {
        super(aName);
    }

    public PhaseChangeCube(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new PhaseChangeCube(this.mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("PhaseChangeCubeRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_PhaseChangeCube_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_06"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_07"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_08"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addTecTechHatchInfo()
            .beginStructureBlock(19, 19, 19, true)
            .addInputBus(StatCollector.translateToLocal("Tooltip_PhaseChangeCube_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_PhaseChangeCube_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_PhaseChangeCube_Casing"), 1)
            .addOutputHatch(StatCollector.translateToLocal("Tooltip_PhaseChangeCube_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_PhaseChangeCube_Casing"), 1)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getCasingTextureID() {
        return BlockGTCasingsTT.textureOffset;
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
    public IStructureDefinition<PhaseChangeCube> getStructureDefinition() {
        return StructureDefinition.<PhaseChangeCube>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(sBlockCasingsTT, 6))
            .addElement(
                'B',
                GTStructureUtility.buildHatchAdder(PhaseChangeCube.class)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.InputHatch,
                        HatchElement.OutputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .casingIndex(getCasingTextureID())
                    .dot(1)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasingsTT, 0))))
            .addElement(
                'C',
                GTStructureChannels.HEATING_COIL.use(
                    GTStructureUtility.activeCoils(
                        GTStructureUtility.ofCoil(PhaseChangeCube::setMCoilLevel, PhaseChangeCube::getMCoilLevel))))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 7))
            .addElement('E', StructureUtility.ofBlock(sBlockCasingsTT, 4))
            .addElement('F', GTStructureUtility.ofFrame(Materials.SuperconductorUEVBase))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 12))
            .addElement('H', StructureUtility.ofBlock(BlockLoader.metaBlockGlass, 2))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            hintsOnly,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        return this.survivalBuildPiece(
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
        return mCountCasing > 1;
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && getMCoilLevel() != HeatingCoilLevel.None;
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @Override
            public boolean tryCachePossibleRecipesFromPattern(IDualInputInventoryWithPattern inv) {
                if (machineMode != MACHINEMODE_SOLID) return super.tryCachePossibleRecipesFromPattern(inv);
                if (!inv.shouldBeCached()) {
                    return true;
                }
                if (dualInvWithPatternToRecipeCache.containsKey(inv)) {
                    activeDualInv = inv;
                    return true;
                }

                GTDualInputPattern inputs = inv.getPatternInputs();
                setInputItems(inputs.inputItems);
                setInputFluids(inputs.inputFluid);
                Set<GTRecipe> recipes = findRecipeMatches(RecipeMaps.fluidSolidifierRecipes)
                    .collect(Collectors.toSet());
                if (!recipes.isEmpty()) {
                    dualInvWithPatternToRecipeCache.put(inv, recipes);
                    activeDualInv = inv;
                    return true;
                }
                return false;
            }

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (wirelessMode && recipe.mEUt > GTValues.V[Math.min(mParallelTier + 1, 14)] * 4) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public GTNLOverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(mConfigSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier());
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkRecipeForCustomHatches(CheckRecipeResult lastResult) {
        if (machineMode != MACHINEMODE_SOLID) return lastResult;
        for (MTEHatchInput solidifierHatch : mInputHatches) {
            if (solidifierHatch instanceof HumongousSolidifierHatch hatch) {
                List<ItemStack> items = hatch.getNonConsumableItems();
                if (items != null && hatch.mStoredFluid != null) {
                    for (FluidStack fluid : hatch.mStoredFluid) {
                        if (fluid == null) continue;

                        processingLogic.setInputItems(items);
                        processingLogic.setInputFluids(fluid);

                        CheckRecipeResult foundResult = processingLogic.process();
                        if (foundResult.wasSuccessful()) {
                            return foundResult;
                        }
                        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) {
                            lastResult = foundResult;
                        }
                    }
                }
            } else if (solidifierHatch instanceof MTEHatchSolidifier hatch) {
                List<ItemStack> items = hatch.getNonConsumableItems();
                FluidStack fluid = solidifierHatch.getFluid();

                if (items != null && fluid != null) {
                    processingLogic.setInputItems(items);
                    processingLogic.setInputFluids(fluid);

                    CheckRecipeResult foundResult = processingLogic.process();
                    if (foundResult.wasSuccessful()) {
                        return foundResult;
                    }
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) {
                        // Recipe failed in interesting way, so remember that and continue searching
                        lastResult = foundResult;
                    }
                }
            }
        }
        processingLogic.clear();
        return lastResult;
    }

    @Override
    public double getEUtDiscount() {
        return super.getEUtDiscount() * Math.pow(0.80, getMCoilLevel().getTier());
    }

    @Override
    public double getDurationModifier() {
        return super.getDurationModifier() * Math.pow(0.80, getMCoilLevel().getTier());
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        if (machineMode == MACHINEMODE_EXTRA) return RecipeMaps.extractorRecipes;
        else if (machineMode == MACHINEMODE_FLUID) return RecipeMaps.fluidExtractionRecipes;
        else if (machineMode == MACHINEMODE_SOLID) return RecipeMaps.fluidSolidifierRecipes;
        return RecipeMaps.extractorRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays
            .asList(RecipeMaps.extractorRecipes, RecipeMaps.fluidExtractionRecipes, RecipeMaps.fluidSolidifierRecipes);
    }

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_METAL);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_LPF_FLUID);
        machineModeIcons.add(GTUITextures.OVERLAY_BUTTON_MACHINEMODE_COMPRESSING);
    }

    @Override
    public int nextMachineMode() {
        if (machineMode == MACHINEMODE_EXTRA) return MACHINEMODE_FLUID;
        else if (machineMode == MACHINEMODE_FLUID) return MACHINEMODE_SOLID;
        else if (machineMode == MACHINEMODE_SOLID) return MACHINEMODE_EXTRA;
        return MACHINEMODE_EXTRA;
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        this.machineMode = (this.machineMode + 1) % 3;
        GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("PhaseChangeCube_Mode_" + this.machineMode));
    }

    @Override
    public String getMachineModeName() {
        return StatCollector.translateToLocal("PhaseChangeCube_Mode_" + machineMode);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

}
