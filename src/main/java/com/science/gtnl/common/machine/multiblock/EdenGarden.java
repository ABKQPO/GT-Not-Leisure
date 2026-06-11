package com.science.gtnl.common.machine.multiblock;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.drawable.UITexture;
import com.gtnewhorizon.cropsnh.api.ISeedData;
import com.gtnewhorizon.cropsnh.farming.SeedData;
import com.gtnewhorizon.cropsnh.farming.SeedStats;
import com.gtnewhorizon.cropsnh.reference.Constants;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;
import com.gtnewhorizon.cropsnh.utility.IFDropTable;
import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.MainAxisAlignment;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.builder.UIInfo;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.CycleButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedRow;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.science.gtnl.api.IGreenHouse;
import com.science.gtnl.common.gui.modularui.EdenGardenGui;
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseMode;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseModes;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseStoredCrop;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseViewMode;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoidingMode;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gtPlusPlus.core.block.ModBlocks;
import gtnhlanth.common.register.LanthItemList;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;

public class EdenGarden extends MultiMachineBase<EdenGarden> implements IGreenHouse {

    public Collection<GreenHouseMode.EIGMigrationHolder> toMigrate;
    public HashMap<ItemStack, Double> synchedGUIDropTracker = new HashMap<>();
    @Getter
    public List<GreenHouseStoredCrop> storedCrops = new LinkedList<>();
    @Getter
    @Setter
    public IFDropTable industrialFarmDropTracker = new IFDropTable();
    @Getter
    @Setter
    public IFDropTable industrialFarmGuiDropTracker = new IFDropTable();
    @Getter
    @Setter
    public int maxSeedTypes = Integer.MAX_VALUE, maxSeedCount = Integer.MAX_VALUE, setupPhase = 1;
    @Getter
    @Setter
    public GreenHouseMode mode = GreenHouseModes.Normal;
    @Getter
    @Setter
    public GreenHouseViewMode greenHouseViewMode = GreenHouseViewMode.SEEDS;

    @Getter
    @Setter
    public boolean useNoHumidity = false;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String EG_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/eden_garden";
    private static final String[][] shape = StructureUtils.readStructureFromFile(EG_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 6;
    private static final int VERTICAL_OFF_SET = 43;
    private static final int DEPTH_OFF_SET = 10;
    private static final UITexture[] MODE_ICONS = { GTGuiTextures.OVERLAY_BUTTON_ALLOW_INPUT,
        GTGuiTextures.OVERLAY_BUTTON_CYCLIC, GTGuiTextures.OVERLAY_BUTTON_ALLOW_OUTPUT };

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings10, 5);
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public ArrayList<? extends IOutputBus> getOutputBus() {
        return mOutputBusses;
    }

    @Override
    public int getMaxProgressTime() {
        return mMaxProgresstime;
    }

    @Override
    public void setMaxProgressTime(int time) {
        this.mMaxProgresstime = time;
    }

    @Override
    public void setLEUt(long lEUt) {
        this.lEUt = lEUt;
    }

    @Override
    public int getWaterUsage() {
        return 2000;
    }

    @Override
    public int getMachineMode() {
        return machineMode;
    }

    @Override
    public void setMachineMode(int machineMode) {
        this.machineMode = switch (machineMode) {
            case MODE_INPUT, MODE_FARM, MODE_OUTPUT -> machineMode;
            default -> MODE_INPUT;
        };
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public int nextMachineMode() {
        machineMode = switch (machineMode) {
            case MODE_INPUT -> MODE_FARM;
            case MODE_FARM -> MODE_OUTPUT;
            default -> MODE_INPUT;
        };
        return machineMode;
    }

    @Override
    public String getMachineModeName() {
        return switch (machineMode) {
            case MODE_FARM -> StatCollector.translateToLocal("Info_EdenGarden_Operating");
            case MODE_OUTPUT -> StatCollector.translateToLocal("Info_EdenGarden_Output");
            default -> StatCollector.translateToLocal("Info_EdenGarden_Input");
        };
    }

    @Override
    public int getIndustrialFarmTier() {
        return Math.max(0, mEnergyHatchTier);
    }

    @Override
    public long getIndustrialFarmEUt() {
        return Math.max(1L, (long) (GTValues.V[Math.max(0, mEnergyHatchTier)] * 0.5d));
    }

    @Override
    public boolean shouldUseCurrentBiome() {
        return false;
    }

    @Override
    public boolean forcesBestSeedStats() {
        return true;
    }

    @Override
    public double getGreenHouseOutputMultiplier() {
        return 5.0d;
    }

    @Override
    public void setGreenHouseOutputItems(ItemStack[] outputs) {
        this.mOutputItems = outputs;
    }

    @Override
    public boolean addItemOutputsToGreenHouse(ItemStack[] outputs) {
        return addItemOutputs(outputs);
    }

    @Override
    public Set<VoidingMode> getAllowedVoidingModes() {
        return VoidingMode.ITEM_ONLY_MODES;
    }

    @Override
    public ISeedData createRuntimeSeedData(ItemStack seedStack) {
        ISeedData seedData = IGreenHouse.super.createRuntimeSeedData(seedStack);
        if (seedData == null) return null;
        ItemStack runtimeSeed = seedStack.copy();
        SeedStats bestStats = new SeedStats(
            (byte) Constants.MAX_SEED_STAT,
            (byte) Constants.MAX_SEED_STAT,
            (byte) Constants.MAX_SEED_STAT,
            true);
        runtimeSeed.setTagCompound(bestStats.writeToNBT(new NBTTagCompound()));
        return new SeedData(seedData.getCrop(), bestStats, runtimeSeed);
    }

    @Override
    public IStructureDefinition<EdenGarden> getStructureDefinition() {
        return StructureDefinition.<EdenGarden>builder()
            .addShape(STRUCTURE_PIECE_MAIN, StructureUtility.transpose(shape))
            .addElement('A', StructureUtility.ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
            .addElement(
                'B',
                StructureUtility.ofChain(
                    buildHatchAdder(EdenGarden.class)
                        .atLeast(
                            HatchElement.Maintenance,
                            HatchElement.InputBus,
                            HatchElement.OutputBus,
                            HatchElement.InputHatch,
                            HatchElement.Maintenance,
                            HatchElement.Energy.or(HatchElement.ExoticEnergy))
                        .casingIndex(StructureUtils.getTextureIndex(GregTechAPI.sBlockCasings10, 4))
                        .hint(1)
                        .build(),
                    StructureUtility.onElementPass(
                        x -> ++x.mCountCasing,
                        StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 4))))
            .addElement('C', StructureUtility.ofBlock(GregTechAPI.sBlockCasings10, 5))
            .addElement('D', StructureUtility.ofBlock(GregTechAPI.sBlockCasings8, 10))
            .addElement('E', StructureUtility.ofBlock(GregTechAPI.sBlockCasings9, 11))
            .addElement('F', StructureUtility.ofBlock(ModBlocks.blockCasings2Misc, 3))
            .addElement('G', StructureUtility.ofBlock(LanthItemList.SHIELDED_ACCELERATOR_GLASS, 0))
            .addElement('H', StructureUtility.ofBlock(BlockLoader.metaBlockGlow, 0))
            .addElement('I', StructureUtility.ofBlock(Blocks.farmland, 0))
            .addElement(
                'J',
                StructureUtility.ofChain(
                    StructureUtility.ofBlockAnyMeta(Blocks.water),
                    StructureUtility.ofBlock(BlocksItems.getFluidBlock(InternalName.fluidDistilledWater), 0)))
            .build();
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack itemStack,
        List<StructureError> errors) {
        if (!checkPieceAndHatch(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET, errors))
            return;
        setupParameters();
        checkStructureCondition(errors, mCountCasing >= 1000);
    }

    @Override
    public boolean checkHatch() {
        return super.checkHatch() && !(this.mEnergyHatches.isEmpty() && this.mExoticEnergyHatches.isEmpty());
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
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
    public IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated();
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("EdenGardenRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EdenGarden_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EdenGarden_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EdenGarden_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EdenGarden_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EdenGarden_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EdenGarden_05"))
            .addInfo(StatCollector.translateToLocal("Tooltip_EdenGarden_06"))
            .addTecTechHatchInfo()
            .beginStructureBlock(6, 43, 10, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_EdenGarden_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EdenGarden_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_EdenGarden_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_EdenGarden_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_EdenGarden_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    public EdenGarden(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public EdenGarden(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new EdenGarden(this.mName);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (this.toMigrate == null) return;

        for (GreenHouseMode.EIGMigrationHolder holder : toMigrate) {
            holder.seed.stackSize = holder.count;
            CheckRecipeResult result = tryAddCropStack(holder.seed, false);
            if (!result.wasSuccessful() && holder.seed.stackSize > 0) {
                addItemOutputs(new ItemStack[] { holder.seed });
            }
        }
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        IGregTechTileEntity mte = getBaseMetaTileEntity();
        for (GreenHouseStoredCrop crop : storedCrops) {
            dropStoredStack(mte, crop.getSeedStack());
            dropStoredStack(mte, crop.getBlockUnderStack());
        }
        storedCrops.clear();
    }

    private void dropStoredStack(IGregTechTileEntity mte, ItemStack stack) {
        if (CropsNHUtils.isStackInvalid(stack)) return;
        EntityItem entityitem = new EntityItem(
            mte.getWorld(),
            mte.getXCoord(),
            mte.getYCoord(),
            mte.getZCoord(),
            stack);
        entityitem.delayBeforeCanPickup = 10;
        mte.getWorld()
            .spawnEntityInWorld(entityitem);
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        nextMachineMode();
        GTUtility.sendChatToPlayer(aPlayer, getMachineModeName());
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        greenHouseViewMode = greenHouseViewMode.next();
        GTUtility.sendChatToPlayer(aPlayer, greenHouseViewMode.name());
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("greenHouseViewMode", this.greenHouseViewMode.ordinal());
        NBTTagList cropListNBT = new NBTTagList();
        for (GreenHouseStoredCrop crop : this.storedCrops) {
            cropListNBT.appendTag(crop.save());
        }
        aNBT.setTag("industrialFarmProgress", this.industrialFarmDropTracker.save());
        aNBT.setTag("industrialFarmCrops", cropListNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        this.greenHouseViewMode = GreenHouseViewMode.fromOrdinal(aNBT.getInteger("greenHouseViewMode"));
        this.industrialFarmDropTracker = new IFDropTable(aNBT, "industrialFarmProgress");
        this.storedCrops.clear();
        NBTTagList cropListNBT = aNBT.getTagList("industrialFarmCrops", 10);
        for (int i = 0; i < cropListNBT.tagCount(); i++) {
            GreenHouseStoredCrop crop = GreenHouseStoredCrop.load(cropListNBT.getCompoundTagAt(i));
            if (crop.isValid()) {
                this.storedCrops.add(crop);
            }
        }
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;
        return processIndustrialFarmMode();
    }

    // TODO: Remove this MUI1 fallback after Eden Garden no longer supports MUI1 startup paths.
    @Deprecated
    public static final UIInfo<?, ?> GreenhouseUI = GreenHouseMode
        .createGreenhouseUI(GreenHouseMode.MUIContainer_Greenhouse::new);

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new EdenGardenGui(this).withMachineModeIcons(MODE_ICONS);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        openGui(aPlayer);
        return true;
    }

    @Override
    @Deprecated
    public void addConfigurationWidgets(DynamicPositionedRow configurationElements, UIBuildContext buildContext) {
        // TODO: Remove this mui1 fallback after Eden Garden no longer supports mui1 startup paths.
        buildContext.addSyncedWindow(GreenHouseMode.CONFIGURATION_WINDOW_ID, this::createConfigurationWindow);
        configurationElements.setSynced(false);
        configurationElements.widget(
            new ButtonWidget().setOnClick(
                (clickData, widget) -> {
                    if (!widget.isClient()) widget.getContext()
                        .openSyncedWindow(GreenHouseMode.CONFIGURATION_WINDOW_ID);
                })
                .setBackground(GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_CYCLIC)
                .addTooltip(StatCollector.translateToLocal("Info_EdenGarden_Configuration"))
                .setSize(16, 16));
    }

    @Override
    public void createInventorySlots() {}

    public boolean isInInventory = true;

    @Override
    @Deprecated
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        // TODO: Remove this mui1 fallback after the Eden Garden main GUI is fully ported to mui2.
        isInInventory = !getBaseMetaTileEntity().isActive();
        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.PICTURE_SCREEN_BLACK)
                .setPos(4, 4)
                .setSize(190, 85)
                .setEnabled(w -> !isInInventory));
        builder.widget(
            new CycleButtonWidget().setToggle(() -> isInInventory, i -> isInInventory = i)
                .setTextureGetter(
                    i -> i == 0 ? new Text(StatCollector.translateToLocal("Info_EdenGarden_Inventory"))
                        : new Text(StatCollector.translateToLocal("Info_EdenGarden_Status")))
                .setBackground(GTUITextures.BUTTON_STANDARD)
                .setPos(140, 91)
                .setSize(55, 16));

        final DynamicPositionedColumn screenElements = new DynamicPositionedColumn();
        drawTexts(screenElements, null);
        builder.widget(
            new Scrollable().setVerticalScroll()
                .widget(screenElements.setPos(10, 0))
                .setPos(0, 7)
                .setSize(190, 79)
                .setEnabled(w -> !isInInventory));

        builder.widget(createPowerSwitchButton(builder))
            .widget(createVoidExcessButton(builder))
            .widget(createInputSeparationButton(builder))
            .widget(createBatchModeButton(builder))
            .widget(createLockToSingleRecipeButton(builder))
            .widget(createStructureUpdateButton(builder));

        if (supportsMachineInfo()) {
            builder.widget(createMachineInfoButton(builder));
            buildContext.addSyncedWindow(MACHINE_INFO_WINDOW_ID, this::createMachineInfo);
        }

        DynamicPositionedRow configurationElements = new DynamicPositionedRow();
        addConfigurationWidgets(configurationElements, buildContext);

        builder.widget(
            configurationElements.setSpace(2)
                .setAlignment(MainAxisAlignment.SPACE_BETWEEN)
                .setPos(getRecipeLockingButtonPos().add(18, 0)));
    }

    @Override
    public String generateCurrentRecipeInfoString() {
        double maxProgressSeconds = getSafeMaxProgressSeconds();
        double progressPercent = getSafeProgressPercent();
        StringBuilder ret = new StringBuilder(
            EnumChatFormatting.WHITE + StatCollector.translateToLocal("GT5U.gui.text.progress"))
                .append(String.format("%,.2f", (double) this.mProgresstime / 20))
                .append("s / ")
                .append(String.format("%,.2f", maxProgressSeconds))
                .append("s (")
                .append(String.format("%,.1f", progressPercent))
                .append("%)\n");
        Object2IntOpenHashMap<ItemId> outputCounts = getOutputItemCounts(mOutputItems);
        ArrayList<Map.Entry<ItemStack, Double>> sortedDrops = new ArrayList<>(
            this.industrialFarmGuiDropTracker.entrySet());
        sortedDrops.sort(
            Comparator.comparing(
                a -> a.getKey()
                    .toString()
                    .toLowerCase()));

        for (Map.Entry<ItemStack, Double> drop : sortedDrops) {
            int outputSize = outputCounts.getInt(ItemId.createNoCopy(drop.getKey()));
            ret.append(EnumChatFormatting.AQUA)
                .append(
                    drop.getKey()
                        .getDisplayName())
                .append(EnumChatFormatting.WHITE)
                .append(": ");
            if (outputSize == 0) {
                ret.append(String.format("%.2f", drop.getValue() * 100))
                    .append("%\n");
            } else {
                ret.append(EnumChatFormatting.GOLD)
                    .append(
                        String.format(
                            "x%d %s(+%.2f/s)\n",
                            outputSize,
                            EnumChatFormatting.WHITE,
                            outputSize / maxProgressSeconds));
            }
        }
        return ret.toString();
    }

    public double getSafeMaxProgressSeconds() {
        return Math.max(mMaxProgresstime / 20.0D, 0.05D);
    }

    public double getSafeProgressPercent() {
        if (mMaxProgresstime <= 0) {
            return 0;
        }
        return (double) mProgresstime / mMaxProgresstime * 100;
    }

    public Object2IntOpenHashMap<ItemId> getOutputItemCounts(ItemStack[] outputItems) {
        Object2IntOpenHashMap<ItemId> outputCounts = new Object2IntOpenHashMap<>(
            outputItems == null ? 0 : outputItems.length);
        if (outputItems == null) {
            return outputCounts;
        }
        for (ItemStack outputItem : outputItems) {
            if (outputItem != null && outputItem.stackSize > 0) {
                outputCounts.addTo(ItemId.createNoCopy(outputItem), outputItem.stackSize);
            }
        }
        return outputCounts;
    }

    @Override
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        screenElements.widget(new FakeSyncWidget<>(() -> {
            HashMap<ItemStack, Double> ret = new HashMap<>();

            for (Map.Entry<ItemStack, Double> drop : this.industrialFarmGuiDropTracker.entrySet()) {
                ret.merge(drop.getKey(), drop.getValue(), Double::sum);
            }

            return ret;
        }, h -> this.synchedGUIDropTracker = h, (buffer, h) -> {
            buffer.writeVarIntToBuffer(h.size());
            for (Map.Entry<ItemStack, Double> itemStackDoubleEntry : h.entrySet()) {
                try {
                    buffer.writeItemStackToBuffer(itemStackDoubleEntry.getKey());
                    buffer.writeDouble(itemStackDoubleEntry.getValue());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, buffer -> {
            int len = buffer.readVarIntFromBuffer();
            HashMap<ItemStack, Double> ret = new HashMap<>(len);
            for (int i = 0; i < len; i++) {
                try {
                    ret.put(buffer.readItemStackFromBuffer(), buffer.readDouble());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return ret;
        }));
        super.drawTexts(screenElements, inventorySlot);
    }

    @Override
    public String[] getInfoData() {
        List<String> info = new ArrayList<>(
            Arrays.asList(
                StatCollector.translateToLocalFormatted(
                    "Info_EdenGarden_01",
                    EnumChatFormatting.GREEN + getMachineModeName() + EnumChatFormatting.RESET),

                StatCollector.translateToLocalFormatted(
                    "Info_EdenGarden_04",
                    EnumChatFormatting.GREEN,
                    this.maxSeedCount,
                    EnumChatFormatting.RESET),

                StatCollector.translateToLocalFormatted(
                    "Info_EdenGarden_05",
                    ((this.getTotalStoredCropCount() > maxSeedCount) ? EnumChatFormatting.RED
                        : EnumChatFormatting.GREEN),
                    this.getTotalStoredCropCount())));

        if (this.getTotalStoredCropCount() > this.maxSeedCount) {
            info.add(
                EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("Info_EdenGarden_07")
                    + EnumChatFormatting.RESET);
        }

        info.addAll(Arrays.asList(super.getInfoData()));
        return info.toArray(new String[0]);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }
}
