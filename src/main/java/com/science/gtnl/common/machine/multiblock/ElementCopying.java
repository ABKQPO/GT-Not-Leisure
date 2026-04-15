package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase.CustomHatchElement.ParallelCon;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.github.bsideup.jabel.Desugar;
import com.google.common.collect.Lists;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizons.modularui.api.drawable.FluidDrawable;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.ItemDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicTextWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.machine.multiMachineBase.WirelessEnergyMultiMachineBase;
import com.science.gtnl.common.recipe.gtnl.ElementCopyingRecipes;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.Utils;

import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchMultiInput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.misc.WirelessNetworkManager;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import gtnhlanth.common.register.LanthItemList;

public class ElementCopying extends WirelessEnergyMultiMachineBase<ElementCopying> implements ISurvivalConstructable {

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String EC_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/element_copying";
    private static final String[][] shape = StructureUtils.readStructureFromFile(EC_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 7;
    private static final int VERTICAL_OFF_SET = 0;
    private static final int DEPTH_OFF_SET = 12;

    public static FluidStack UUMatter = Materials.UUMatter.getFluid(1);

    public Set<ItemCopyingEntry> itemEntry = new HashSet<>();
    public Set<FluidCopyingEntry> fluidEntry = new HashSet<>();

    public ElementCopying(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public ElementCopying(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new ElementCopying(this.mName);
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
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        NBTTagList itemList = new NBTTagList();
        for (ItemCopyingEntry item : itemEntry) {
            itemList.appendTag(item.serialize());
        }
        aNBT.setTag("itemEntry", itemList);

        NBTTagList fluidList = new NBTTagList();
        for (FluidCopyingEntry fluid : fluidEntry) {
            fluidList.appendTag(fluid.serialize());
        }
        aNBT.setTag("fluidEntry", fluidList);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        itemEntry.clear();
        if (aNBT.hasKey("itemEntry", 9)) {
            NBTTagList itemList = aNBT.getTagList("itemEntry", 10);
            for (int i = 0; i < itemList.tagCount(); i++) {
                NBTTagCompound tag = itemList.getCompoundTagAt(i);
                ItemCopyingEntry entry = ItemCopyingEntry.deserialize(tag);
                itemEntry.add(entry);
            }
        }

        fluidEntry.clear();
        if (aNBT.hasKey("fluidEntry", 9)) {
            NBTTagList fluidList = aNBT.getTagList("fluidEntry", 10);
            for (int i = 0; i < fluidList.tagCount(); i++) {
                NBTTagCompound tag = fluidList.getCompoundTagAt(i);
                FluidCopyingEntry entry = FluidCopyingEntry.deserialize(tag);
                fluidEntry.add(entry);
            }
        }
    }

    @Override
    public int getCasingTextureID() {
        return 1028;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.replicatorRecipes;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        // Clear cached wireless parallel cap from previous recipe checks.
        maxParallelStored = -1;
        resetParallelTier();
        maxParallelStored = getTrueParallel();
        costingEU = BigInteger.ZERO;
        costingEUText = Utils.ZERO_STRING;

        long maxInputEU = wirelessMode ? Utils.toLongSafe(WirelessNetworkManager.getUserEU(ownerUUID))
            : getMaxInputEu();

        long needUUMPerUnit = getNeedUUM();
        long needEUtPerUnit = Math.max(getNeedEU() / 20, 1);

        if (needUUMPerUnit <= 0) return CheckRecipeResultRegistry.NO_RECIPE;

        long totalUUMAvailable = getStoredFluids().stream()
            .filter(stack -> GTUtility.areFluidsEqual(stack, UUMatter))
            .mapToLong(stack -> stack.amount)
            .sum();

        int parallelByUUM = (int) Math.min(Integer.MAX_VALUE, totalUUMAvailable / needUUMPerUnit);
        int parallelByEU = (int) Math.min(Integer.MAX_VALUE, maxInputEU / needEUtPerUnit);
        if (parallelByEU <= 0) return CheckRecipeResultRegistry.insufficientPower(parallelByEU);

        maxParallelStored = Math.min(maxParallelStored, Math.min(parallelByUUM, parallelByEU));
        if (maxParallelStored <= 0) return CheckRecipeResultRegistry.NO_RECIPE;

        long totalUUMNeeded = needUUMPerUnit * maxParallelStored;

        List<FluidStack> matter = new ArrayList<>();
        while (totalUUMNeeded > 0) {
            int amount = (int) Math.min(Integer.MAX_VALUE, totalUUMNeeded);
            matter.add(Materials.UUMatter.getFluid(amount));
            totalUUMNeeded -= amount;
        }
        depleteInputList(matter, false);

        mOutputItems = itemEntry.stream()
            .map(
                entry -> entry.itemId()
                    .getItemStack(maxParallelStored))
            .toArray(ItemStack[]::new);

        List<FluidStack> outputFluidList = new ArrayList<>();

        for (FluidCopyingEntry entry : fluidEntry) {
            long totalAmount = (long) maxParallelStored * 1000L;
            while (totalAmount > 0) {
                int amount = (int) Math.min(Integer.MAX_VALUE, totalAmount);
                outputFluidList.add(
                    entry.fluidId()
                        .getFluidStack(amount));
                totalAmount -= amount;
            }
        }

        mOutputFluids = outputFluidList.toArray(new FluidStack[0]);

        mMaxProgresstime = 20;

        if (wirelessMode) {
            addEUToGlobalEnergyMap(ownerUUID, -needEUtPerUnit);
            lEUt = 0;
        } else {
            lEUt = -needEUtPerUnit;
        }

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public boolean depleteInputList(List<FluidStack> fluids, boolean simulate) {
        if (fluids == null || fluids.isEmpty()) return false;

        Map<Fluid, Long> mergedStorage = new HashMap<>();
        for (FluidStack stored : getStoredFluids()) {
            if (stored != null) {
                mergedStorage.merge(stored.getFluid(), (long) stored.amount, Long::sum);
            }
        }

        Map<Fluid, Long> mergedNeeded = new HashMap<>();
        for (FluidStack needed : fluids) {
            if (needed != null) {
                mergedNeeded.merge(needed.getFluid(), (long) needed.amount, Long::sum);
            }
        }

        for (Map.Entry<Fluid, Long> neededEntry : mergedNeeded.entrySet()) {
            long availableAmount = mergedStorage.getOrDefault(neededEntry.getKey(), 0L);
            if (availableAmount < neededEntry.getValue()) return false;
        }

        if (simulate) return true;
        for (FluidStack needed : fluids) {
            int remaining = needed.amount;

            while (remaining > 0) {
                int drainedThisRound = 0;

                for (MTEHatch hatch : getAllInputHatches()) {
                    int drained = drainFluid(hatch, new FluidStack(needed.getFluid(), remaining), true);
                    drainedThisRound += drained;
                }

                if (drainedThisRound <= 0) {
                    break;
                }

                remaining -= drainedThisRound;
            }
        }

        return true;
    }

    public int drainFluid(MTEHatch hatch, FluidStack fluid, boolean doDrain) {
        if (fluid == null || hatch == null) return 0;

        if (supportsCraftingMEBuffer() && hatch instanceof IDualInputHatch tHatch && tHatch.supportsFluids()) {
            Optional<IDualInputInventory> inventoryOpt = tHatch.getFirstNonEmptyInventory();
            if (inventoryOpt.isPresent()) {
                IDualInputInventory inventory = inventoryOpt.get();
                for (FluidStack stored : Lists.newArrayList(inventory.getFluidInputs())) {
                    if (stored != null && stored.amount > 0 && stored.isFluidEqual(fluid)) {
                        int deduct = Math.min(stored.amount, fluid.amount);
                        if (doDrain) stored.amount -= deduct;
                        return deduct;
                    }
                }
            }
        }

        if (hatch instanceof MTEHatchInput tHatch && tHatch.isValid()) {
            if (tHatch instanceof MTEHatchInputME meHatch) {
                meHatch.startRecipeProcessing();
                FluidStack drained = meHatch.drain(ForgeDirection.UNKNOWN, fluid, doDrain);
                meHatch.endRecipeProcessing(this);
                return drained != null ? Math.min(drained.amount, fluid.amount) : 0;
            } else {
                FluidStack drained = tHatch.drain(ForgeDirection.UNKNOWN, fluid, doDrain);
                return drained != null ? Math.min(drained.amount, fluid.amount) : 0;
            }
        }

        return 0;
    }

    public List<MTEHatch> getAllInputHatches() {
        List<MTEHatch> dualHatches = mDualInputHatches.stream()
            .map(h -> (MTEHatch) h)
            .collect(Collectors.toList());

        List<MTEHatch> allHatches = new ArrayList<>(mInputHatches);
        allHatches.addAll(dualHatches);

        return GTUtility.filterValidMTEs(allHatches);
    }

    @Override
    public ArrayList<FluidStack> getStoredFluidsForColor(Optional<Byte> color) {
        ArrayList<FluidStack> rList = new ArrayList<>();
        Map<Fluid, FluidStack> inputsFromME = new HashMap<>();
        for (MTEHatchInput tHatch : GTUtility.validMTEList(mInputHatches)) {
            byte hatchColor = tHatch.getColor();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            setHatchRecipeMap(tHatch);
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

        if (supportsCraftingMEBuffer()) {
            for (IDualInputHatch dualInputHatch : mDualInputHatches) {
                rList.addAll(Arrays.asList(dualInputHatch.getAllFluids()));
            }
        }

        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("ElementCopyingRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElementCopying_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElementCopying_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_ElementCopying_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_09"))
            .addInfo(StatCollector.translateToLocal("Tooltip_WirelessEnergyMultiMachine_10"))
            .addTecTechHatchInfo()
            .beginStructureBlock(15, 3, 15, true)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_ElementCopying_Casing"))
            .addInputBus(StatCollector.translateToLocal("Tooltip_ElementCopying_Casing"))
            .addOutputBus(StatCollector.translateToLocal("Tooltip_ElementCopying_Casing"))
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_ElementCopying_Casing"))
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_ElementCopying_Casing"))
            .toolTipFinisher();
        return tt;
    }

    @Override
    public IStructureDefinition<ElementCopying> getStructureDefinition() {
        return StructureDefinition.<ElementCopying>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(BlockLoader.metaCasing, 18))
            .addElement('B', StructureUtility.ofBlockAnyMeta(LanthItemList.ELECTRODE_CASING))
            .addElement(
                'C',
                buildHatchAdder(ElementCopying.class).casingIndex(getCasingTextureID())
                    .dot(1)
                    .atLeast(
                        HatchElement.InputHatch,
                        HatchElement.InputBus,
                        HatchElement.OutputBus,
                        HatchElement.OutputHatch,
                        HatchElement.Energy.or(HatchElement.ExoticEnergy),
                        HatchElement.Maintenance,
                        ParallelCon)
                    .buildAndChain(
                        StructureUtility
                            .onElementPass(x -> ++x.mCountCasing, StructureUtility.ofBlock(sBlockCasingsTT, 4))))
            .addElement('D', StructureUtility.ofBlock(sBlockCasingsTT, 6))
            .addElement('E', StructureUtility.ofBlock(sBlockCasingsTT, 7))
            .addElement('F', StructureUtility.ofBlock(sBlockCasingsTT, 8))
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 5;
    }

    public long getTotal(ToLongFunction<ElementCopyingEntry> costGetter) {
        long total = 0;
        for (ElementCopyingEntry entry : itemEntry) {
            total += costGetter.applyAsLong(entry);
        }
        for (ElementCopyingEntry entry : fluidEntry) {
            total += costGetter.applyAsLong(entry);
        }
        return total;
    }

    public long getNeedUUM() {
        return getTotal(ElementCopyingEntry::costUUM);
    }

    public long getNeedEU() {
        return getTotal(ElementCopyingEntry::costEU);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        super.addUIWidgets(builder, buildContext);
        buildContext.addSyncedWindow(11, this::createCopyingWindow);
        addSyncers(builder);
        builder.widget(new ButtonWidget().setOnClick((click, widget) -> {
            if (!widget.isClient()) {
                widget.getContext()
                    .openSyncedWindow(11);
            }
        })
            .setBackground(
                () -> new IDrawable[] { GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_ARROW_GREEN_UP })
            .setTooltipShowUpDelay(5)
            .setPos(174, 110)
            .setSize(16, 16));
    }

    public ModularWindow createCopyingWindow(EntityPlayer player) {
        final int WIDTH = 198;
        final int HEIGHT = 42 + (ElementCopyingRecipes.ENTRIES.size() + 8) / 10 * 18;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();

        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(Alignment.TopRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT)))
                .subtract(5, 0)
                .add(0, 4));

        for (int i = 0; i < ElementCopyingRecipes.ENTRIES.size(); i++) {
            ElementCopyingEntry entry = ElementCopyingRecipes.ENTRIES.get(i);

            int col = i % 10;
            int row = i / 10;
            int x = col * 18 + 9;
            int y = row * 18 + 10;

            if (entry instanceof ItemCopyingEntry item) {
                ItemStack stack = item.itemId()
                    .getItemStack();
                builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                    if (itemEntry.contains(item)) {
                        itemEntry.remove(item);
                    } else {
                        itemEntry.add(item);
                    }
                })
                    .addTooltip(stack.getDisplayName())
                    .setBackground(
                        () -> new IDrawable[] { itemEntry.contains(item) ? GTUITextures.BUTTON_STANDARD_PRESSED
                            : GTUITextures.BUTTON_STANDARD, new ItemDrawable(stack) })
                    .setSize(16, 16)
                    .setPos(x, y));
            } else if (entry instanceof FluidCopyingEntry fluid) {
                FluidStack stack = fluid.fluidId()
                    .getFluidStack();
                builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
                    if (fluidEntry.contains(fluid)) {
                        fluidEntry.remove(fluid);
                    } else {
                        fluidEntry.add(fluid);
                    }
                })
                    .addTooltip(stack.getLocalizedName())
                    .setBackground(
                        () -> new IDrawable[] { fluidEntry.contains(fluid) ? GTUITextures.BUTTON_STANDARD_PRESSED
                            : GTUITextures.BUTTON_STANDARD, new FluidDrawable().setFluid(stack) })
                    .setSize(16, 16)
                    .setPos(x, y));
            }
        }

        builder.widget(
            new DynamicTextWidget(
                () -> new Text(
                    StatCollector.translateToLocalFormatted(
                        "Info_ElementCopying_00",
                        itemEntry.size() + fluidEntry.size(),
                        GTUtility.formatNumbers(getNeedUUM()),
                        GTUtility.formatNumbers(getNeedEU()))).color(Color.WHITE.normal))
                            .setTextAlignment(Alignment.Center)
                            .setSize(180, 12)
                            .setPos(9, HEIGHT - 28));

        return builder.build();
    }

    public void addSyncers(ModularWindow.Builder builder) {
        builder.widget(new Utils.SetSyncer<>(itemEntry, (buffer, entry) -> {
            try {
                buffer.writeNBTTagCompoundToBuffer(entry.serialize());
            } catch (IOException e) {
                ScienceNotLeisure.LOG.error("Failed to sync entry", e);
            }
        }, buffer -> {
            try {
                return ItemCopyingEntry.deserialize(buffer.readNBTTagCompoundFromBuffer());
            } catch (IOException e) {
                ScienceNotLeisure.LOG.error("Failed to read entry", e);
                return null;
            }
        }));

        builder.widget(new Utils.SetSyncer<>(fluidEntry, (buffer, entry) -> {
            try {
                buffer.writeNBTTagCompoundToBuffer(entry.serialize());
            } catch (IOException e) {
                ScienceNotLeisure.LOG.error("Failed to sync entry", e);
            }
        }, buffer -> {
            try {
                return FluidCopyingEntry.deserialize(buffer.readNBTTagCompoundFromBuffer());
            } catch (IOException e) {
                ScienceNotLeisure.LOG.error("Failed to read entry", e);
                return null;
            }
        }));
    }

    public interface ElementCopyingEntry {

        long costUUM();

        long costEU();

        NBTTagCompound serialize();
    }

    @Desugar
    public record ItemCopyingEntry(GTUtility.ItemId itemId, long costUUM, long costEU) implements ElementCopyingEntry {

        @Override
        public NBTTagCompound serialize() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("id", itemId.writeToNBT());
            tag.setLong("uum", costUUM);
            tag.setLong("eu", costEU);
            return tag;
        }

        public static ItemCopyingEntry deserialize(NBTTagCompound tag) {
            return new ItemCopyingEntry(
                GTUtility.ItemId.create(tag.getCompoundTag("id")),
                tag.getLong("uum"),
                tag.getLong("eu"));
        }
    }

    @Desugar
    public record FluidCopyingEntry(GTUtility.FluidId fluidId, long costUUM, long costEU)
        implements ElementCopyingEntry {

        @Override
        public NBTTagCompound serialize() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("id", fluidId.writeToNBT());
            tag.setLong("uum", costUUM);
            tag.setLong("eu", costEU);
            return tag;
        }

        public static FluidCopyingEntry deserialize(NBTTagCompound tag) {
            return new FluidCopyingEntry(
                GTUtility.FluidId.create(tag.getCompoundTag("id")),
                tag.getLong("uum"),
                tag.getLong("eu"));
        }

    }
}
