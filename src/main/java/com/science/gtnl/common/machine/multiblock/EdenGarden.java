package com.science.gtnl.common.machine.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;
import static com.science.gtnl.utils.machine.greenHouseManager.GreenHouseMode.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
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
import java.util.stream.Collectors;

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

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
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
import com.science.gtnl.common.machine.multiMachineBase.MultiMachineBase;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseBucket;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseDropTable;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseMode;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseModes;
import com.science.gtnl.utils.machine.greenHouseManager.buckets.GreenHouseIC2Bucket;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Textures;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import gtnhlanth.common.register.LanthItemList;
import ic2.core.init.BlocksItems;
import ic2.core.init.InternalName;
import lombok.Getter;
import lombok.Setter;

public class EdenGarden extends MultiMachineBase<EdenGarden> implements IGreenHouse {

    @Getter
    public List<GreenHouseBucket> buckets = new LinkedList<>();
    public GreenHouseDropTable dropTracker = new GreenHouseDropTable();
    public Collection<GreenHouseMode.EIGMigrationHolder> toMigrate;
    public GreenHouseDropTable guiDropTracker = new GreenHouseDropTable();
    public HashMap<ItemStack, Double> synchedGUIDropTracker = new HashMap<>();
    @Getter
    @Setter
    public int maxSeedTypes = Integer.MAX_VALUE, maxSeedCount = Integer.MAX_VALUE, setupPhase = 1;
    @Getter
    @Setter
    public GreenHouseMode mode = GreenHouseModes.Normal;

    @Getter
    @Setter
    public boolean useNoHumidity = false;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final String EG_STRUCTURE_FILE_PATH = RESOURCE_ROOT_ID + ":" + "multiblock/eden_garden";
    private static final String[][] shape = StructureUtils.readStructureFromFile(EG_STRUCTURE_FILE_PATH);
    private static final int HORIZONTAL_OFF_SET = 6;
    private static final int VERTICAL_OFF_SET = 43;
    private static final int DEPTH_OFF_SET = 10;

    @Override
    public int getCasingTextureID() {
        return StructureUtils.getTextureIndex(sBlockCasings10, 5);
    }

    @Override
    public boolean getPerfectOC() {
        return true;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 1;
    }

    @Override
    public ArrayList<MTEHatchOutputBus> getOutputBus() {
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
    public IStructureDefinition<EdenGarden> getStructureDefinition() {
        return StructureDefinition.<EdenGarden>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(shape))
            .addElement('A', ofBlock(LanthItemList.SHIELDED_ACCELERATOR_CASING, 0))
            .addElement(
                'B',
                ofChain(
                    buildHatchAdder(EdenGarden.class)
                        .atLeast(Maintenance, InputBus, OutputBus, InputHatch, Maintenance, Energy.or(ExoticEnergy))
                        .dot(1)
                        .casingIndex(StructureUtils.getTextureIndex(sBlockCasings10, 4))
                        .build(),
                    onElementPass(x -> ++x.mCountCasing, ofBlock(sBlockCasings10, 4))))
            .addElement('C', ofBlock(sBlockCasings10, 5))
            .addElement('D', ofBlock(sBlockCasings8, 10))
            .addElement('E', ofBlock(sBlockCasings9, 11))
            .addElement('F', ofBlock(ModBlocks.blockCasings2Misc, 3))
            .addElement('G', ofBlock(LanthItemList.SHIELDED_ACCELERATOR_GLASS, 0))
            .addElement('H', ofBlock(BlockLoader.metaBlockGlow, 0))
            .addElement('I', ofBlock(Blocks.farmland, 0))
            .addElement(
                'J',
                ofChain(
                    ofBlockAnyMeta(Blocks.water),
                    ofBlock(BlocksItems.getFluidBlock(InternalName.fluidDistilledWater), 0)))
            .build();
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack itemStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFF_SET, VERTICAL_OFF_SET, DEPTH_OFF_SET) || !checkHatch()) {
            return false;
        }
        setupParameters();
        return mCountCasing >= 1000;
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
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && f.isNotFlipped();
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
            .addInfo(StatCollector.translateToLocal("Tooltip_Tectech_Hatch"))
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(6, 43, 10, false)
            .addInputBus(StatCollector.translateToLocal("Tooltip_EdenGarden_Casing"), 1)
            .addOutputBus(StatCollector.translateToLocal("Tooltip_EdenGarden_Casing"), 1)
            .addInputHatch(StatCollector.translateToLocal("Tooltip_EdenGarden_Casing"), 1)
            .addEnergyHatch(StatCollector.translateToLocal("Tooltip_EdenGarden_Casing"), 1)
            .addMaintenanceHatch(StatCollector.translateToLocal("Tooltip_EdenGarden_Casing"), 1)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        List<String> info = new ArrayList<>(Arrays.asList(super.getStructureDescription(stackSize)));
        return info.toArray(new String[] {});
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

        if (this.mode == GreenHouseModes.IC2) {
            toMigrate.forEach(holder -> buckets.add(new GreenHouseIC2Bucket(this, holder.seed)));
        } else {
            this.mode = GreenHouseModes.Normal;
            for (GreenHouseMode.EIGMigrationHolder holder : toMigrate) {
                holder.seed.stackSize = holder.count;
                GreenHouseBucket bucket = this.mode.tryCreateNewBucket(this, holder.seed, Integer.MAX_VALUE, false);
                if (bucket != null) {
                    buckets.add(bucket);
                } else {
                    holder.seed.stackSize = holder.count;
                    addOutput(holder.seed);
                }
            }
        }
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        buckets.removeIf(this::tryEmptyBucket);
        if (buckets.isEmpty()) return;

        IGregTechTileEntity mte = getBaseMetaTileEntity();
        buckets.forEach(bucket -> {
            for (ItemStack stack : bucket.tryRemoveSeed(bucket.getSeedCount(), false)) {
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
        });
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (aPlayer.isSneaking()) {
            tryChangeMode(aPlayer);
        } else {
            tryChangeSetupPhase(aPlayer);
        }
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        this.tryChangeHumidityMode(aPlayer);
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("setupPhase", this.setupPhase);
        aNBT.setString("mode", this.mode.getName());
        aNBT.setBoolean("isNoHumidity", this.useNoHumidity);
        NBTTagList bucketListNBT = new NBTTagList();
        for (GreenHouseBucket b : this.buckets) {
            bucketListNBT.appendTag(b.save());
        }
        aNBT.setTag(
            "progress",
            this.dropTracker.intersect(this.guiDropTracker)
                .save());
        aNBT.setTag("buckets", bucketListNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        this.setupPhase = aNBT.getInteger("setupPhase");
        this.mode = GreenHouseModes.getModeFromName(aNBT.getString("mode"));
        this.useNoHumidity = aNBT.getBoolean("isNoHumidity");
        this.mode.restoreBuckets(aNBT.getTagList("buckets", 10), this.buckets);
        new GreenHouseDropTable(aNBT.getTagList("progress", 10)).addTo(this.dropTracker);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;
        if (setupPhase > 0) return processSetupPhase();

        CheckRecipeResult validation = validateBuckets();
        if (validation != null) return validation;

        return calculateProgressAndDrops();
    }

    public CheckRecipeResult calculateProgressAndDrops() {
        double multiplier = GreenHouseMode.EIG_BALANCE_MAX_FERTILIZER_BOOST;
        this.guiDropTracker = new GreenHouseDropTable();

        int baseTime = 1200;
        if (mode == GreenHouseModes.IC2) {
            this.mMaxProgresstime = Math.max(20, baseTime / Math.max(mEnergyHatchTier - 3, 1));
            double timeElapsed = (double) mMaxProgresstime * (1 << GreenHouseMode.EIG_BALANCE_IC2_ACCELERATOR_TIER);
            buckets.forEach(bucket -> bucket.addProgress(timeElapsed * multiplier, guiDropTracker));
        } else {
            this.mMaxProgresstime = Math.max(20, baseTime / mEnergyHatchTier);
            buckets.forEach(bucket -> bucket.addProgress(multiplier, guiDropTracker));
        }

        guiDropTracker.addTo(dropTracker, multiplier);
        mOutputItems = dropTracker.getDrops();

        lEUt = -(long) (GTValues.V[mEnergyHatchTier] * 0.99d);
        mEfficiency = mEfficiencyIncrease = 10000;

        updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public static final UIInfo<?, ?> GreenhouseUI = GreenHouseMode.createGreenhouseUI(MUIContainer_Greenhouse::new);

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        GreenhouseUI.open(
            aPlayer,
            aBaseMetaTileEntity.getWorld(),
            aBaseMetaTileEntity.getXCoord(),
            aBaseMetaTileEntity.getYCoord(),
            aBaseMetaTileEntity.getZCoord());
        return true;
    }

    @Override
    public void addConfigurationWidgets(DynamicPositionedRow configurationElements, UIBuildContext buildContext) {
        buildContext.addSyncedWindow(CONFIGURATION_WINDOW_ID, this::createConfigurationWindow);
        configurationElements.setSynced(false);
        configurationElements.widget(
            new ButtonWidget().setOnClick(
                (clickData, widget) -> {
                    if (!widget.isClient()) widget.getContext()
                        .openSyncedWindow(CONFIGURATION_WINDOW_ID);
                })
                .setBackground(GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_CYCLIC)
                .addTooltip(StatCollector.translateToLocal("Info_EdenGarden_Configuration"))
                .setSize(16, 16));
    }

    @Override
    public void createInventorySlots() {}

    public boolean isInInventory = true;

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        isInInventory = !getBaseMetaTileEntity().isActive();
        builder.widget(
            new DrawableWidget().setDrawable(GTUITextures.PICTURE_SCREEN_BLACK)
                .setPos(4, 4)
                .setSize(190, 85)
                .setEnabled(w -> !isInInventory));
        builder.widget(
            getDynamicInventory().asWidget(builder, buildContext)
                .setPos(10, 16)
                .setEnabled(w -> isInInventory));

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

        DynamicPositionedRow configurationElements = new DynamicPositionedRow();
        addConfigurationWidgets(configurationElements, buildContext);

        builder.widget(
            configurationElements.setSpace(2)
                .setAlignment(MainAxisAlignment.SPACE_BETWEEN)
                .setPos(getRecipeLockingButtonPos().add(18, 0)));
    }

    @Override
    public String generateCurrentRecipeInfoString() {
        StringBuilder ret = new StringBuilder(
            EnumChatFormatting.WHITE + StatCollector.translateToLocal("GT5U.gui.text.progress"))
                .append(String.format("%,.2f", (double) this.mProgresstime / 20))
                .append("s / ")
                .append(String.format("%,.2f", (double) this.mMaxProgresstime / 20))
                .append("s (")
                .append(String.format("%,.1f", (double) this.mProgresstime / this.mMaxProgresstime * 100))
                .append("%)\n");

        for (Map.Entry<ItemStack, Double> drop : this.synchedGUIDropTracker.entrySet()
            .stream()
            .sorted(
                Comparator.comparing(
                    a -> a.getKey()
                        .toString()
                        .toLowerCase()))
            .collect(Collectors.toList())) {
            int outputSize = Arrays.stream(this.mOutputItems)
                .filter(s -> s.isItemEqual(drop.getKey()))
                .mapToInt(i -> i.stackSize)
                .sum();
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
                            (double) outputSize / (mMaxProgresstime / 20)));
            }
        }
        return ret.toString();
    }

    @Override
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        screenElements.widget(
            new FakeSyncWidget.BooleanSyncer(
                () -> this.mode == GreenHouseModes.IC2,
                b -> this.mode = b ? GreenHouseModes.IC2 : GreenHouseModes.Normal));
        screenElements.widget(new FakeSyncWidget<>(() -> {
            HashMap<ItemStack, Double> ret = new HashMap<>();

            for (Map.Entry<ItemStack, Double> drop : this.guiDropTracker.entrySet()) {
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
            Arrays
                .asList(
                    StatCollector.translateToLocalFormatted(
                        "Info_EdenGarden_01",
                        EnumChatFormatting.GREEN
                            + (this.setupPhase == 0 ? this.mode.getName()
                                : StatCollector.translateToLocal(
                                    this.setupPhase == 1 ? "Info_EdenGarden_02" : "Info_EdenGarden_03"))
                            + EnumChatFormatting.RESET),

                    StatCollector.translateToLocalFormatted(
                        "Info_EdenGarden_04",
                        EnumChatFormatting.GREEN,
                        this.maxSeedTypes,
                        EnumChatFormatting.RESET),

                    StatCollector.translateToLocalFormatted(
                        "Info_EdenGarden_05",
                        ((this.buckets.size() > maxSeedTypes) ? EnumChatFormatting.RED : EnumChatFormatting.GREEN),
                        this.buckets.size())));

        for (GreenHouseBucket bucket : buckets) {
            info.add(bucket.getInfoData());
        }

        if (this.buckets.size() > this.maxSeedTypes) {
            info.add(
                EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("Info_EdenGarden_06")
                    + EnumChatFormatting.RESET);
        }

        if (this.getTotalSeedCount() > this.maxSeedCount) {
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
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureID()) };
    }
}
