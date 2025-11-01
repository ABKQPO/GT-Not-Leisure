package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import static com.science.gtnl.utils.machine.greenHouseManager.GreenHouseMode.*;
import static gregtech.api.metatileentity.BaseTileEntity.*;

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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

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
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.gui.CircularGaugeDrawable;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseBucket;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseDropTable;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseMode;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseModes;
import com.science.gtnl.utils.machine.greenHouseManager.buckets.GreenHouseIC2Bucket;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import lombok.Getter;
import lombok.Setter;

public class SteamGreenhouseModule extends SteamElevatorModule implements IGreenHouse {

    @Getter
    public List<GreenHouseBucket> buckets = new LinkedList<>();
    public GreenHouseDropTable dropTracker = new GreenHouseDropTable();
    public Collection<GreenHouseMode.EIGMigrationHolder> toMigrate;
    public GreenHouseDropTable guiDropTracker = new GreenHouseDropTable();
    public HashMap<ItemStack, Double> synchedGUIDropTracker = new HashMap<>();
    @Getter
    @Setter
    public int maxSeedTypes = 4, maxSeedCount = 16, setupPhase = 1;
    @Getter
    @Setter
    public GreenHouseMode mode = GreenHouseModes.Normal;

    @Getter
    @Setter
    public boolean useNoHumidity = false;

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
        return 16000;
    }

    @Override
    public String getMachineType() {
        return "SteamGreenhouseModuleRecipeType";
    }

    @Override
    public int clampRecipeOcCount(int value) {
        return 0;
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
            .addSeparator()
            .addInfo(StatCollector.translateToLocal("StructureTooComplex"))
            .addInfo(StatCollector.translateToLocal("BLUE_PRINT_INFO"))
            .beginStructureBlock(1, 5, 2, false)
            .toolTipFinisher();
        return tt;
    }

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
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
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

    public void tryChangeSetupPhase(EntityPlayer aPlayer) {
        if (this.mMaxProgresstime > 0) {
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Info_EdenGarden_SetupPhase_Working"));
            return;
        }
        this.setupPhase++;
        if (this.setupPhase == 3) this.setupPhase = 0;

        String phaseChangeMessage = StatCollector.translateToLocal("Info_EdenGarden_SetupPhase_Change") + " ";
        switch (this.setupPhase) {
            case 0:
                phaseChangeMessage += StatCollector.translateToLocal("Info_EdenGarden_Operating");
                break;
            case 1:
                phaseChangeMessage += StatCollector.translateToLocal("Info_EdenGarden_Input");
                break;
            case 2:
                phaseChangeMessage += StatCollector.translateToLocal("Info_EdenGarden_Output");
                break;
            default:
                phaseChangeMessage += StatCollector.translateToLocal("Info_EdenGarden_SetupPhase_Invalid");
                break;
        }
        GTUtility.sendChatToPlayer(aPlayer, phaseChangeMessage);
    }

    public void tryChangeMode(EntityPlayer aPlayer) {
        if (this.mMaxProgresstime > 0) {
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Info_EdenGarden_Mode_Working"));
            return;
        }
        if (!this.buckets.isEmpty()) {
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Info_EdenGarden_Mode_HasSeeds"));
            return;
        }
        this.mode = GreenHouseModes.getNextMode(this.mode);
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocalFormatted("Info_EdenGarden_Mode_Change", this.mode.getName()));
    }

    public void tryChangeHumidityMode(EntityPlayer aPlayer) {
        this.useNoHumidity = !this.useNoHumidity;
        if (this.useNoHumidity) {
            GTUtility
                .sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Info_EdenGarden_NoHumidityMode_Enabled"));
        } else {
            GTUtility
                .sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Info_EdenGarden_NoHumidityMode_Disabled"));
        }
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

    public int getTotalSeedCount() {
        return buckets.stream()
            .mapToInt(GreenHouseBucket::getSeedCount)
            .sum();
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
        this.guiDropTracker = new GreenHouseDropTable();

        if (mode == GreenHouseModes.IC2) {
            buckets.forEach(bucket -> bucket.addProgress(10, guiDropTracker));
        } else {
            buckets.forEach(bucket -> bucket.addProgress(10, guiDropTracker));
        }

        guiDropTracker.addTo(dropTracker, 1);
        mOutputItems = dropTracker.getDrops();

        lEUt = -8192L;
        mEfficiency = mEfficiencyIncrease = 10000;
        this.mMaxProgresstime = 1200;

        updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

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

    public static final UIInfo<?, ?> GreenhouseUI = GreenHouseMode.createGreenhouseUI(MUIContainer_Greenhouse::new);

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

    public boolean isInInventory = true;

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
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
}
