package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.drawable.UITexture;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;
import com.gtnewhorizon.cropsnh.utility.IFDropTable;
import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.gtnewhorizons.modularui.api.drawable.ItemDrawable;
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
import com.science.gtnl.common.gui.CircularGaugeDrawable;
import com.science.gtnl.common.gui.modularui.SteamGreenhouseModuleGui;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseMode;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseModes;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseStoredCrop;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseViewMode;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;

public class SteamGreenhouseModule extends SteamElevatorModule implements IGreenHouse {

    private static final UITexture[] MODE_ICONS = { GTGuiTextures.OVERLAY_BUTTON_ALLOW_INPUT,
        GTGuiTextures.OVERLAY_BUTTON_CYCLIC, GTGuiTextures.OVERLAY_BUTTON_ALLOW_OUTPUT };

    @Deprecated
    public static final UIInfo<?, ?> GreenhouseUI = GreenHouseMode
        .createGreenhouseUI(GreenHouseMode.MUIContainer_Greenhouse::new);

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
    public int maxSeedTypes = 4, maxSeedCount = 16, setupPhase = 1;

    @Getter
    @Setter
    public GreenHouseMode mode = GreenHouseModes.Normal;

    @Getter
    @Setter
    public GreenHouseViewMode greenHouseViewMode = GreenHouseViewMode.SEEDS;

    @Getter
    @Setter
    public boolean useNoHumidity = false;

    public boolean isInInventory = true;

    public SteamGreenhouseModule(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 5);
    }

    public SteamGreenhouseModule(String aName) {
        super(aName, 5);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new SteamGreenhouseModule(this.mName);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (this.toMigrate == null) return;

        for (GreenHouseMode.EIGMigrationHolder holder : toMigrate) {
            holder.seed.stackSize = holder.count;
            CheckRecipeResult result = tryAddCropStack(holder.seed, false);
            if (!result.wasSuccessful() && holder.seed.stackSize > 0) {
                addOutput(holder.seed);
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

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        this.mEfficiency = 10000;
        this.mEfficiencyIncrease = 10000;
        return processIndustrialFarmMode();
    }

    @Override
    public boolean addItemOutputsToGreenHouse(ItemStack[] outputs) {
        return addItemOutputs(outputs);
    }

    @Override
    public String getMachineType() {
        return "SteamGreenhouseModuleRecipeType";
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(StatCollector.translateToLocal("SteamGreenhouseModuleRecipeType"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamGreenhouseModule_00"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamGreenhouseModule_01"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamGreenhouseModule_02"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamGreenhouseModule_03"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamGreenhouseModule_04"))
            .addInfo(StatCollector.translateToLocal("Tooltip_SteamGreenhouseModule_05"))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
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
                    this.getTotalStoredCropCount() > maxSeedCount ? EnumChatFormatting.RED : EnumChatFormatting.GREEN,
                    this.getTotalStoredCropCount())));

        if (this.getTotalStoredCropCount() > this.maxSeedCount) {
            info.add(
                EnumChatFormatting.DARK_RED + StatCollector.translateToLocal("Info_EdenGarden_07")
                    + EnumChatFormatting.RESET);
        }

        info.addAll(Arrays.asList(super.getInfoData()));
        return info.toArray(new String[0]);
    }

    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        nextMachineMode();
        GTUtility.sendChatTrans(aPlayer, getMachineModeNameKey());
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        greenHouseViewMode = greenHouseViewMode.next();
        GTUtility.sendChatTrans(aPlayer, "Info_EdenGarden_ViewMode_Change", greenHouseViewMode.name());
        return true;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        openGui(aPlayer);
        return true;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new SteamGreenhouseModuleGui(this).withMachineModeIcons(MODE_ICONS);
    }

    @Deprecated
    public void addConfigurationWidgets(DynamicPositionedRow configurationElements, UIBuildContext buildContext) {
        // TODO: Remove this MUI1 configuration button after Steam Greenhouse Module only uses MUI2.
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
    @Deprecated
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        // TODO: Remove this MUI1 fallback after Steam Greenhouse Module only uses MUI2.
        isInInventory = !getBaseMetaTileEntity().isActive();

        builder.widget(new FakeSyncWidget.LongSyncer(this::getTotalSteamCapacityLong, val -> uiSteamCapacity = val));
        builder.widget(new FakeSyncWidget.LongSyncer(this::getLongTotalSteamStored, val -> uiSteamStored = val));
        builder.widget(
            new FakeSyncWidget.IntegerSyncer(this::getTotalSteamStoredOfAnyType, val -> uiSteamStoredOfAllTypes = val));

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

        builder.widget(
            new DrawableWidget().setDrawable(this.tierMachine == 2 ? STEAM_GAUGE_STEEL_BG : STEAM_GAUGE_BG)
                .dynamicTooltip(() -> {
                    List<String> ret = new ArrayList<>();
                    ret.add(
                        StatCollector.translateToLocal("AllSteamCapacity") + uiSteamStored
                            + "/"
                            + uiSteamCapacity
                            + "L");
                    if (uiSteamStored == 0 && uiSteamStoredOfAllTypes != 0) {
                        ret.add(EnumChatFormatting.RED + "Found steam of wrong type!");
                    }
                    return ret;
                })
                .setTooltipShowUpDelay(TOOLTIP_DELAY)
                .setUpdateTooltipEveryTick(true)
                .setSize(64, 42)
                .setPos(-64, 100));

        builder.widget(
            new DrawableWidget().setDrawable(new CircularGaugeDrawable(() -> (float) uiSteamStored / uiSteamCapacity))
                .setPos(-64 + 21, 100 + 21)
                .setSize(18, 4));

        builder.widget(
            new ItemDrawable(GTNLItemList.FakeItemSiren.get(1)).asWidget()
                .setPos(-64 + 21 - 7, 100 - 20)
                .setEnabled(w -> uiSteamStored == 0));
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
        return 16000;
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
        return StatCollector.translateToLocal(getMachineModeNameKey());
    }

    private String getMachineModeNameKey() {
        return switch (machineMode) {
            case MODE_FARM -> "Info_EdenGarden_Operating";
            case MODE_OUTPUT -> "Info_EdenGarden_Output";
            default -> "Info_EdenGarden_Input";
        };
    }

    @Override
    public int getIndustrialFarmTier() {
        return 3;
    }

    @Override
    public long getIndustrialFarmEUt() {
        return 8192L;
    }

    @Override
    public boolean shouldUseCurrentBiome() {
        return true;
    }

    @Override
    public boolean forcesBestSeedStats() {
        return false;
    }

    @Override
    public double getGreenHouseOutputMultiplier() {
        return 1.0d;
    }

    @Override
    public void setGreenHouseOutputItems(ItemStack[] outputs) {
        this.mOutputItems = outputs;
    }

    @Override
    public boolean supportsSteamOC() {
        return false;
    }

    @Deprecated
    public void tryChangeSetupPhase(EntityPlayer aPlayer) {
        // TODO: Remove this legacy MUI1 setup phase toggle after Steam Greenhouse only exposes MUI2 machine modes.
        if (this.mMaxProgresstime > 0) {
            GTUtility.sendChatTrans(aPlayer, "Info_EdenGarden_SetupPhase_Working");
            return;
        }
        this.setupPhase++;
        if (this.setupPhase == 3) this.setupPhase = 0;

        String phaseKey = switch (this.setupPhase) {
            case 0 -> "Info_EdenGarden_Operating";
            case 1 -> "Info_EdenGarden_Input";
            case 2 -> "Info_EdenGarden_Output";
            default -> "Info_EdenGarden_SetupPhase_Invalid";
        };
        GTUtility
            .sendChatTrans(aPlayer, "Info_EdenGarden_SetupPhase_Change_Format", new ChatComponentTranslation(phaseKey));
    }

    @Deprecated
    public void tryChangeMode(EntityPlayer aPlayer) {
        // TODO: Remove this legacy MUI1 greenhouse mode toggle after Steam Greenhouse only exposes MUI2 machine modes.
        if (this.mMaxProgresstime > 0) {
            GTUtility.sendChatTrans(aPlayer, "Info_EdenGarden_Mode_Working");
            return;
        }
        if (!this.storedCrops.isEmpty()) {
            GTUtility.sendChatTrans(aPlayer, "Info_EdenGarden_Mode_HasSeeds");
            return;
        }
        this.mode = GreenHouseModes.getNextMode(this.mode);
        GTUtility.sendChatTrans(aPlayer, "Info_EdenGarden_Mode_Change", this.mode.getName());
    }

    @Deprecated
    public void tryChangeHumidityMode(EntityPlayer aPlayer) {
        // TODO: Remove this legacy humidity toggle after CropsNH biome checks are the only growth environment control.
        this.useNoHumidity = !this.useNoHumidity;
        if (this.useNoHumidity) {
            GTUtility.sendChatTrans(aPlayer, "Info_EdenGarden_NoHumidityMode_Enabled");
        } else {
            GTUtility.sendChatTrans(aPlayer, "Info_EdenGarden_NoHumidityMode_Disabled");
        }
    }

    public int getTotalSeedCount() {
        return getTotalStoredCropCount();
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
}
