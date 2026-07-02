package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.science.gtnl.api.mixinHelper.IResearchStationMarker;
import com.science.gtnl.common.gui.modularui.LargeResearchStationGui;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.AssemblyLineUtils;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import tectech.recipe.TecTechRecipeMaps;
import tectech.thing.metaTileEntity.multi.MTEResearchStation;

public class LargeResearchStation extends MTEResearchStation implements IResearchStationMarker {

    public static int MAX_PARALLEL = 4;
    private static final int FILTER_SLOTS = 4;

    private static final String NBT_PARALLEL = "gtnlLargeResearchParallel";
    private static final String NBT_LOCKED_OUTPUTS = "gtnlLargeResearchLockedOutputs";
    private static final String NBT_RESEARCH_STACKS = "gtnlLargeResearchStacks";
    private static final String NBT_RESEARCH_OUTPUTS = "gtnlLargeResearchOutputs";
    private static final String NBT_DATA_STICKS = "gtnlLargeResearchDataSticks";
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int HORIZONTAL_OFF_SET = 1;
    private static final int VERTICAL_OFF_SET = 1;
    private static final int DEPTH_OFF_SET = 0;
    private static final String[] STRUCTURE_DESCRIPTION = new String[] { EnumChatFormatting.AQUA + "Hint Details:",
        "3x3x3 Cobblestone cube", "Controller: center of the front face",
        "Allowed hatches: Energy, Maintenance, Data Input, Input Bus, Output Bus, Input Hatch" };
    private static final Field PACKET_LOSS_DECAY_FROM_FIELD = getResearchStationField("packetLossDecayFrom");
    private static final IStructureDefinition<MTEResearchStation> STRUCTURE_DEFINITION = IStructureDefinition
        .<MTEResearchStation>builder()
        .addShape(
            STRUCTURE_PIECE_MAIN,
            transpose(new String[][] { { "AAA", "AAA", "AAA" }, { "A~A", "AAA", "AAA" }, { "AAA", "AAA", "AAA" } }))
        .addElement(
            'A',
            buildHatchAdder(MTEResearchStation.class)
                .atLeast(
                    Energy.or(HatchElement.EnergyMulti),
                    Maintenance,
                    HatchElement.InputData,
                    InputBus,
                    OutputBus,
                    InputHatch)
                .casingIndex(GTUtility.getTextureId((byte) 116, (byte) 24))
                .hint(1)
                .buildAndChain(ofBlock(Blocks.cobblestone, 0)))
        .build();

    private int currentParallel = 1;
    private final ItemStack[] lockedOutputs = new ItemStack[FILTER_SLOTS];
    private final IItemHandlerModifiable lockedOutputHandler = new ItemStackHandler(lockedOutputs);
    private final ArrayList<ItemStack> researchStacksToConsume = new ArrayList<>();
    private final ArrayList<ItemStack> researchOutputsForGUI = new ArrayList<>();
    private int dataSticksToConsume;

    public LargeResearchStation(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public LargeResearchStation(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new LargeResearchStation(mName);
    }

    @Override
    public IItemHandlerModifiable gtnl$getResearchMarkerInventoryHandler() {
        return lockedOutputHandler;
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
    public String[] getStructureDescription(ItemStack stackSize) {
        return STRUCTURE_DESCRIPTION;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new LargeResearchStationGui(this).withMachineModeIcons(
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_RESEARCH,
            GTGuiTextures.OVERLAY_BUTTON_MACHINEMODE_SCANNER);
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
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public int nextMachineMode() {
        if (this.machineMode == MODE_RESEARCH_STATION) return MODE_SCANNER;
        return MODE_RESEARCH_STATION;
    }

    @Override
    public void setMachineMode(int aIndex) {
        switch (aIndex) {
            case MODE_RESEARCH_STATION, MODE_SCANNER -> this.machineMode = aIndex;
            default -> this.machineMode = MODE_RESEARCH_STATION;
        }
    }

    @Override
    public int getMaxParallelRecipes() {
        return Math.min(FILTER_SLOTS, Math.max(1, MAX_PARALLEL));
    }

    @Override
    @NotNull
    protected CheckRecipeResult checkProcessing_EM() {
        resetLargeProgress();
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
        if (countDataSticks() <= 0) {
            return CheckRecipeResultRegistry.NO_DATA_STICKS;
        }

        for (TecTechRecipeMaps.TTResearchStationALRecipe assRecipe : TecTechRecipeMaps.researchableALRecipeList) {
            if (!matchesAnyLockedOutput(assRecipe.mOutput)) {
                continue;
            }
            if (countResearchItems(assRecipe.mResearchItem) < assRecipe.mResearchItem.stackSize) {
                continue;
            }

            ItemStack[] outputs = outputDataSticks(assRecipe, 1);
            if (protectsExcessItem() && !canOutputAll(outputs)) {
                return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
            }

            this.currentParallel = 1;
            this.researchOutputForGUI = assRecipe.mOutput == null ? null : assRecipe.mOutput.copy();
            if (assRecipe.mOutput != null) {
                this.researchOutputsForGUI.add(assRecipe.mOutput.copy());
            }
            this.researchStacksToConsume
                .add(GTUtility.copyAmount(assRecipe.mResearchItem.stackSize, assRecipe.mResearchItem));
            this.dataSticksToConsume = 1;
            this.mOutputItems = outputs;
            this.mMaxProgresstime = 1;
            this.mEfficiencyIncrease = 10000;
            this.mEUt = 0;
            this.eRequiredData = 0;
            this.eAmpereFlow = 0;
            return SimpleCheckRecipeResult.ofSuccess("scanning");
        }
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    private ItemStack[] outputDataSticks(TecTechRecipeMaps.TTResearchStationALRecipe assRecipe, int parallel) {
        ItemStack[] outputs = new ItemStack[parallel];
        Arrays.setAll(outputs, i -> outputDataStick(assRecipe));
        return outputs;
    }

    private ItemStack outputDataStick(TecTechRecipeMaps.TTResearchStationALRecipe assRecipe) {
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

    private int limitParallelByOutput(ItemStack[] outputs, int parallel) {
        if (!protectsExcessItem()) {
            return parallel;
        }
        for (int candidate = parallel; candidate > 0; candidate--) {
            if (canOutputAll(Arrays.copyOf(outputs, candidate))) {
                return candidate;
            }
        }
        return 0;
    }

    private void setPacketLossDecayFrom(long value) {
        try {
            PACKET_LOSS_DECAY_FROM_FIELD.setLong(this, value);
        } catch (IllegalAccessException ignored) {}
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

    private int countDataSticks() {
        int count = 0;
        ArrayList<ItemStack> inputs = getStoredInputs();
        for (ItemStack input : inputs) {
            if (ItemList.Tool_DataStick.isStackEqual(input, false, true)) {
                count += input.stackSize;
            }
        }
        return count;
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
            if (!depleteInput(researchStackToConsume)) {
                this.mOutputItems = null;
                return;
            }
        }
        if (this.dataSticksToConsume > 0 && !depleteInput(ItemList.Tool_DataStick.get(this.dataSticksToConsume))) {
            this.mOutputItems = null;
        }
    }

    @Override
    protected void addClassicOutputs_EM() {
        super.addClassicOutputs_EM();
        resetLargeProgress();
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

    private void resetLargeProgress() {
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

    private static Field getResearchStationField(String name) {
        try {
            Field field = MTEResearchStation.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

}
