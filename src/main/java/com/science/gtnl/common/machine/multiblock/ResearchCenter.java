package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.chainAllGlasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.science.gtnl.api.mixinHelper.IResearchStationMarker;
import com.science.gtnl.common.gui.modularui.ResearchCenterGui;
import com.science.gtnl.mixins.late.tecTech.AccessorMTEResearchStation;
import com.science.gtnl.utils.StructureUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.AssemblyLineUtils;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTScannerResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import tectech.recipe.TecTechRecipeMaps;
import tectech.thing.metaTileEntity.multi.MTEResearchStation;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

public class ResearchCenter extends MTEResearchStation implements IResearchStationMarker {

    private static final int MAX_PARALLEL = 4;
    private static final int FILTER_SLOTS = 4;

    private static final String NBT_PARALLEL = "gtnlResearchCenterParallel";
    private static final String NBT_LOCKED_OUTPUTS = "gtnlResearchCenterLockedOutputs";
    private static final String NBT_RESEARCH_STACKS = "gtnlResearchCenterStacks";
    private static final String NBT_RESEARCH_OUTPUTS = "gtnlResearchCenterOutputs";
    private static final String NBT_DATA_STICKS = "gtnlResearchCenterDataSticks";
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String RESEARCH_CENTER_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":"
        + "multiblock/research_center";
    private static final int HORIZONTAL_OFF_SET = 28;
    private static final int VERTICAL_OFF_SET = 4;
    private static final int DEPTH_OFF_SET = 4;
    private static final int CONTROLLER_TEXTURE_ID = StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings8, 10);
    private static final String[][] shape = StructureUtils.readStructureFromFile(RESEARCH_CENTER_STRUCTURE_FILE_PATH);
    private static final IStructureDefinition<MTEResearchStation> STRUCTURE_DEFINITION = IStructureDefinition
        .<MTEResearchStation>builder()
        .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
        .addElement('A', chainAllGlasses())
        .addElement('B', ofBlock(GregTechAPI.sBlockCasings8, 7))
        .addElement(
            'C',
            buildHatchAdder(MTEResearchStation.class)
                .atLeast(
                    Energy.or(HatchElement.EnergyMulti),
                    Maintenance,
                    HatchElement.InputData,
                    InputBus,
                    OutputBus,
                    InputHatch)
                .casingIndex(CONTROLLER_TEXTURE_ID)
                .hint(1)
                .buildAndChain(ofBlock(GregTechAPI.sBlockCasings8, 10)))
        .addElement('D', ofBlock(GregTechAPI.sBlockCasings9, 7))
        .addElement('E', ofBlock(steelBars(), 0))
        .addElement('F', ofBlock(chiselNeonite(), 3))
        .build();

    private static Block steelBars() {
        Block block = GameRegistry.findBlock("dreamcraft", "SteelBars");
        return block == null ? Blocks.iron_bars : block;
    }

    private static Block chiselNeonite() {
        Block block = GameRegistry.findBlock("chisel", "neonite");
        return block == null ? Blocks.glowstone : block;
    }

    private int currentParallel = 1;
    private final ItemStack[] lockedOutputs = new ItemStack[FILTER_SLOTS];
    private int dataSticksToConsume;

    private final IItemHandlerModifiable lockedOutputHandler = new ItemStackHandler(lockedOutputs);
    private final ArrayList<ItemStack> researchStacksToConsume = new ArrayList<>();
    private final ArrayList<ItemStack> researchOutputsForGUI = new ArrayList<>();

    public ResearchCenter(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ResearchCenter(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ResearchCenter(mName);
    }

    @Override
    public void checkMachine(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack,
        List<StructureError> errors) {
        checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors);
    }

    @Override
    public IStructureDefinition<MTEResearchStation> getStructure_EM() {
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
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CONTROLLER_TEXTURE_ID),
                new TTRenderedExtendedFacingTexture(aActive ? TTMultiblockBase.ScreenON : TTMultiblockBase.ScreenOFF) };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(CONTROLLER_TEXTURE_ID) };
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ResearchCenterRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ResearchCenter_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ResearchCenter_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ResearchCenter_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ResearchCenter_03"))
            .addTecTechHatchInfo()
            .beginStructureBlock(37, 8, 10, false)
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_ResearchCenter_Structure_00"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_ResearchCenter_Structure_01"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_ResearchCenter_Structure_02"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_ResearchCenter_Structure_03"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_ResearchCenter_Structure_04"))
            .addStructureInfo(StatCollector.translateToLocal("Tooltip_ResearchCenter_Structure_05"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_ResearchCenter_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ResearchCenter_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_ResearchCenter_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_ResearchCenter_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_ResearchCenter_Casing"), 1)
            .addOtherStructurePart(
                StatCollector.translateToLocal("tt.keyword.Structure.DataAccessHatch"),
                StatCollector.translateToLocal("Tooltip_ResearchCenter_Casing"),
                1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public int getMaxParallelRecipes() {
        return Math.min(FILTER_SLOTS, Math.max(1, MAX_PARALLEL));
    }

    @Override
    @NotNull
    protected CheckRecipeResult checkProcessing_EM() {
        resetResearchCenterProgress();
        if (this.machineMode == MODE_SCANNER) {
            return checkDebugScannerProcessing();
        }

        int availableDataSticks = countDataSticks();
        if (availableDataSticks <= 0) {
            return CheckRecipeResultRegistry.NO_DATA_STICKS;
        }

        ArrayList<ItemStack> outputs = new ArrayList<>();
        ArrayList<ItemStack> researchStacksToConsume = new ArrayList<>();
        ArrayList<ItemStack> researchOutputsForGUI = new ArrayList<>();
        ItemStack firstResearchOutput = null;
        long computationRequired = 0;
        int recipeEUt = 0;
        long ampereFlow = 0;
        int maxParallel = Math.min(getMaxParallelRecipes(), getTrueParallel());
        int outputFullAt = -1;

        for (int channel = 0; channel < maxParallel; channel++) {
            if (outputs.size() >= availableDataSticks) {
                break;
            }
            TecTechRecipeMaps.TTResearchStationALRecipe assRecipe = findRecipeForChannel(
                getLockedOutput(channel),
                researchStacksToConsume);
            if (assRecipe == null) {
                continue;
            }

            outputs.add(outputDataStick(assRecipe));
            if (protectsExcessItem() && !canOutputAll(outputs.toArray(new ItemStack[0]))) {
                outputFullAt = outputs.size();
                outputs.remove(outputs.size() - 1);
                break;
            }
            if (firstResearchOutput == null && assRecipe.mOutput != null) {
                firstResearchOutput = assRecipe.mOutput.copy();
            }
            if (assRecipe.mOutput != null) {
                researchOutputsForGUI.add(assRecipe.mOutput.copy());
            }
            addResearchStackToConsume(
                researchStacksToConsume,
                assRecipe.mResearchItem,
                assRecipe.mResearchItem.stackSize);
            computationRequired += assRecipe.mComputation * 20L;
            recipeEUt = Math.min(recipeEUt, Math.min(assRecipe.mEUt, -assRecipe.mEUt));
            ampereFlow = Math.max(ampereFlow, assRecipe.mAmperage);
        }

        if (outputs.isEmpty()) {
            return outputFullAt > 0 ? CheckRecipeResultRegistry.ITEM_OUTPUT_FULL : CheckRecipeResultRegistry.NO_RECIPE;
        }

        this.currentParallel = outputs.size();
        this.researchOutputForGUI = firstResearchOutput;
        this.researchOutputsForGUI.addAll(researchOutputsForGUI);
        this.researchStacksToConsume.addAll(researchStacksToConsume);
        this.dataSticksToConsume = outputs.size();
        this.computationRequired = this.computationRemaining = computationRequired;
        setPacketLossDecayFrom(this.computationRequired);
        this.mOutputItems = outputs.toArray(new ItemStack[0]);
        this.mEUt = recipeEUt;
        this.eRequiredData = 0;
        this.eAmpereFlow = ampereFlow;
        this.mMaxProgresstime = 20;
        this.mEfficiencyIncrease = 10000;
        return SimpleCheckRecipeResult.ofSuccess("researching");
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (this.computationRemaining <= 0) {
            this.computationRemaining = 0;
            this.mProgresstime = this.mMaxProgresstime;
            return true;
        }

        long computationPerTick = this.eAvailableData * Math.max(1L, this.currentParallel);
        if (computationPerTick > 0) {
            this.computationRemaining -= computationPerTick;
        }
        this.mProgresstime = 1;
        return true;
    }

    public boolean tickAcceleration(int tickAcceleratedRate) {
        if (this.computationRemaining <= 0) return true;
        this.computationRemaining -= (long) tickAcceleratedRate * this.eAvailableData
            * Math.max(1L, this.currentParallel);
        return true;
    }

    @Override
    protected boolean checkComputationTimeout() {
        return true;
    }

    @Override
    public void outputAfterRecipe_EM() {
        for (ItemStack researchStackToConsume : this.researchStacksToConsume) {
            if (!depleteInputsAcrossSlots(researchStackToConsume)) {
                this.mOutputItems = null;
                return;
            }
        }
        if (this.dataSticksToConsume > 0 && !depleteDataSticks(this.dataSticksToConsume)) {
            this.mOutputItems = null;
        }
    }

    @Override
    protected void addClassicOutputs_EM() {
        super.addClassicOutputs_EM();
        resetResearchCenterProgress();
    }

    @Override
    public String[] getInfoData() {
        String[] info = super.getInfoData();
        String[] extended = Arrays.copyOf(info, info.length + 1);
        extended[info.length] = "Parallel: " + EnumChatFormatting.GREEN
            + formatNumber(this.currentParallel)
            + EnumChatFormatting.RESET
            + " / "
            + EnumChatFormatting.YELLOW
            + formatNumber(getMaxParallelRecipes())
            + EnumChatFormatting.RESET;
        return extended;
    }

    public String getResearchOutputsForGui() {
        if (this.researchOutputsForGUI.isEmpty()) {
            return "";
        }

        Map<String, Integer> outputCounts = new LinkedHashMap<>();
        for (ItemStack output : this.researchOutputsForGUI) {
            if (GTUtility.isStackInvalid(output)) {
                continue;
            }
            String displayName = output.getDisplayName();
            outputCounts.put(displayName, outputCounts.getOrDefault(displayName, 0) + 1);
        }
        if (outputCounts.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Integer> entry : outputCounts.entrySet()) {
            if (!first) {
                builder.append(", ");
            }
            first = false;
            builder.append(entry.getKey());
            if (entry.getValue() > 1) {
                builder.append(" x")
                    .append(entry.getValue());
            }
        }
        return builder.toString();
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new ResearchCenterGui(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_RESEARCH,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_SCANNER);
    }

    @Override
    public IItemHandlerModifiable gtnl$getResearchMarkerInventoryHandler() {
        return lockedOutputHandler;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger(NBT_PARALLEL, this.currentParallel);
        aNBT.setInteger(NBT_DATA_STICKS, this.dataSticksToConsume);
        NBTTagList lockedOutputsTag = new NBTTagList();
        for (int i = 0; i < FILTER_SLOTS; i++) {
            ItemStack lockedOutput = getLockedOutput(i);
            if (lockedOutput != null) {
                NBTTagCompound lockedOutputTag = new NBTTagCompound();
                lockedOutputTag.setInteger("Slot", i);
                lockedOutput.writeToNBT(lockedOutputTag);
                lockedOutputsTag.appendTag(lockedOutputTag);
            }
        }
        if (lockedOutputsTag.tagCount() > 0) {
            aNBT.setTag(NBT_LOCKED_OUTPUTS, lockedOutputsTag);
        }
        if (!this.researchStacksToConsume.isEmpty()) {
            NBTTagList stacksTag = new NBTTagList();
            for (ItemStack stackToConsume : this.researchStacksToConsume) {
                NBTTagCompound stackTag = new NBTTagCompound();
                stackToConsume.writeToNBT(stackTag);
                stacksTag.appendTag(stackTag);
            }
            aNBT.setTag(NBT_RESEARCH_STACKS, stacksTag);
        }
        if (!this.researchOutputsForGUI.isEmpty()) {
            NBTTagList outputsTag = new NBTTagList();
            for (ItemStack output : this.researchOutputsForGUI) {
                NBTTagCompound outputTag = new NBTTagCompound();
                output.writeToNBT(outputTag);
                outputsTag.appendTag(outputTag);
            }
            aNBT.setTag(NBT_RESEARCH_OUTPUTS, outputsTag);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        this.currentParallel = Math.max(1, aNBT.getInteger(NBT_PARALLEL));
        this.dataSticksToConsume = aNBT.getInteger(NBT_DATA_STICKS);
        this.researchStacksToConsume.clear();
        this.researchOutputsForGUI.clear();
        Arrays.fill(this.lockedOutputs, null);
        if (aNBT.hasKey(NBT_LOCKED_OUTPUTS, Constants.NBT.TAG_LIST)) {
            NBTTagList lockedOutputsTag = aNBT.getTagList(NBT_LOCKED_OUTPUTS, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < lockedOutputsTag.tagCount(); i++) {
                NBTTagCompound lockedOutputTag = lockedOutputsTag.getCompoundTagAt(i);
                int slot = lockedOutputTag.getInteger("Slot");
                if (slot >= 0 && slot < FILTER_SLOTS) {
                    this.lockedOutputs[slot] = ItemStack.loadItemStackFromNBT(lockedOutputTag);
                }
            }
        }
        if (aNBT.hasKey(NBT_RESEARCH_STACKS, Constants.NBT.TAG_LIST)) {
            NBTTagList stacksTag = aNBT.getTagList(NBT_RESEARCH_STACKS, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < stacksTag.tagCount(); i++) {
                ItemStack stackToConsume = ItemStack.loadItemStackFromNBT(stacksTag.getCompoundTagAt(i));
                if (GTUtility.isStackValid(stackToConsume)) {
                    this.researchStacksToConsume.add(stackToConsume);
                }
            }
        }
        if (aNBT.hasKey(NBT_RESEARCH_OUTPUTS, Constants.NBT.TAG_LIST)) {
            NBTTagList outputsTag = aNBT.getTagList(NBT_RESEARCH_OUTPUTS, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < outputsTag.tagCount(); i++) {
                ItemStack output = ItemStack.loadItemStackFromNBT(outputsTag.getCompoundTagAt(i));
                if (GTUtility.isStackValid(output)) {
                    this.researchOutputsForGUI.add(output);
                }
            }
        }
    }

    private TecTechRecipeMaps.TTResearchStationALRecipe findRecipeForChannel(ItemStack lockedOutput,
        List<ItemStack> plannedConsumes) {
        for (TecTechRecipeMaps.TTResearchStationALRecipe assRecipe : TecTechRecipeMaps.researchableALRecipeList) {
            if (!matchesChannelLockedOutput(assRecipe.mOutput, lockedOutput)) {
                continue;
            }
            int availableResearchItems = countResearchItems(assRecipe.mResearchItem, plannedConsumes);
            if (availableResearchItems >= assRecipe.mResearchItem.stackSize) {
                return assRecipe;
            }
        }
        return null;
    }

    private CheckRecipeResult checkDebugScannerProcessing() {
        if (countAvailableDataSticks() <= 0) {
            return CheckRecipeResultRegistry.NO_DATA_STICKS;
        }

        FluidStack fluid = getScannerFluid();
        CheckRecipeResult outputFull = null;

        for (ItemStack researchItem : getScannerCandidateItems()) {
            if (isDataContainer(researchItem)) {
                continue;
            }

            GTScannerResult scannerResult = RecipeMaps.scannerHandlers
                .findRecipe(this, researchItem, ItemList.Tool_DataStick.get(1), fluid);
            if (scannerResult != null && !scannerResult.isNotMet()) {
                if (!matchesScannerResultFilter(scannerResult)) {
                    continue;
                }
                if (protectsExcessItem() && scannerResult.output != null
                    && !canOutputAll(new ItemStack[] { scannerResult.output })) {
                    outputFull = CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
                    continue;
                }
                return applyDebugScannerSuccess(researchItem, scannerResult);
            }

            GTRecipe fakeRecipe = findFakeScannerRecipe(researchItem);
            if (fakeRecipe == null || researchItem.stackSize < fakeRecipe.mInputs[0].stackSize) {
                continue;
            }

            GTRecipe.RecipeAssemblyLine assRecipe = findAssemblyLineRecipe(researchItem);
            if (assRecipe == null || !matchesAnyLockedOutput(assRecipe.mOutput)) {
                continue;
            }

            ItemStack[] outputs = outputDataSticks(assRecipe, 1);
            if (protectsExcessItem() && !canOutputAll(outputs)) {
                outputFull = CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
                continue;
            }

            this.currentParallel = 1;
            this.researchOutputForGUI = assRecipe.mOutput == null ? null : assRecipe.mOutput.copy();
            if (assRecipe.mOutput != null) {
                this.researchOutputsForGUI.add(assRecipe.mOutput.copy());
            }
            this.researchStacksToConsume.add(GTUtility.copyAmount(fakeRecipe.mInputs[0].stackSize, researchItem));
            this.dataSticksToConsume = 1;
            this.mOutputItems = outputs;
            this.mMaxProgresstime = 1;
            this.mEfficiencyIncrease = 10000;
            this.mEUt = 0;
            this.eRequiredData = 0;
            this.eAmpereFlow = 0;
            return SimpleCheckRecipeResult.ofSuccess("scanning");
        }

        return outputFull != null ? outputFull : CheckRecipeResultRegistry.NO_RECIPE;
    }

    private CheckRecipeResult applyDebugScannerSuccess(ItemStack researchItem, GTScannerResult scannerResult) {
        this.currentParallel = 1;
        ItemStack guiOutput = null;
        if (scannerResult instanceof GTScannerResult.ALScannerResult alResult) {
            guiOutput = alResult.alRecipe.mOutput == null ? null : alResult.alRecipe.mOutput.copy();
        } else if (scannerResult.output != null) {
            guiOutput = scannerResult.output.copy();
        }
        this.researchOutputForGUI = guiOutput;
        if (guiOutput != null) {
            this.researchOutputsForGUI.add(guiOutput);
        }
        this.researchStacksToConsume.add(GTUtility.copyAmount(scannerResult.inputConsume, researchItem));
        this.dataSticksToConsume = Math.max(1, scannerResult.specialConsume);
        this.mOutputItems = scannerResult.output == null ? null : new ItemStack[] { scannerResult.output };
        this.mMaxProgresstime = 1;
        this.mEfficiencyIncrease = 10000;
        this.mEUt = 0;
        this.eRequiredData = 0;
        this.eAmpereFlow = 0;
        this.computationRequired = this.computationRemaining = 0;
        setPacketLossDecayFrom(0);
        return SimpleCheckRecipeResult.ofSuccess("scanning");
    }

    private List<ItemStack> getScannerCandidateItems() {
        ArrayList<ItemStack> candidates = new ArrayList<>();
        for (ItemStack input : getAllStoredInputs()) {
            if (GTUtility.isStackValid(input) && !isDataContainer(input)) {
                candidates.add(input);
            }
        }
        ItemStack controllerStack = getStackInSlot(getControllerSlotIndex());
        if (GTUtility.isStackValid(controllerStack) && !isDataContainer(controllerStack)) {
            candidates.add(controllerStack);
        }
        return candidates;
    }

    private FluidStack getScannerFluid() {
        for (FluidStack fluid : getStoredFluids()) {
            if (fluid != null && fluid.getFluid() != null && fluid.amount > 0) {
                return fluid;
            }
        }
        return null;
    }

    private boolean matchesScannerResultFilter(GTScannerResult scannerResult) {
        if (scannerResult instanceof GTScannerResult.ALScannerResult alResult) {
            return matchesAnyLockedOutput(alResult.alRecipe.mOutput);
        }
        return matchesAnyLockedOutput(scannerResult.output);
    }

    private static boolean isDataContainer(ItemStack stack) {
        return ItemList.Tool_DataStick.isStackEqual(stack, false, true)
            || ItemList.Tool_DataOrb.isStackEqual(stack, false, true);
    }

    private static GTRecipe findFakeScannerRecipe(ItemStack researchStack) {
        GTRecipe recipe = findFakeRecipeInMap(TecTechRecipeMaps.researchStationFakeRecipes, researchStack);
        if (recipe != null) {
            return recipe;
        }
        return findFakeRecipeInMap(RecipeMaps.scannerFakeRecipes, researchStack);
    }

    private static GTRecipe findFakeRecipeInMap(RecipeMap<?> recipeMap, ItemStack researchStack) {
        for (GTRecipe fakeRecipe : recipeMap.getAllRecipes()) {
            if (GTUtility.areStacksEqual(fakeRecipe.mInputs[0], researchStack, true)) {
                return fakeRecipe;
            }
        }
        return null;
    }

    private static GTRecipe.RecipeAssemblyLine findAssemblyLineRecipe(ItemStack researchStack) {
        for (GTRecipe.RecipeAssemblyLine assRecipe : TecTechRecipeMaps.researchableALRecipeList) {
            if (GTUtility.areStacksEqual(assRecipe.mResearchItem, researchStack, true)) {
                return assRecipe;
            }
        }
        for (GTRecipe.RecipeAssemblyLine assRecipe : GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes) {
            if (GTUtility.areStacksEqual(assRecipe.mResearchItem, researchStack, true)) {
                return assRecipe;
            }
        }
        return null;
    }

    private ItemStack[] outputDataSticks(GTRecipe.RecipeAssemblyLine assRecipe, int parallel) {
        ItemStack[] outputs = new ItemStack[parallel];
        Arrays.setAll(outputs, i -> outputDataStick(assRecipe));
        return outputs;
    }

    private ItemStack outputDataStick(GTRecipe.RecipeAssemblyLine assRecipe) {
        ItemStack output = ItemList.Tool_DataStick.get(1);
        output.setTagCompound(new NBTTagCompound());
        output.getTagCompound()
            .setString(
                "author",
                EnumChatFormatting.BLUE + "Tec"
                    + EnumChatFormatting.DARK_BLUE
                    + "Tech"
                    + EnumChatFormatting.WHITE
                    + " Assembly Line Recipe Generator");
        AssemblyLineUtils.setAssemblyLineRecipeOnDataStick(output, assRecipe);
        return output;
    }

    private int countResearchItems(ItemStack researchItem) {
        return countResearchItems(researchItem, null);
    }

    private int countResearchItems(ItemStack researchItem, List<ItemStack> plannedConsumes) {
        int count = 0;
        ArrayList<ItemStack> inputs = getStoredInputs();
        for (ItemStack input : inputs) {
            if (GTUtility.areStacksEqual(researchItem, input, true)) {
                count += input.stackSize;
            }
        }
        if (plannedConsumes != null) {
            for (ItemStack plannedConsume : plannedConsumes) {
                if (GTUtility.areStacksEqual(researchItem, plannedConsume, true)) {
                    count -= plannedConsume.stackSize;
                }
            }
        }
        return count;
    }

    private void addResearchStackToConsume(List<ItemStack> plannedConsumes, ItemStack researchItem, int amount) {
        for (ItemStack plannedConsume : plannedConsumes) {
            if (GTUtility.areStacksEqual(researchItem, plannedConsume, true)) {
                plannedConsume.stackSize += amount;
                return;
            }
        }
        plannedConsumes.add(GTUtility.copyAmount(amount, researchItem));
    }

    private int countAvailableDataSticks() {
        int count = 0;
        for (ItemStack input : getAllStoredInputs()) {
            if (ItemList.Tool_DataStick.isStackEqual(input, false, true)) {
                count += input.stackSize;
            }
        }
        ItemStack controllerStack = getStackInSlot(getControllerSlotIndex());
        if (ItemList.Tool_DataStick.isStackEqual(controllerStack, false, true)) {
            count += controllerStack.stackSize;
        }
        return count;
    }

    private boolean depleteDataSticks(int amount) {
        if (depleteInputsAcrossSlots(ItemList.Tool_DataStick.get(amount))) {
            return true;
        }
        ItemStack controllerStack = getStackInSlot(getControllerSlotIndex());
        if (!ItemList.Tool_DataStick.isStackEqual(controllerStack, false, true) || controllerStack.stackSize < amount) {
            return false;
        }
        controllerStack.stackSize -= amount;
        if (controllerStack.stackSize <= 0) {
            mInventory[getControllerSlotIndex()] = null;
        }
        return true;
    }

    private int countDataSticks() {
        return countAvailableDataSticks();
    }

    private ItemStack getLockedOutput(int channel) {
        if (channel < 0 || channel >= FILTER_SLOTS) {
            return null;
        }
        return lockedOutputHandler.getStackInSlot(channel);
    }

    private boolean matchesChannelLockedOutput(ItemStack output, ItemStack lockedOutput) {
        return lockedOutput == null || GTUtility.areStacksEqual(output, lockedOutput, true);
    }

    private boolean matchesAnyLockedOutput(ItemStack output) {
        if (!hasAnyLockedOutput()) {
            return true;
        }
        for (int i = 0; i < FILTER_SLOTS; i++) {
            ItemStack lockedOutput = getLockedOutput(i);
            if (lockedOutput != null && matchesChannelLockedOutput(output, lockedOutput)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAnyLockedOutput() {
        for (int i = 0; i < FILTER_SLOTS; i++) {
            if (getLockedOutput(i) != null) {
                return true;
            }
        }
        return false;
    }

    private boolean depleteInputsAcrossSlots(ItemStack stack) {
        if (GTUtility.isStackInvalid(stack)) {
            return false;
        }
        if (countDepletableItems(stack) < stack.stackSize) {
            return false;
        }

        ItemStack singleItem = GTUtility.copyAmount(1, stack);
        for (int i = 0; i < stack.stackSize; i++) {
            if (!depleteInput(singleItem)) {
                return false;
            }
        }
        return true;
    }

    private int countDepletableItems(ItemStack stack) {
        int count = 0;
        ArrayList<ItemStack> inputs = getStoredInputs();
        for (ItemStack input : inputs) {
            if (GTUtility.areStacksEqual(stack, input)) {
                count += input.stackSize;
            }
        }
        return count;
    }

    private void setPacketLossDecayFrom(long value) {
        ((AccessorMTEResearchStation) this).setPacketLossDecayFrom(value);
    }

    private void resetResearchCenterProgress() {
        this.eComputationTimeout = MAX_COMPUTATION_TIMEOUT;
        this.researchOutputForGUI = null;
        this.researchOutputsForGUI.clear();
        this.researchStacksToConsume.clear();
        this.dataSticksToConsume = 0;
        this.currentParallel = 1;
        this.mOutputItems = null;
        this.mMaxProgresstime = 0;
        this.mEfficiencyIncrease = 0;
        this.mEUt = 0;
        this.computationRequired = this.computationRemaining = 0;
        setPacketLossDecayFrom(0);
    }

}
