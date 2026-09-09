package com.science.gtnl.common.machine.multiblock.wireless;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler;
import com.reavaritia.utils.item.ToolHelper;
import com.science.gtnl.common.gui.modularui.TreeDiagramGui;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.GTNLStructureChannels;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLParallelHelper;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;
import com.science.gtnl.utils.recipes.data.CircuitNanitesRecipeData;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.objects.XSTR;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.material.MaterialsElements;
import lombok.Getter;
import tectech.thing.casing.TTCasingsContainer;

@IMetaTileEntity.SkipGenerateDescription
@IMetaTileEntity.SkipGenerateName
public class TreeDiagram extends WirelessEnergyMultiMachineBase<TreeDiagram> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String TD_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/tree_diagram";
    private static final String[][] shape = StructureUtils.readStructureFromFile(TD_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 95;
    private static final int VERTICAL_OFF_SET = 26;
    private static final int DEPTH_OFF_SET = 69;
    private static final String NANITE_SLOTS_NBT_KEY = "treeDiagramNaniteSlots";
    private static final int NANITE_SLOT_COUNT = 4;
    private static final double SPARSE_BINOMIAL_CHANCE_THRESHOLD = 0.125;

    public double euDiscount = 1;
    public double speedBonus = 1;
    public double failureBonus;
    public double outputCoefficient;
    public int nanitesParallel = 1;
    public int nantiesTier;

    public final double[] naniteEuDiscountModifiers = createNaniteModifierArray();
    public final double[] naniteSpeedBonusModifiers = createNaniteModifierArray();
    public final double[] naniteFailureBonusModifiers = createNaniteModifierArray();
    public final double[] naniteOutputCoefficientModifiers = createNaniteModifierArray();
    public final double[] naniteParallelModifiers = createNaniteModifierArray();
    public boolean loadingNaniteSlots;
    @Getter
    public final ItemStackHandler naniteSlotHandler = new ItemStackHandler(NANITE_SLOT_COUNT) {

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isValidNaniteStack(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (!loadingNaniteSlots) {
                refreshNaniteModifiers();
                markNaniteSlotsDirty();
            }
        }
    };

    public TreeDiagram(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public TreeDiagram(String aName) {
        super(aName);
    }

    @Override
    public String getLocalNameKey() {
        return "gtnl.machine.tree_diagram.name";
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new TreeDiagram(this.mName);
    }

    @Override
    public IStructureDefinition<TreeDiagram> getStructureDefinition() {
        return StructureDefinition.<TreeDiagram>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement(
                'A',
                GTNLStructureChannels.STRUCTURE_RENDER.use(
                    StructureUtility.ofBlocksTiered(
                        (block,
                            meta) -> block == Block.getBlockFromItem(
                                MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFrameBox(1)
                                    .getItem()) ? 1 : null,
                        ImmutableList.of(
                            Pair.of(
                                Block.getBlockFromItem(
                                    MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFrameBox(1)
                                        .getItem()),
                                0)),
                        -1,
                        (t, m) -> {},
                        t -> -1)))
            .addElement('B', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 6))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings1, 14))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 7))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 8))
            .addElement('F', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 11))
            .addElement('G', StructureUtility.ofBlock(GregTechAPI.sBlockCasings2, 8))
            .addElement('H', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 0))
            .addElement(
                'I',
                buildHatchAdder(TreeDiagram.class).casingIndex(getCasingTextureID())
                    .hint(1)
                    .atLeast(
                        HatchElement.Maintenance,
                        HatchElement.InputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.Maintenance,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility.onElementPass(
                            x -> ++x.mCountCasing,
                            StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 2))))
            .addElement('J', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 7))
            .addElement('K', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))
            .addElement('L', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 12))
            .addElement('M', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 0))
            .addElement('N', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 1))
            .addElement('O', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 11))
            .addElement('P', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 12))
            .addElement('Q', StructureUtility.ofBlock(TTCasingsContainer.sBlockCasingsBA0, 7))
            .addElement('R', StructureUtility.ofBlock(GregTechAPI.sBlockCasingsNH, 10))
            .addElement('S', StructureUtility.ofBlock(TTCasingsContainer.sBlockCasingsTT, 4))
            .addElement('T', StructureUtility.ofBlock(TTCasingsContainer.sBlockCasingsTT, 6))
            .addElement('U', StructureUtility.ofBlock(TTCasingsContainer.sBlockCasingsTT, 8))
            .addElement('V', StructureUtility.ofBlock(ModBlocks.blockSpecialMultiCasings, 15))
            .addElement('W', StructureUtility.ofBlock(BlockLoader.metaBlockGlow, 1))
            .addElement('X', StructureUtility.ofBlock(BlockLoader.metaBlockGlow, 17))
            .addElement('Y', StructureUtility.ofBlock(BlockLoader.metaBlockGlow, 25))
            .addElement('Z', StructureUtility.ofBlock(BlockLoader.metaBlockGlow, 27))
            .addElement('0', StructureUtility.ofBlock(BlockLoader.metaBlockGlow, 29))
            .addElement('1', StructureUtility.ofBlock(BlockLoader.metaBlockGlow, 31))
            .addElement('2', StructureUtility.ofBlock(BlockLoader.metaCasing, 7))
            .addElement('3', StructureUtility.ofBlock(BlockLoader.metaCasing, 19))
            .addElement('4', StructureUtility.ofBlock(BlockLoader.metaCasing02, 17))
            .build();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        if (!GTNLStructureChannels.STRUCTURE_RENDER.hasValue(stackSize)) return;
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        if (!GTNLStructureChannels.STRUCTURE_RENDER.hasValue(stackSize)) return -1;

        int realBudget = elementBudget >= 500 ? elementBudget : Math.min(500, elementBudget * 5);

        return this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            HORIZONTAL_OFF_SET,
            VERTICAL_OFF_SET,
            DEPTH_OFF_SET,
            realBudget,
            env,
            false,
            true);
    }

    @Override
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0;
    }

    @Override
    public void checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors)) return;
        setupParameters();
        checkHatch(errors);
        checkCasingMin(errors, mCountCasing, 30);
    }

    @Override
    public ProcessingLogic createProcessingLogic() {
        return new GTNLProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (wirelessMode && recipe.mEUt > GTValues.V[Math.min(mParallelTier + 1, 14)] * 4) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                if (recipe.mSpecialValue > nantiesTier) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(recipe.mSpecialValue);
                }
                return super.validateRecipe(recipe);
            }

            @NotNull
            @Override
            public GTNLParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                return new TreeDiagramParallelHelper(TreeDiagram.this).setRecipe(recipe)
                    .setItemInputs(inputItems)
                    .setFluidInputs(inputFluids)
                    .setAvailableEUt(availableVoltage * availableAmperage)
                    .setMachine(machine, protectItems, protectFluids)
                    .setRecipeLocked(recipeLockableMachine, isRecipeLocked)
                    .setMaxParallel(maxParallel)
                    .setEUtModifier(euModifier)
                    .enableBatchMode(batchSize)
                    .setConsumption(true)
                    .setOutputCalculation(true);
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

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setTag(NANITE_SLOTS_NBT_KEY, naniteSlotHandler.serializeNBT());
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey(NANITE_SLOTS_NBT_KEY)) {
            loadingNaniteSlots = true;
            naniteSlotHandler.deserializeNBT(aNBT.getCompoundTag(NANITE_SLOTS_NBT_KEY));
            IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
            if (baseMetaTileEntity == null || baseMetaTileEntity.isServerSide()) sanitizeNaniteSlots();
            loadingNaniteSlots = false;
        }
        refreshNaniteModifiers();
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (!aBaseMetaTileEntity.isServerSide()) return;

        XSTR random = new XSTR(
            aBaseMetaTileEntity.getWorld()
                .getSeed());
        for (int slot = 0; slot < NANITE_SLOT_COUNT; slot++) {
            naniteEuDiscountModifiers[slot] = randomNaniteModifier(random);
            naniteSpeedBonusModifiers[slot] = randomNaniteModifier(random);
            naniteFailureBonusModifiers[slot] = randomNaniteModifier(random);
            naniteOutputCoefficientModifiers[slot] = randomNaniteModifier(random);
            naniteParallelModifiers[slot] = randomNaniteModifier(random);
        }
        refreshNaniteModifiers();
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();
        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        if (baseMetaTileEntity == null || baseMetaTileEntity.isClientSide()) return;

        for (int slot = 0; slot < naniteSlotHandler.getSlots(); slot++) {
            ItemStack stack = naniteSlotHandler.getStackInSlot(slot);
            if (stack == null || stack.stackSize <= 0) continue;
            ToolHelper.dropItem(
                stack.copy(),
                baseMetaTileEntity.getWorld(),
                baseMetaTileEntity.getXCoord() + 0.5,
                baseMetaTileEntity.getYCoord() + 0.5,
                baseMetaTileEntity.getZCoord() + 0.5);
            naniteSlotHandler.setStackInSlot(slot, null);
        }
    }

    @Override
    public double getEUtDiscount() {
        return ((wirelessUpgrade ? 0.5 : 1) - (mParallelTier / 50.0)) * euDiscount;
    }

    @Override
    public double getDurationModifier() {
        return (1.0 / (wirelessUpgrade ? 2 : 1) - (Math.max(0, mParallelTier - 1) / 50.0)) * speedBonus;
    }

    @Override
    public int getMaxParallelRecipes() {
        return Math.min(nanitesParallel, super.getMaxParallelRecipes());
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new TreeDiagramGui(this);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTNLRecipeMaps.TreeDiagramRecipes;
    }

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 2);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int aColorIndex, boolean aActive, boolean aRedstone) {
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
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("gtnl.machine.tree_diagram.recipe_type"))
            .addPerfectOCInfo()
            .addSupportAny()
            .beginStructureBlock(194, 71, 184, true)
            .addInputHatch("0+", StatCollector.translateToLocal("gtnl.machine.tree_diagram.tooltip.casing"))
            .addInputBus("0+", StatCollector.translateToLocal("gtnl.machine.tree_diagram.tooltip.casing"))
            .addOutputBus("0+", StatCollector.translateToLocal("gtnl.machine.tree_diagram.tooltip.casing"))
            .addEnergyHatch("0+", StatCollector.translateToLocal("gtnl.machine.tree_diagram.tooltip.casing"))
            .addMaintenanceHatch("0+", StatCollector.translateToLocal("gtnl.machine.tree_diagram.tooltip.casing"))
            .addSubChannelUsage(GTNLStructureChannels.STRUCTURE_RENDER)
            .toolTipFinisher();
        return tt;
    }

    public boolean isValidNaniteStack(ItemStack stack) {
        return stack != null && CircuitNanitesRecipeData.recipeDataMap.containsKey(ItemId.createWithoutNBT(stack));
    }

    public void sanitizeNaniteSlots() {
        for (int slot = 0; slot < naniteSlotHandler.getSlots(); slot++) {
            ItemStack stack = naniteSlotHandler.getStackInSlot(slot);
            if (!isValidNaniteStack(stack)) {
                naniteSlotHandler.setStackInSlot(slot, null);
            } else if (stack.stackSize != 1) {
                ItemStack sanitizedStack = stack.copy();
                sanitizedStack.stackSize = 1;
                naniteSlotHandler.setStackInSlot(slot, sanitizedStack);
            }
        }
    }

    public void refreshNaniteModifiers() {
        double minimumEuDiscount = Double.MAX_VALUE;
        double minimumSpeedBonus = Double.MAX_VALUE;
        double maximumFailureBonus = Double.MIN_VALUE;
        double maximumOutputCoefficient = Double.MIN_VALUE;
        boolean hasActiveNanite = false;
        nanitesParallel = 1;
        nantiesTier = 0;

        for (int slot = 0; slot < naniteSlotHandler.getSlots(); slot++) {
            ItemStack stack = naniteSlotHandler.getStackInSlot(slot);
            CircuitNanitesRecipeData data = stack == null ? null
                : CircuitNanitesRecipeData.recipeDataMap.get(ItemId.createWithoutNBT(stack));
            if (data == null) continue;

            minimumEuDiscount = Math.min(minimumEuDiscount, data.euModifier * naniteEuDiscountModifiers[slot]);
            minimumSpeedBonus = Math.min(minimumSpeedBonus, data.speedBoost * naniteSpeedBonusModifiers[slot]);
            maximumFailureBonus = Math.max(maximumFailureBonus, data.failedChance * naniteFailureBonusModifiers[slot]);
            maximumOutputCoefficient = Math
                .max(maximumOutputCoefficient, data.outputMultiplier * naniteOutputCoefficientModifiers[slot]);
            nanitesParallel = saturatingAdd(nanitesParallel, scaleParallelCount(data.parallelCount, slot));
            nantiesTier = Math.max(nantiesTier, data.nantiesTier);
            hasActiveNanite = true;
        }

        if (!hasActiveNanite) {
            euDiscount = 1;
            speedBonus = 1;
            failureBonus = 0;
            outputCoefficient = 0;
            return;
        }

        euDiscount = minimumEuDiscount;
        speedBonus = minimumSpeedBonus;
        failureBonus = maximumFailureBonus;
        outputCoefficient = maximumOutputCoefficient;
    }

    public void markNaniteSlotsDirty() {
        IGregTechTileEntity baseMetaTileEntity = getBaseMetaTileEntity();
        if (baseMetaTileEntity != null && baseMetaTileEntity.isServerSide()) baseMetaTileEntity.markDirty();
    }

    public int saturatingAdd(int left, int right) {
        if (right <= 0 || left >= Integer.MAX_VALUE - right) return Integer.MAX_VALUE;
        return left + right;
    }

    public int scaleParallelCount(int parallelCount, int slot) {
        return Math.max(1, (int) Math.round(parallelCount * naniteParallelModifiers[slot]));
    }

    public double randomNaniteModifier(XSTR random) {
        return Math.round((0.75 + random.nextDouble() * 0.5) * 100.0) / 100.0;
    }

    public int sampleNaniteEffect(int parallel, double chance) {
        if (parallel <= 0 || chance <= 0) return 0;
        if (chance >= 1) return parallel;

        double mean = parallel * chance;
        double standardDeviation = Math.sqrt(parallel * chance * (1 - chance));
        if (standardDeviation < 1) return Math.min(parallel, (int) Math.round(mean));

        boolean sampleMisses = chance > 0.5;
        double sampledChance = sampleMisses ? 1 - chance : chance;
        int sampledEffects = sampledChance < SPARSE_BINOMIAL_CHANCE_THRESHOLD
            ? sampleSparseBinomialEffects(parallel, sampledChance)
            : sampleBinomialEffects(parallel, sampledChance);
        return sampleMisses ? parallel - sampledEffects : sampledEffects;
    }

    public int sampleBinomialEffects(int parallel, double chance) {
        int affectedParallels = 0;
        for (int parallelIndex = 0; parallelIndex < parallel; parallelIndex++) {
            if (XSTR.XSTR_INSTANCE.nextDouble() < chance) affectedParallels++;
        }
        return affectedParallels;
    }

    public int sampleSparseBinomialEffects(int parallel, double chance) {
        int affectedParallels = 0;
        long parallelIndex = -1;
        double logMissChance = Math.log1p(-chance);
        while (parallelIndex < parallel - 1L) {
            parallelIndex += 1L + (long) (Math.log1p(-XSTR.XSTR_INSTANCE.nextDouble()) / logMissChance);
            if (parallelIndex >= parallel) break;
            affectedParallels++;
        }
        return affectedParallels;
    }

    public double[] createNaniteModifierArray() {
        double[] modifiers = new double[NANITE_SLOT_COUNT];
        Arrays.fill(modifiers, 1);
        return modifiers;
    }

    private static class TreeDiagramParallelHelper extends GTNLParallelHelper {

        private final TreeDiagram treeDiagram;
        private final boolean hasNaniteOutputEffects;
        private NaniteOutputModifiers naniteOutputModifiers;

        private TreeDiagramParallelHelper(TreeDiagram treeDiagram) {
            this.treeDiagram = treeDiagram;
            hasNaniteOutputEffects = treeDiagram.failureBonus > 0 || treeDiagram.outputCoefficient > 0;
        }

        @Override
        public void calculateItemOutputs(ItemStack[] truncatedItemOutputs) {
            if (customItemOutputCalculation != null) {
                super.calculateItemOutputs(truncatedItemOutputs);
                return;
            }
            if (truncatedItemOutputs.length == 0) return;

            ArrayList<ItemStack> outputItems = new ArrayList<>(truncatedItemOutputs.length);
            for (int outputIndex = 0; outputIndex < truncatedItemOutputs.length; outputIndex++) {
                ItemStack recipeOutput = recipe.getOutput(outputIndex);
                if (recipeOutput == null) continue;

                ItemStack output = recipeOutput.copy();
                GTNLParallelHelper.addItemsLong(
                    outputItems,
                    output,
                    getNaniteAdjustedOutputAmount(output.stackSize, recipe.getOutputChance(outputIndex)));
            }
            itemOutputs = outputItems.toArray(new ItemStack[0]);
        }

        @Override
        public void calculateFluidOutputs(FluidStack[] truncatedFluidOutputs) {
            if (customFluidOutputCalculation != null) {
                super.calculateFluidOutputs(truncatedFluidOutputs);
                return;
            }
            if (truncatedFluidOutputs.length == 0) return;

            ArrayList<FluidStack> outputFluids = new ArrayList<>(truncatedFluidOutputs.length);
            for (int outputIndex = 0; outputIndex < truncatedFluidOutputs.length; outputIndex++) {
                FluidStack recipeOutput = recipe.getFluidOutput(outputIndex);
                if (recipeOutput == null) continue;

                FluidStack output = recipeOutput.copy();
                GTNLParallelHelper.addFluidsLong(
                    outputFluids,
                    output,
                    getNaniteAdjustedOutputAmount(output.amount, recipe.getFluidOutputChance(outputIndex)));
            }
            fluidOutputs = outputFluids.toArray(new FluidStack[0]);
        }

        public long getNaniteAdjustedOutputAmount(int outputAmount, int outputChance) {
            long outputMultiplier = calculateIntegralChancedOutputMultiplier(
                (int) (outputChance * chanceMultiplier),
                currentParallel);
            long amount = (long) outputAmount * outputMultiplier;
            if (amount == 0 || !hasNaniteOutputEffects) return amount;

            NaniteOutputModifiers modifiers = getNaniteOutputModifiers();
            if (modifiers.failedParallels() == 0 && modifiers.bonusParallels() == 0) return amount;

            long failedOutputReduction = Math.round(amount * modifiers.failedParallels() / (4.0 * currentParallel));
            long bonusOutputIncrease = Math.round(amount * modifiers.bonusParallels() / (2.0 * currentParallel));
            return amount - failedOutputReduction + bonusOutputIncrease;
        }

        private NaniteOutputModifiers getNaniteOutputModifiers() {
            if (naniteOutputModifiers == null) {
                naniteOutputModifiers = new NaniteOutputModifiers(
                    treeDiagram.sampleNaniteEffect(currentParallel, treeDiagram.failureBonus),
                    treeDiagram.sampleNaniteEffect(currentParallel, treeDiagram.outputCoefficient));
            }
            return naniteOutputModifiers;
        }
    }

    private record NaniteOutputModifiers(int failedParallels, int bonusParallels) {}
}
