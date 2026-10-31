package com.science.gtnl.common.machine.multiblock.module.steamElevator;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.drawable.UITexture;
import com.google.common.collect.ImmutableMap;
import com.gtnewhorizon.cropsnh.api.ISeedData;
import com.gtnewhorizon.cropsnh.utility.CropsNHUtils;
import com.gtnewhorizon.cropsnh.utility.IFDropTable;
import com.science.gtnl.api.IGreenHouse;
import com.science.gtnl.common.greenhouse.GreenHouseModes;
import com.science.gtnl.common.greenhouse.GreenHouseRecipeInfo;
import com.science.gtnl.common.gui.modularui.multiblock.SteamGreenhouseModuleGui;
import com.science.gtnl.common.machine.tooltip.GTNLMultiblockTooltipBuilder;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseMode;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseStoredCrop;
import com.science.gtnl.utils.machine.greenHouseManager.GreenHouseViewMode;

import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;

@IMetaTileEntity.SkipGenerateDescription
public class SteamGreenhouseModule extends SteamElevatorModuleBase implements IGreenHouse {

    private static final int RUN_TICKS = 60 * SECONDS;
    private static final UITexture[] MODE_ICONS = { GTGuiTextures.OVERLAY_BUTTON_ALLOW_INPUT,
        GTGuiTextures.OVERLAY_BUTTON_CYCLIC, GTGuiTextures.OVERLAY_BUTTON_ALLOW_OUTPUT };

    public Collection<GreenHouseMode.EIGMigrationHolder> toMigrate;
    public Object2DoubleOpenHashMap<ItemStack> synchedGUIDropTracker = new Object2DoubleOpenHashMap<>();

    @Getter
    public List<GreenHouseStoredCrop> storedCrops = new ObjectArrayList<>();

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
    public GreenHouseViewMode greenHouseViewMode = GreenHouseViewMode.STATUS;

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
        return switch (machineMode) {
            case MODE_INPUT -> {
                industrialFarmDropTracker.clear();
                yield checkProcessingInputMode();
            }
            case MODE_FARM -> {
                CheckRecipeResult result = checkProcessingFarmMode();
                if (!result.wasSuccessful()) {
                    industrialFarmDropTracker.clear();
                }
                yield result;
            }
            case MODE_OUTPUT -> {
                industrialFarmDropTracker.clear();
                yield checkProcessingOutputMode();
            }
            default -> CheckRecipeResultRegistry.NO_RECIPE;
        };
    }

    @Override
    public boolean addItemOutputsToGreenHouse(ItemStack[] outputs) {
        return addItemOutputs(outputs);
    }

    @Override
    public void setGreenHouseViewMode(GreenHouseViewMode mode) {
        this.greenHouseViewMode = mode == null ? GreenHouseViewMode.STATUS : mode.withoutBlocks();
    }

    @Override
    public boolean supportsBlockUnderView() {
        return false;
    }

    @Override
    public CheckRecipeResult tryAddBlockUnderStack(ItemStack input, boolean simulate) {
        if (CropsNHUtils.isStackInvalid(input)) return CheckRecipeResultRegistry.NO_RECIPE;

        ItemStack acceptedType = getAcceptedBlockUnderType();
        if (CropsNHUtils.isStackValid(acceptedType) && !GTUtility.areStacksEqual(acceptedType, input, false)) {
            return BLOCK_UNDER_NOT_FOUND;
        }

        int remaining = input.stackSize;
        boolean inserted = false;
        for (GreenHouseStoredCrop crop : storedCrops) {
            if (remaining <= 0) break;

            ISeedData seedData = CropsNHUtils.getSeedData(crop.getSeedStack(), false, true);
            if (seedData == null || !needsBlockUnder(seedData)) continue;
            int missing = getMissingBlockUnderCount(crop);
            if (missing <= 0) continue;

            ItemStack blockUnder = crop.getBlockUnderStack();
            if (CropsNHUtils.isStackValid(blockUnder)) {
                if (!GTUtility.areStacksEqual(blockUnder, input, false)) continue;
            } else {
                if (!isValidBlockUnder(seedData, input)) continue;
                if (!simulate) {
                    blockUnder = CropsNHUtils.copyStackWithSize(input, 0);
                    crop.setBlockUnderStack(blockUnder);
                }
            }

            int toInsert = Math.min(remaining, missing);
            remaining -= toInsert;
            inserted = true;
            if (!simulate) {
                input.stackSize -= toInsert;
                blockUnder.stackSize += toInsert;
            }
        }

        return inserted ? CheckRecipeResultRegistry.SUCCESSFUL : BLOCK_UNDER_NOT_FOUND;
    }

    private ItemStack getAcceptedBlockUnderType() {
        for (GreenHouseStoredCrop crop : storedCrops) {
            ItemStack blockUnder = crop.getBlockUnderStack();
            if (CropsNHUtils.isStackValid(blockUnder)) {
                return blockUnder;
            }
        }
        return null;
    }

    @Override
    public boolean supportsPowerPanel() {
        return false;
    }

    @Override
    public String getMachineType() {
        return "gtnl.machine.steam_greenhouse_module.recipe_type";
    }

    @Override
    public TooltipStats getTooltipStats() {
        return null;
    }

    @Override
    public Map<String, Object> getTooltipVars() {
        return ImmutableMap.of("run_seconds", RUN_TICKS / SECONDS);
    }

    @Override
    public int getCycleDuration() {
        return RUN_TICKS;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip(this)
            .toolTipFinisher();
        return tt;
    }

    @Override
    public String generateCurrentRecipeInfoString() {
        return GreenHouseRecipeInfo
            .describe(mProgresstime, mMaxProgresstime, mOutputItems, industrialFarmGuiDropTracker.entrySet());
    }

    @Override
    public String[] getInfoData() {
        List<String> info = new ObjectArrayList<>(
            Arrays.asList(
                IGregTechDeviceInformation.encode(
                    "gtnl.machine.eden_garden.info.mode",
                    EnumChatFormatting.GREEN,
                    IGregTechDeviceInformation.translatable(getMachineModeKey()),
                    EnumChatFormatting.RESET),
                IGregTechDeviceInformation.encode(
                    "gtnl.machine.eden_garden.info.max_slots",
                    EnumChatFormatting.GREEN,
                    this.maxSeedCount,
                    EnumChatFormatting.RESET),
                IGregTechDeviceInformation.encode(
                    "gtnl.machine.eden_garden.info.used_slots",
                    this.getTotalStoredCropCount() > maxSeedCount ? EnumChatFormatting.RED : EnumChatFormatting.GREEN,
                    this.getTotalStoredCropCount())));

        if (this.getTotalStoredCropCount() > this.maxSeedCount) {
            info.add(
                IGregTechDeviceInformation.encode(
                    "gtnl.machine.eden_garden.info.too_many_seeds.format",
                    EnumChatFormatting.DARK_RED,
                    EnumChatFormatting.RESET));
        }

        info.addAll(Arrays.asList(super.getInfoData()));
        return info.toArray(new String[0]);
    }

    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        nextMachineMode();
        GTUtility.sendChatTrans(aPlayer, getMachineModeKey());
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        greenHouseViewMode = greenHouseViewMode.nextWithoutBlocks();
        GTUtility
            .sendChatTrans(aPlayer, "gtnl.machine.eden_garden.message.view_mode_changed", greenHouseViewMode.name());
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
        setMachineMode(machineMode);
        this.greenHouseViewMode = aNBT.hasKey("greenHouseViewMode")
            ? GreenHouseViewMode.fromOrdinalWithoutBlocks(aNBT.getInteger("greenHouseViewMode"))
            : GreenHouseViewMode.STATUS;
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
        int normalizedMachineMode = switch (machineMode) {
            case MODE_INPUT, MODE_FARM, MODE_OUTPUT -> machineMode;
            default -> MODE_INPUT;
        };
        super.setMachineMode(normalizedMachineMode);
        this.setupPhase = setupPhaseFromMachineMode(this.machineMode);
    }

    @Override
    public boolean supportsMachineModeSwitch() {
        return true;
    }

    @Override
    public int nextMachineMode() {
        int nextMode = switch (machineMode) {
            case MODE_INPUT -> MODE_FARM;
            case MODE_FARM -> MODE_OUTPUT;
            default -> MODE_INPUT;
        };
        setMachineMode(nextMode);
        return machineMode;
    }

    @Override
    public String getMachineModeKey() {
        return switch (machineMode) {
            case MODE_FARM -> "gtnl.machine.eden_garden.mode.operating";
            case MODE_OUTPUT -> "gtnl.machine.eden_garden.mode.output";
            default -> "gtnl.machine.eden_garden.mode.input";
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
        return 1.5d;
    }

    @Override
    public double getCropDropChanceMultiplier(ISeedData seedData) {
        return 1.5d;
    }

    @Override
    public void setGreenHouseOutputItems(ItemStack[] outputs) {
        this.mOutputItems = outputs;
    }

    @Override
    public boolean supportsSteamOC() {
        return false;
    }

    @Override
    public void tryChangeSetupPhase(EntityPlayer aPlayer) {
        if (this.mMaxProgresstime > 0) {
            GTUtility.sendChatTrans(aPlayer, "gtnl.machine.eden_garden.message.setup_mode_running");
            return;
        }
        this.setupPhase++;
        if (this.setupPhase == 3) this.setupPhase = 0;
        setMachineMode(machineModeFromSetupPhase(this.setupPhase));

        String phaseKey = switch (this.setupPhase) {
            case 0 -> "gtnl.machine.eden_garden.mode.operating";
            case 1 -> "gtnl.machine.eden_garden.mode.input";
            case 2 -> "gtnl.machine.eden_garden.mode.output";
            default -> "gtnl.machine.eden_garden.message.invalid_setup_phase";
        };
        GTUtility.sendChatTrans(
            aPlayer,
            "gtnl.machine.eden_garden.message.setup_phase_changed.format",
            new ChatComponentTranslation(phaseKey));
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
