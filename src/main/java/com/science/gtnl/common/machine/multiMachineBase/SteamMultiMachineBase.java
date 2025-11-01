package com.science.gtnl.common.machine.multiMachineBase;

import static bartworks.system.material.WerkstoffLoader.BWBlockCasings;
import static com.science.gtnl.utils.enums.GTNLMachineID.BIG_STEAM_INPUT_HATCH;
import static com.science.gtnl.utils.enums.GTNLMachineID.PIPELESS_STEAM_HATCH;
import static com.science.gtnl.utils.steam.SteamWirelessNetworkManager.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.GregTechAPI.sBlockFrames;
import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTUtility.filterValidMTEs;
import static gregtech.api.util.GTUtility.validMTEList;
import static gtPlusPlus.core.block.ModBlocks.blockCustomMachineCasings;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.ItemDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.IWidgetBuilder;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.NumericWidget;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.hatch.WirelessSteamEnergyHatch;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.StructureUtils;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.enums.SteamTypes;
import com.science.gtnl.utils.gui.CircularGaugeDrawable;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

import gregtech.api.enums.StructureError;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.metatileentity.IItemLockable;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.metatileentity.implementations.MTEHatchMultiInput;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.HatchElementBuilder;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;
import gregtech.common.render.GTRenderedTexture;
import gregtech.common.tileentities.machines.IDualInputHatch;
import gregtech.common.tileentities.machines.IDualInputInventory;
import gregtech.common.tileentities.machines.IDualInputInventoryWithPattern;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusInput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusOutput;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTESteamMultiBase;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public abstract class SteamMultiMachineBase<T extends SteamMultiMachineBase<T>> extends MTESteamMultiBase<T> {

    public static final int OC_WINDOW_ID = 11;
    public double configSpeedBoost = 1;
    public int recipeOcCount = 0;
    public int tierAdvancedCasing = -1;
    public int tierBrickCasing = -1;
    public int tierPlatedCasing = -1;
    public int tierPipeCasing = -1;
    public int tierFireboxCasing = -1;
    public int tierMaterialBlock = -1;
    public int tierGearCasing = -1;
    public int tierFrameCasing = -1;
    public int tierIndustrialCasing = -1;
    public int tierMachineFrame = -1;
    public int tierMachineCasing = -1;
    public int tierMachine = 0;
    public int mCountCasing = 0;
    public boolean isBroken = true;
    public ArrayList<CustomFluidHatch> mSteamBigInputFluids = new ArrayList<>();
    public ArrayList<CustomFluidHatch> mSteamWirelessInputFluids = new ArrayList<>();
    public long uiSteamStored = 0;
    public long uiSteamCapacity = 0;
    public int uiSteamStoredOfAllTypes = 0;

    public UUID ownerUUID;
    public UUID teamUUID;
    public boolean isInTeam;
    public BigInteger steamDisplay;

    public static final UITexture STEAM_GAUGE_BG = UITexture
        .fullImage(ModList.ScienceNotLeisure.ID, "gui/background/steam_dial");
    public static final UITexture STEAM_GAUGE_STEEL_BG = UITexture
        .fullImage(ModList.ScienceNotLeisure.ID, "gui/background/steam_dial_steel");

    public SteamMultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public SteamMultiMachineBase(String aName) {
        super(aName);
    }

    @Nullable
    public static Integer getTierAdvancedCasing(Block block, int meta) {
        if (block == null) return null;
        if (block == BWBlockCasings && 32066 == meta) return 1;
        if (block == BWBlockCasings && 32071 == meta) return 2;
        return null;
    }

    @Nullable
    public static Integer getTierMachineCasing(Block block, int meta) {
        if (block == null) return null;
        if (block == sBlockCasings1 && 10 == meta) {
            return 1;
        }
        if (block == sBlockCasings2 && 0 == meta) {
            return 2;
        }
        return null;
    }

    @Nullable
    public static Integer getTierPipeCasing(Block block, int meta) {
        if (block == null) return null;
        if (block == sBlockCasings2 && 12 == meta) return 1;
        if (block == sBlockCasings2 && 13 == meta) return 2;
        return null;
    }

    @Nullable
    public static Integer getTierGearCasing(Block block, int meta) {
        if (block == null) return null;
        if (block == sBlockCasings2 && 2 == meta) return 1;
        if (block == sBlockCasings2 && 3 == meta) return 2;
        return null;
    }

    @Nullable
    public static Integer getTierMaterialBlockCasing(Block block, int meta) {
        if (block == null) return null;
        if (block == Blocks.iron_block) return 1;
        if (block == sBlockMetal6 && 13 == meta) return 2;
        return null;
    }

    @Nullable
    public static Integer getTierFrameCasing(Block block, int meta) {
        if (block == null) return null;
        if (block == sBlockFrames && 300 == meta) return 1;
        if (block == sBlockFrames && 305 == meta) return 2;
        return null;
    }

    @Nullable
    public static Integer getTierBrickCasing(Block block, int meta) {
        if (block == null) return null;
        if (block == BlockLoader.metaBlockColumn && 0 == meta) return 1;
        if (block == BlockLoader.metaBlockColumn && 1 == meta) return 2;
        return null;
    }

    @Nullable
    public static Integer getTierPlatedCasing(Block block, int meta) {
        if (block == null) return null;
        if (block == blockCustomMachineCasings && 0 == meta) return 1;
        if (block == sBlockCasings2 && 0 == meta) return 2;
        return null;
    }

    @Nullable
    public static Integer getTierFireboxCasing(Block block, int meta) {
        if (block == null) return null;
        if (block == sBlockCasings3 && 13 == meta) return 1;
        if (block == sBlockCasings3 && 14 == meta) return 2;
        return null;
    }

    @Nullable
    public static Integer getTierIndustrialCasing(Block block, int meta) {
        if (block == null) return null;
        if (block == BlockLoader.metaCasing02 && 1 == meta) return 1;
        if (block == BlockLoader.metaCasing02 && 2 == meta) return 2;
        return null;
    }

    @Nullable
    public static Integer getTierMachineFrame(Block block, int meta) {
        if (block == null) return null;
        if (block == BlockLoader.metaBlockColumn && 4 == meta) return 1;
        if (block == BlockLoader.metaBlockColumn && 5 == meta) return 2;
        return null;
    }

    @Override
    public GTRenderedTexture getFrontOverlay() {
        return null;
    }

    @Override
    public GTRenderedTexture getFrontOverlayActive() {
        return null;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @Override
    public boolean getDefaultInputSeparationMode() {
        return false;
    }

    public void updateHatchTexture() {
        for (MTEHatch h : mSteamInputs) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mSteamOutputs) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mSteamInputFluids) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mSteamBigInputFluids) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mSteamWirelessInputFluids) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mMaintenanceHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mInputBusses) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputBusses) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mInputHatches) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mOutputHatches) h.updateTexture(getCasingTextureID());
        for (IDualInputHatch h : mDualInputHatches) h.updateTexture(getCasingTextureID());
    }

    public int getCasingTextureID() {
        if (tierAdvancedCasing == 2 || tierBrickCasing == 2
            || tierPlatedCasing == 2
            || tierIndustrialCasing == 2
            || tierMachineFrame == 2
            || tierPipeCasing == 2
            || tierFireboxCasing == 2
            || tierMaterialBlock == 2
            || tierGearCasing == 2
            || tierFrameCasing == 2
            || tierMachineCasing == 2
            || tierMachine == 2) {
            return StructureUtils.getTextureIndex(sBlockCasings2, 0);
        }
        return StructureUtils.getTextureIndex(sBlockCasings1, 10);
    }

    public boolean checkHatches() {
        return !mSteamInputFluids.isEmpty() || !mSteamBigInputFluids.isEmpty() || !mSteamWirelessInputFluids.isEmpty();
    }

    @Override
    public void onValueUpdate(byte aValue) {
        if ((byte) tierMachine != aValue) {
            tierMachine = (byte) (aValue & 0x0F);
        }
    }

    @Override
    public byte getUpdateData() {
        if (tierMachine <= 0) return 0;
        return (byte) tierMachine;
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> info = new ArrayList<>(Arrays.asList(super.getInfoData()));
        info.add(StatCollector.translateToLocal("MachineTierTooltip") + EnumChatFormatting.YELLOW + tierMachine);
        info.add(StatCollector.translateToLocal("ParallelTooltip") + EnumChatFormatting.YELLOW + getTrueParallel());
        return info.toArray(new String[0]);
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        currenttip.add(
            StatCollector.translateToLocal("GTPP.machines.tier") + ": "
                + EnumChatFormatting.YELLOW
                + getSteamTierTextForWaila(tag)
                + EnumChatFormatting.RESET);
        currenttip.add(
            StatCollector.translateToLocal("GT5U.multiblock.curparallelism") + ": "
                + EnumChatFormatting.BLUE
                + tag.getInteger("parallel")
                + EnumChatFormatting.RESET);

        String steamNetworkOwner = tag.getString("SteamNetworkOwner");
        boolean isInTeam = tag.getBoolean("isInSteamNetwork");

        if (!isInTeam) {
            currenttip.add(StatCollector.translateToLocalFormatted("Info_SteamNetwork_00", steamNetworkOwner));
        } else {
            String steamNetworkDisplay = tag.getString("SteamNetworkDisplay");
            currenttip.add(
                StatCollector
                    .translateToLocalFormatted("Info_SteamNetwork_01", steamNetworkOwner, steamNetworkDisplay));
            if (tag.hasKey("SteamNetworkTeam")) {
                currenttip.add(
                    StatCollector.translateToLocalFormatted(
                        "Info_SteamNetwork_02",
                        steamNetworkOwner,
                        tag.getString("SteamNetworkTeam")));
            }
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("tierMachine", tierMachine);
        tag.setInteger("parallel", getTrueParallel());

        tag.setString("SteamNetworkOwner", SpaceProjectManager.getPlayerNameFromUUID(ownerUUID));
        tag.setBoolean("isInSteamNetwork", isInTeam);

        if (isInTeam) {
            tag.setString(
                "SteamNetworkDisplay",
                steamDisplay.toString()
                    .length() > 10 ? GTUtility.scientificFormat(steamDisplay) : GTUtility.formatNumbers(steamDisplay));
            if (!ownerUUID.equals(teamUUID)) {
                tag.setString("SteamNetworkTeam", SpaceProjectManager.getPlayerNameFromUUID(teamUUID));
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("tierMachine", tierMachine);
        aNBT.setInteger("mMode", machineMode);
        aNBT.setInteger("recipeOcCount", recipeOcCount);
        if (ownerUUID != null) {
            aNBT.setString("OwnerUUID", ownerUUID.toString());
        }
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        tierMachine = aNBT.getInteger("tierMachine");
        recipeOcCount = aNBT.getInteger("recipeOcCount");
        ownerUUID = UUID.fromString(aNBT.getString("OwnerUUID"));
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (!aBaseMetaTileEntity.isServerSide()) return;

        ownerUUID = aBaseMetaTileEntity.getOwnerUuid();

        SpaceProjectManager.checkOrCreateTeam(ownerUUID);

        isInTeam = SpaceProjectManager.isInTeam(ownerUUID);

        if (isInTeam) {
            teamUUID = SpaceProjectManager.getLeader(ownerUUID);
            steamDisplay = getUserSteam(ownerUUID);
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (aTick % 200 == 0L) {
                isInTeam = SpaceProjectManager.isInTeam(ownerUUID);

                if (isInTeam) {
                    teamUUID = SpaceProjectManager.getLeader(ownerUUID);
                    steamDisplay = getUserSteam(ownerUUID);
                }
            }
            if (this.mUpdate == 1 || this.mStartUpCheck == 1) {
                this.mSteamInputs.clear();
                this.mSteamOutputs.clear();
                this.mInputHatches.clear();
                this.mSteamInputFluids.clear();
                this.mSteamBigInputFluids.clear();
                this.mSteamWirelessInputFluids.clear();
            }
            if (mUpdate < -250) mUpdate = 50;
            if ((aTick % 1200) == 0) {
                isBroken = true;
            }
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @ApiStatus.OverrideOnly
    public ProcessingLogic createProcessingLogic() {
        return new GTNL_ProcessingLogic() {

            @Override
            @Nonnull
            public GTNL_OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setExtraDurationModifier(configSpeedBoost)
                    .setEUtDiscount(getEUtDiscount())
                    .setDurationModifier(getDurationModifier())
                    .setPerfectOC(getPerfectOC())
                    .setMaxTierSkips(getMaxTierSkip())
                    .setMaxOverclocks(getMaxOverclocks());
            }

        }.setMaxParallelSupplier(this::getTrueParallel);
    }

    /**
     * Proxy Perfect Overclock Supplier.
     *
     * @return If true, enable Perfect Overclock.
     */
    @ApiStatus.OverrideOnly
    public boolean getPerfectOC() {
        return false;
    }

    @ApiStatus.OverrideOnly
    public int getMaxOverclocks() {
        return 0;
    }

    @ApiStatus.OverrideOnly
    public int getMaxTierSkip() {
        return 0;
    }

    @ApiStatus.OverrideOnly
    public double getEUtDiscount() {
        return (1 << (2 * Math.min(4, recipeOcCount)));
    }

    @ApiStatus.OverrideOnly
    public double getDurationModifier() {
        return 1.0 / (1 << Math.min(4, recipeOcCount));
    }

    @Override
    public boolean addToMachineList(final IGregTechTileEntity aTileEntity, final int aBaseCasingIndex) {
        boolean aDidAdd = super.addToMachineList(aTileEntity, aBaseCasingIndex);

        if (aTileEntity == null) {
            log("Invalid IGregTechTileEntity");
            return false;
        }
        final IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            log("Invalid IMetaTileEntity");
            return false;
        }

        if (aMetaTileEntity instanceof WirelessSteamEnergyHatch) {
            log("Adding Steam Wireless Input Hatch");
            aDidAdd = addToMachineListInternal(mSteamWirelessInputFluids, aMetaTileEntity, aBaseCasingIndex);
        } else if (aMetaTileEntity instanceof CustomFluidHatch) {
            log("Adding Steam Big Input Hatch");
            aDidAdd = addToMachineListInternal(mSteamBigInputFluids, aMetaTileEntity, aBaseCasingIndex);
        }

        return aDidAdd;
    }

    @Override
    public ArrayList<ItemStack> getAllStoredInputs() {
        ArrayList<ItemStack> rList = new ArrayList<>();

        if (supportsCraftingMEBuffer()) {
            for (IDualInputHatch dualInputHatch : mDualInputHatches) {
                rList.addAll(Arrays.asList(dualInputHatch.getAllItems()));
            }
        }

        Map<GTUtility.ItemId, ItemStack> inputsFromME = new HashMap<>();
        for (MTEHatchInputBus tHatch : validMTEList(mInputBusses)) {
            if (tHatch instanceof MTEHatchCraftingInputME) {
                continue;
            }
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            boolean isMEBus = tHatch instanceof MTEHatchInputBusME;
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    if (isMEBus) {
                        // Prevent the same item from different ME buses from being recognized
                        inputsFromME.put(GTUtility.ItemId.createNoCopy(itemStack), itemStack);
                    } else {
                        rList.add(itemStack);
                    }
                }
            }
        }

        for (MTEHatchSteamBusInput tHatch : validMTEList(mSteamInputs)) {
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    rList.add(itemStack);
                }
            }
        }

        ItemStack stackInSlot1 = getStackInSlot(1);
        if (stackInSlot1 != null && stackInSlot1.getUnlocalizedName()
            .startsWith("gt.integrated_circuit")) rList.add(stackInSlot1);
        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;
    }

    @Override
    public boolean depleteInput(FluidStack aLiquid) {
        if (aLiquid == null) return false;
        for (MTEHatchCustomFluidBase tHatch : validMTEList(mSteamInputFluids)) {
            FluidStack tLiquid = tHatch.getFluid();
            if (tLiquid != null && tLiquid.isFluidEqual(aLiquid)) {
                tLiquid = tHatch.drain(aLiquid.amount, false);
                if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
                    tLiquid = tHatch.drain(aLiquid.amount, true);
                    return tLiquid != null && tLiquid.amount >= aLiquid.amount;
                }
            }
        }
        for (CustomFluidHatch tHatch : validMTEList(mSteamBigInputFluids)) {
            FluidStack tLiquid = tHatch.getFluid();
            if (tLiquid != null && tLiquid.isFluidEqual(aLiquid)) {
                tLiquid = tHatch.drain(aLiquid.amount, false);
                if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
                    tLiquid = tHatch.drain(aLiquid.amount, true);
                    return tLiquid != null && tLiquid.amount >= aLiquid.amount;
                }
            }
        }
        for (CustomFluidHatch tHatch : validMTEList(mSteamWirelessInputFluids)) {
            FluidStack tLiquid = tHatch.getFluid();
            if (tLiquid != null && tLiquid.isFluidEqual(aLiquid)) {
                tLiquid = tHatch.drain(aLiquid.amount, false);
                if (tLiquid != null && tLiquid.amount >= aLiquid.amount) {
                    tLiquid = tHatch.drain(aLiquid.amount, true);
                    return tLiquid != null && tLiquid.amount >= aLiquid.amount;
                }
            }
        }
        return false;
    }

    @Override
    public boolean depleteInput(ItemStack aStack) {
        if (GTUtility.isStackInvalid(aStack)) return false;

        for (MTEHatchCustomFluidBase tHatch : validMTEList(mSteamInputFluids)) {
            if (GTUtility.areStacksEqual(
                aStack,
                tHatch.getBaseMetaTileEntity()
                    .getStackInSlot(0))) {
                if (tHatch.getBaseMetaTileEntity()
                    .getStackInSlot(0).stackSize >= aStack.stackSize) {
                    tHatch.getBaseMetaTileEntity()
                        .decrStackSize(0, aStack.stackSize);
                    return true;
                }
            }
        }

        for (CustomFluidHatch tHatch : validMTEList(mSteamBigInputFluids)) {
            if (GTUtility.areStacksEqual(
                aStack,
                tHatch.getBaseMetaTileEntity()
                    .getStackInSlot(0))) {
                if (tHatch.getBaseMetaTileEntity()
                    .getStackInSlot(0).stackSize >= aStack.stackSize) {
                    tHatch.getBaseMetaTileEntity()
                        .decrStackSize(0, aStack.stackSize);
                    return true;
                }
            }
        }

        for (CustomFluidHatch tHatch : validMTEList(mSteamWirelessInputFluids)) {
            if (GTUtility.areStacksEqual(
                aStack,
                tHatch.getBaseMetaTileEntity()
                    .getStackInSlot(0))) {
                if (tHatch.getBaseMetaTileEntity()
                    .getStackInSlot(0).stackSize >= aStack.stackSize) {
                    tHatch.getBaseMetaTileEntity()
                        .decrStackSize(0, aStack.stackSize);
                    return true;
                }
            }
        }

        for (MTEHatchSteamBusInput tHatch : validMTEList(mSteamInputs)) {
            tHatch.mRecipeMap = getRecipeMap();
            final IGregTechTileEntity baseMetaTileEntity = tHatch.getBaseMetaTileEntity();
            for (int i = baseMetaTileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack stackInSlot = baseMetaTileEntity.getStackInSlot(i);
                if (GTUtility.areStacksEqual(aStack, stackInSlot)) {
                    if (stackInSlot.stackSize >= aStack.stackSize) {
                        baseMetaTileEntity.decrStackSize(i, aStack.stackSize);
                        return true;
                    }
                }
            }
        }

        for (MTEHatchInputBus tHatch : validMTEList(mInputBusses)) {
            tHatch.mRecipeMap = getRecipeMap();
            final IGregTechTileEntity baseMetaTileEntity = tHatch.getBaseMetaTileEntity();
            for (int i = baseMetaTileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack stackInSlot = baseMetaTileEntity.getStackInSlot(i);
                if (GTUtility.areStacksEqual(aStack, stackInSlot)) {
                    if (stackInSlot.stackSize >= aStack.stackSize) {
                        baseMetaTileEntity.decrStackSize(i, aStack.stackSize);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<FluidStack> getStoredSteamFluids() {
        ArrayList<FluidStack> rList = new ArrayList<>();
        for (MTEHatchCustomFluidBase tHatch : validMTEList(mSteamInputFluids)) {
            if (tHatch.getFillableStack() != null) {
                rList.add(tHatch.getFillableStack());
            }
        }
        for (CustomFluidHatch tHatch : validMTEList(mSteamWirelessInputFluids)) {
            if (tHatch.getFillableStack() != null) {
                rList.add(tHatch.getFillableStack());
            }
        }
        for (CustomFluidHatch tHatch : validMTEList(mSteamBigInputFluids)) {
            if (tHatch.getFillableStack() != null) {
                rList.add(tHatch.getFillableStack());
            }
        }
        return rList;
    }

    @Override
    public ArrayList<ItemStack> getStoredInputsForColor(Optional<Byte> color) {
        ArrayList<ItemStack> rList = new ArrayList<>();
        Map<GTUtility.ItemId, ItemStack> inputsFromME = new HashMap<>();
        for (MTEHatchInputBus tHatch : validMTEList(mInputBusses)) {
            if (tHatch instanceof MTEHatchCraftingInputME) {
                continue;
            }
            byte busColor = tHatch.getColor();
            if (color.isPresent() && busColor != -1 && busColor != color.get()) continue;
            tHatch.mRecipeMap = getRecipeMap();
            IGregTechTileEntity tileEntity = tHatch.getBaseMetaTileEntity();
            boolean isMEBus = tHatch instanceof MTEHatchInputBusME;
            for (int i = tileEntity.getSizeInventory() - 1; i >= 0; i--) {
                ItemStack itemStack = tileEntity.getStackInSlot(i);
                if (itemStack != null) {
                    if (isMEBus) {
                        // Prevent the same item from different ME buses from being recognized
                        inputsFromME.put(GTUtility.ItemId.createNoCopy(itemStack), itemStack);
                    } else {
                        rList.add(itemStack);
                    }
                }
            }
        }

        ItemStack stackInSlot1 = getStackInSlot(1);
        if (stackInSlot1 != null && stackInSlot1.getUnlocalizedName()
            .startsWith("gt.integrated_circuit")) rList.add(stackInSlot1);
        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }

        for (MTEHatchSteamBusInput tHatch : validMTEList(mSteamInputs)) {
            byte hatchColor = tHatch.getBaseMetaTileEntity()
                .getColorization();
            if (color.isPresent() && hatchColor != -1 && hatchColor != color.get()) continue;
            tHatch.mRecipeMap = getRecipeMap();
            for (int i = tHatch.getBaseMetaTileEntity()
                .getSizeInventory() - 1; i >= 0; i--) {
                if (tHatch.getBaseMetaTileEntity()
                    .getStackInSlot(i) != null) {
                    rList.add(
                        tHatch.getBaseMetaTileEntity()
                            .getStackInSlot(i));
                }
            }
        }
        return rList;
    }

    @Override
    public ArrayList<ItemStack> getStoredOutputs() {
        ArrayList<ItemStack> rList = new ArrayList<>();

        if (mOutputBusses != null && !mOutputBusses.isEmpty()) {
            for (MTEHatchOutputBus tHatch : validMTEList(mOutputBusses)) {
                IGregTechTileEntity baseMetaTileEntity = tHatch.getBaseMetaTileEntity();
                for (int i = baseMetaTileEntity.getSizeInventory() - 1; i >= 0; i--) {
                    rList.add(baseMetaTileEntity.getStackInSlot(i));
                }
            }
        }

        if (mSteamOutputs != null && !mSteamOutputs.isEmpty()) {
            for (MTEHatchOutputBus tHatch : validMTEList(mSteamOutputs)) {
                for (int i = tHatch.getBaseMetaTileEntity()
                    .getSizeInventory() - 1; i >= 0; i--) {
                    rList.add(
                        tHatch.getBaseMetaTileEntity()
                            .getStackInSlot(i));
                }
            }
        }

        return rList;
    }

    @Override
    public List<ItemStack> getItemOutputSlots(ItemStack[] toOutput) {
        List<ItemStack> ret = new ArrayList<>();

        if (mOutputBusses != null && !mOutputBusses.isEmpty()) {
            for (final MTEHatch tBus : validMTEList(mOutputBusses)) {
                if (!(tBus instanceof MTEHatchOutputBusME)) {
                    final IInventory tBusInv = tBus.getBaseMetaTileEntity();
                    for (int i = 0; i < tBusInv.getSizeInventory(); i++) {
                        final ItemStack stackInSlot = tBus.getStackInSlot(i);

                        if (stackInSlot == null && tBus instanceof IItemLockable lockable && lockable.isLocked()) {
                            assert lockable.getLockedItem() != null;
                            ItemStack fakeItemStack = lockable.getLockedItem()
                                .copy();
                            fakeItemStack.stackSize = 0;
                            ret.add(fakeItemStack);
                        } else {
                            ret.add(stackInSlot);
                        }
                    }
                }
            }
        }

        if (mSteamOutputs != null && !mSteamOutputs.isEmpty()) {
            for (final MTEHatch tBus : validMTEList(mSteamOutputs)) {
                if (!(tBus instanceof MTEHatchOutputBusME)) {
                    final IInventory tBusInv = tBus.getBaseMetaTileEntity();
                    for (int i = 0; i < tBusInv.getSizeInventory(); i++) {
                        final ItemStack stackInSlot = tBus.getStackInSlot(i);
                        if (stackInSlot == null && tBus instanceof IItemLockable lockable && lockable.isLocked()) {
                            assert lockable.getLockedItem() != null;
                            ItemStack fakeItemStack = lockable.getLockedItem()
                                .copy();
                            fakeItemStack.stackSize = 0;
                            ret.add(fakeItemStack);
                        } else {
                            ret.add(stackInSlot);
                        }
                    }
                }
            }
        }

        return ret;
    }

    public long getTotalSteamCapacityLong() {
        long aSteam = 0;
        for (MTEHatchCustomFluidBase tHatch : validMTEList(mSteamInputFluids)) {
            aSteam += tHatch.getRealCapacity();
        }
        for (CustomFluidHatch tHatch : validMTEList(mSteamBigInputFluids)) {
            aSteam += tHatch.getRealCapacity();
        }
        for (CustomFluidHatch tHatch : validMTEList(mSteamWirelessInputFluids)) {
            aSteam += tHatch.getRealCapacity();
        }
        return aSteam;
    }

    public int getTotalSteamStoredOfAnyType() {
        int aSteam = 0;
        for (FluidStack aFluid : this.getStoredSteamFluids()) {
            if (aFluid == null) continue;
            for (SteamTypes type : SteamTypes.VALUES) {
                if (aFluid.getFluid() == type.fluid) {
                    aSteam += aFluid.amount;
                }
            }
        }
        return aSteam;
    }

    @Override
    public ArrayList<FluidStack> getAllSteamStacks() {
        ArrayList<FluidStack> aFluids = new ArrayList<>();
        for (FluidStack aFluid : this.getStoredSteamFluids()) {
            if (aFluid != null) {
                for (SteamTypes type : SteamTypes.VALUES) {
                    if (aFluid.getFluid() == type.fluid) {
                        aFluids.add(aFluid);
                        break;
                    }
                }
            }
        }
        return aFluids;
    }

    @Override
    public ArrayList<FluidStack> getStoredFluids() {
        ArrayList<FluidStack> rList = new ArrayList<>();
        Map<Fluid, FluidStack> inputsFromME = new HashMap<>();
        for (MTEHatchInput tHatch : validMTEList(mInputHatches)) {
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

        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;
    }

    public long getLongTotalSteamStored() {
        long total = 0;
        for (FluidStack aFluid : getAllSteamStacks()) {
            total += aFluid.amount;
        }
        return total;
    }

    @Override
    public boolean addOutput(ItemStack aStack) {
        if (GTUtility.isStackInvalid(aStack)) return false;
        aStack = GTUtility.copy(aStack);
        boolean outputSuccess = true;
        List<MTEHatchOutputBus> filteredBuses = filterValidMTEs(mOutputBusses);
        if (dumpItemBoolean(filteredBuses, aStack, true)) return true;
        if (dumpItemBoolean(filteredBuses, aStack, false)) return true;
        List<MTEHatchSteamBusOutput> filteredSteamBusses = filterValidMTEs(mSteamOutputs);
        if (dumpItemBoolean(filteredSteamBusses, aStack, true)) return true;
        if (dumpItemBoolean(filteredSteamBusses, aStack, false)) return true;

        while (outputSuccess && aStack.stackSize > 0) {
            outputSuccess = false;
            ItemStack single = aStack.splitStack(1);
            for (MTEHatchOutputBus tHatch : validMTEList(mSteamOutputs)) {
                if (!outputSuccess) {
                    if (tHatch.isLocked() && !GTUtility.areStacksEqual(tHatch.getLockedItem(), single)) {
                        continue;
                    }

                    for (int i = tHatch.getSizeInventory() - 1; i >= 0 && !outputSuccess; i--) {
                        if (tHatch.getBaseMetaTileEntity()
                            .addStackToSlot(i, single)) {
                            aStack.stackSize--;
                            outputSuccess = true;
                        }
                    }
                }
            }

            for (MTEHatchOutputBus tHatch : validMTEList(mOutputBusses)) {
                if (!outputSuccess) {
                    if (tHatch.isLocked() && !GTUtility.areStacksEqual(tHatch.getLockedItem(), single)) {
                        continue;
                    }
                    for (int i = tHatch.getSizeInventory() - 1; i >= 0 && !outputSuccess; i--) {
                        if (tHatch.getBaseMetaTileEntity()
                            .addStackToSlot(i, single)) {
                            aStack.stackSize--;
                            outputSuccess = true;
                        }
                    }
                }
            }
            for (MTEHatchOutput tHatch : validMTEList(mOutputHatches)) {
                if (!outputSuccess && tHatch.outputsItems()) {
                    if (tHatch.getBaseMetaTileEntity()
                        .addStackToSlot(1, single)) outputSuccess = true;
                }
            }
        }
        return outputSuccess;
    }

    public ItemStack tryAddOutput(ItemStack aStack) {
        if (GTUtility.isStackInvalid(aStack)) return aStack;
        aStack = GTUtility.copyOrNull(aStack);
        ItemStack itemStack = aStack.copy();

        List<MTEHatchOutputBus> filteredBusses = filterValidMTEs(mOutputBusses);
        aStack = tryDumpItem(filteredBusses, aStack, true, false);
        if (aStack == null || aStack.stackSize <= 0) return new ItemStack(Items.feather, 0);
        if (aStack.stackSize != itemStack.stackSize) return aStack;

        aStack = tryDumpItem(filteredBusses, aStack, false, false);
        if (aStack == null || aStack.stackSize <= 0) return new ItemStack(Items.feather, 0);
        if (aStack.stackSize != itemStack.stackSize) return aStack;

        List<MTEHatchSteamBusOutput> filteredSteamBusses = filterValidMTEs(mSteamOutputs);
        aStack = tryDumpItem(filteredSteamBusses, aStack, true, false);
        if (aStack == null || aStack.stackSize <= 0) return new ItemStack(Items.feather, 0);
        if (aStack.stackSize != itemStack.stackSize) return aStack;

        aStack = tryDumpItem(filteredSteamBusses, aStack, false, false);
        if (aStack == null || aStack.stackSize <= 0) return new ItemStack(Items.feather, 0);
        if (aStack.stackSize != itemStack.stackSize) return aStack;

        boolean outputSuccess = true;
        while (outputSuccess && aStack.stackSize > 0) {
            outputSuccess = false;
            ItemStack single = aStack.copy();
            single.stackSize = 1;

            for (MTEHatchOutputBus tHatch : filterValidMTEs(mSteamOutputs)) {
                if (!outputSuccess) {
                    if (tHatch.isLocked() && !GTUtility.areStacksEqual(tHatch.getLockedItem(), single)) {
                        continue;
                    }
                    for (int i = tHatch.getSizeInventory() - 1; i >= 0 && !outputSuccess; i--) {
                        if (tHatch.getBaseMetaTileEntity()
                            .addStackToSlot(i, single)) {
                            aStack.stackSize--;
                            outputSuccess = true;
                        }
                    }
                }
            }

            for (MTEHatchOutputBus tHatch : filterValidMTEs(mOutputBusses)) {
                if (!outputSuccess) {
                    if (tHatch.isLocked() && !GTUtility.areStacksEqual(tHatch.getLockedItem(), single)) {
                        continue;
                    }
                    for (int i = tHatch.getSizeInventory() - 1; i >= 0 && !outputSuccess; i--) {
                        if (tHatch.getBaseMetaTileEntity()
                            .addStackToSlot(i, single)) {
                            aStack.stackSize--;
                            outputSuccess = true;
                        }
                    }
                }
            }

            for (MTEHatchOutput tHatch : filterValidMTEs(mOutputHatches)) {
                if (!outputSuccess && tHatch.outputsItems()) {
                    if (tHatch.getBaseMetaTileEntity()
                        .addStackToSlot(1, single)) {
                        aStack.stackSize--;
                        outputSuccess = true;
                    }
                }
            }
        }

        return aStack.stackSize > 0 ? aStack : null;
    }

    public ItemStack tryDumpItem(List<? extends MTEHatchOutputBus> outputBuses, ItemStack itemStack,
        boolean restrictiveBusesOnly, boolean simulate) {
        if (GTUtility.isStackInvalid(itemStack)) return itemStack;

        for (MTEHatchOutputBus outputBus : outputBuses) {
            if (itemStack.stackSize <= 0) return null;

            if (outputBus instanceof MTEHatchOutputBusME) {
                itemStack = dumpItemME((MTEHatchOutputBusME) outputBus, itemStack, restrictiveBusesOnly, simulate);
            } else {
                if (restrictiveBusesOnly && !outputBus.isLocked()) {
                    continue;
                }

                if (outputBus.storePartial(itemStack, simulate)) {
                    return null;
                }
            }
        }

        return itemStack;
    }

    public ItemStack dumpItemME(MTEHatchOutputBusME outputBus, ItemStack itemStack, boolean restrictiveBusesOnly,
        boolean simulate) {
        if (GTUtility.isStackInvalid(itemStack)) return itemStack;

        if (restrictiveBusesOnly && !outputBus.isLocked()) {
            return itemStack;
        }

        if (outputBus.storePartial(itemStack, simulate)) {
            return null;
        }

        return itemStack;
    }

    public boolean dumpItemBoolean(List<? extends MTEHatchOutputBus> outputBuses, ItemStack itemStack,
        boolean restrictiveBusesOnly) {
        return dumpItemBoolean(outputBuses, itemStack, restrictiveBusesOnly, false);
    }

    public boolean dumpItemBoolean(List<? extends MTEHatchOutputBus> outputBuses, ItemStack itemStack,
        boolean restrictiveBusesOnly, boolean simulate) {
        for (MTEHatchOutputBus outputBus : outputBuses) {
            if (restrictiveBusesOnly && !outputBus.isLocked()) {
                continue;
            }

            if (outputBus.storePartial(itemStack, simulate)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void updateSlots() {
        for (CustomFluidHatch tHatch : validMTEList(mSteamBigInputFluids)) tHatch.updateSlots();
        for (CustomFluidHatch tHatch : validMTEList(mSteamWirelessInputFluids)) tHatch.updateSlots();
        for (MTEHatchInput tHatch : validMTEList(mInputHatches)) tHatch.updateSlots();
        for (MTEHatchInputBus tHatch : validMTEList(mInputBusses)) tHatch.updateSlots();
        super.updateSlots();
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mInputHatches.clear();
        mSteamInputFluids.clear();
        mSteamBigInputFluids.clear();
        mSteamWirelessInputFluids.clear();
        mSteamInputs.clear();
        mSteamOutputs.clear();
        tierAdvancedCasing = -1;
        tierBrickCasing = -1;
        tierPlatedCasing = -1;
        tierPipeCasing = -1;
        tierFireboxCasing = -1;
        tierMaterialBlock = -1;
        tierGearCasing = -1;
        tierFrameCasing = -1;
        tierIndustrialCasing = -1;
        tierMachineFrame = -1;
        tierMachineCasing = -1;
        tierMachine = -1;
        mCountCasing = 0;
    }

    @Override
    public void validateStructure(Collection<StructureError> errors, NBTTagCompound context) {
        super.validateStructure(errors, context);

        if (mSteamInputFluids.isEmpty() && mSteamBigInputFluids.isEmpty() && mSteamWirelessInputFluids.isEmpty()) {
            errors.add(StructureError.MISSING_STEAM_HATCH);
        } else {
            errors.remove(StructureError.MISSING_STEAM_HATCH);
        }
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (lEUt < 0) {
            long aSteamVal = ((-lEUt * 10000) / Math.max(1000, mEfficiency));
            if (!tryConsumeSteam((int) aSteamVal)) {
                stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean tryConsumeSteam(int aAmount) {
        for (SteamTypes type : SteamTypes.getSupportedTypes()) {
            FluidStack steamStack = new FluidStack(type.fluid, Math.max(1, aAmount / type.efficiencyFactor));
            if (depleteInput(steamStack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder) {
        builder.widget(
            new DrawableWidget().setDrawable(ItemUtils.PICTURE_GTNL_STEAM_LOGO)
                .setSize(18, 18)
                .setPos(172, 67));
    }

    @Override
    @Nonnull
    public CheckRecipeResult doCheckRecipe() {
        CheckRecipeResult result = CheckRecipeResultRegistry.NO_RECIPE;

        // check crafting input hatches first
        for (IDualInputHatch dualInputHatch : mDualInputHatches) {
            ItemStack[] sharedItems = dualInputHatch.getSharedItems();
            for (var it = dualInputHatch.inventories(); it.hasNext();) {
                IDualInputInventory slot = it.next();

                if (!slot.isEmpty()) {
                    // try to cache the possible recipes from pattern
                    if (slot instanceof IDualInputInventoryWithPattern withPattern) {
                        if (!processingLogic.tryCachePossibleRecipesFromPattern(withPattern)) {
                            // move on to next slots if it returns false, which means there is no possible recipes with
                            // given pattern.
                            continue;
                        }
                    }

                    processingLogic.setInputItems(ArrayUtils.addAll(sharedItems, slot.getItemInputs()));
                    processingLogic.setInputFluids(slot.getFluidInputs());

                    CheckRecipeResult foundResult = processingLogic.process();
                    if (foundResult.wasSuccessful()) {
                        return foundResult;
                    }
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) {
                        // Recipe failed in interesting way, so remember that and continue searching
                        result = foundResult;
                    }
                }
            }
        }

        result = checkRecipeForCustomHatches(result);
        if (result.wasSuccessful()) {
            return result;
        }

        // Use hatch colors if any; fallback to color 1 otherwise.
        short hatchColors = getHatchColors();
        boolean doColorChecking = hatchColors != 0;
        if (!doColorChecking) hatchColors = 0b1;

        for (byte color = 0; color < (doColorChecking ? 16 : 1); color++) {
            if (isColorAbsent(hatchColors, color)) continue;
            processingLogic.setInputFluids(getStoredFluidsForColor(Optional.of(color)));
            if (isInputSeparationEnabled()) {
                if (mInputBusses.isEmpty() && mSteamInputs.isEmpty()) {
                    CheckRecipeResult foundResult = processingLogic.process();
                    if (foundResult.wasSuccessful()) return foundResult;
                    // Recipe failed in interesting way, so remember that and continue searching
                    if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                } else {
                    for (MTEHatchInputBus bus : mInputBusses) {
                        if (bus instanceof MTEHatchCraftingInputME) continue;
                        byte busColor = bus.getColor();
                        if (busColor != -1 && busColor != color) continue;
                        List<ItemStack> inputItems = new ArrayList<>();
                        for (int i = bus.getSizeInventory() - 1; i >= 0; i--) {
                            ItemStack stored = bus.getStackInSlot(i);
                            if (stored != null) inputItems.add(stored);
                        }
                        if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                            inputItems.add(getControllerSlot());
                        }
                        processingLogic.setInputItems(inputItems);
                        CheckRecipeResult foundResult = processingLogic.process();
                        if (foundResult.wasSuccessful()) return foundResult;
                        // Recipe failed in interesting way, so remember that and continue searching
                        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                    }
                    for (MTEHatchSteamBusInput bus : mSteamInputs) {
                        byte busColor = bus.getColor();
                        if (busColor != -1 && busColor != color) continue;
                        List<ItemStack> inputItems = new ArrayList<>();
                        for (int i = bus.getSizeInventory() - 1; i >= 0; i--) {
                            ItemStack stored = bus.getStackInSlot(i);
                            if (stored != null) inputItems.add(stored);
                        }
                        if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                            inputItems.add(getControllerSlot());
                        }
                        processingLogic.setInputItems(inputItems);
                        CheckRecipeResult foundResult = processingLogic.process();
                        if (foundResult.wasSuccessful()) return foundResult;
                        // Recipe failed in interesting way, so remember that and continue searching
                        if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
                    }
                }
            } else {
                List<ItemStack> inputItems = getStoredInputsForColor(Optional.of(color));
                if (canUseControllerSlotForRecipe() && getControllerSlot() != null) {
                    inputItems.add(getControllerSlot());
                }
                processingLogic.setInputItems(inputItems);
                CheckRecipeResult foundResult = processingLogic.process();
                if (foundResult.wasSuccessful()) return foundResult;
                // Recipe failed in interesting way, so remember that
                if (foundResult != CheckRecipeResultRegistry.NO_RECIPE) result = foundResult;
            }
        }
        return result;
    }

    @Override
    public ArrayList<FluidStack> getStoredFluidsForColor(Optional<Byte> color) {
        ArrayList<FluidStack> rList = new ArrayList<>();
        Map<Fluid, FluidStack> inputsFromME = new HashMap<>();
        for (MTEHatchInput tHatch : validMTEList(mInputHatches)) {
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

        if (!inputsFromME.isEmpty()) {
            rList.addAll(inputsFromME.values());
        }
        return rList;
    }

    public short getHatchColors() {
        short hatchColors = 0;

        for (var bus : mInputBusses) hatchColors |= (short) (1 << bus.getColor());
        for (var hatch : mInputHatches) hatchColors |= (short) (1 << hatch.getColor());

        for (var bus : mSteamInputs) hatchColors |= (short) (1 << bus.getColor());
        for (var hatch : mSteamInputFluids) hatchColors |= (short) (1 << hatch.getColor());

        return hatchColors;
    }

    public static boolean isColorAbsent(short hatchColors, byte color) {
        return (hatchColors & (1 << color)) == 0;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        if (doesBindPlayerInventory()) {
            builder.widget(
                new DrawableWidget().setDrawable(GTUITextures.PICTURE_SCREEN_BLACK)
                    .setPos(4, 4)
                    .setSize(190, 85));
            final SlotWidget inventorySlot = new SlotWidget(inventoryHandler, 1);
            builder.widget(
                inventorySlot.setPos(173, 167)
                    .setBackground(GTUITextures.SLOT_DARK_GRAY));

            final DynamicPositionedColumn screenElements = new DynamicPositionedColumn();
            drawTexts(screenElements, inventorySlot);
            builder.widget(
                new Scrollable().setVerticalScroll()
                    .widget(screenElements)
                    .setPos(10, 7)
                    .setSize(182, 79));

            if (supportsMachineModeSwitch() && machineModeIcons == null) {
                machineModeIcons = new ArrayList<>(4);
                setMachineModeIcons();
            }
            builder.widget(createPowerSwitchButton(builder))
                .widget(createVoidExcessButton(builder))
                .widget(createInputSeparationButton(builder))
                .widget(createModeSwitchButton(builder))
                .widget(createBatchModeButton(builder))
                .widget(createLockToSingleRecipeButton(builder))
                .widget(createStructureUpdateButton(builder))
                .widget(createMuffleButton(builder));
            if (supportsPowerPanel()) {
                builder.widget(createPowerPanelButton(builder));
                buildContext.addSyncedWindow(POWER_PANEL_WINDOW_ID, this::createPowerPanel);
            }
        } else {
            addNoPlayerInventoryUI(builder, buildContext);
        }

        buildContext.addSyncedWindow(OC_WINDOW_ID, this::createRecipeOcCountWindow);
        builder.widget(new FakeSyncWidget.LongSyncer(this::getTotalSteamCapacityLong, val -> uiSteamCapacity = val));
        builder.widget(new FakeSyncWidget.LongSyncer(this::getLongTotalSteamStored, val -> uiSteamStored = val));
        builder.widget(
            new FakeSyncWidget.IntegerSyncer(this::getTotalSteamStoredOfAnyType, val -> uiSteamStoredOfAllTypes = val));

        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            if (!widget.isClient()) {
                widget.getContext()
                    .openSyncedWindow(OC_WINDOW_ID);
            }
        })
            .setPlayClickSound(true)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(GTUITextures.BUTTON_STANDARD);
                ret.add(GTUITextures.OVERLAY_BUTTON_BATCH_MODE_ON);
                return ret.toArray(new IDrawable[0]);
            })
            .addTooltip(translateToLocal("Info_SteamMachine_00"))
            .setTooltipShowUpDelay(TOOLTIP_DELAY)
            .setPos(174, 112)
            .setSize(16, 16));
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
    public ButtonWidget createMuffleButton(IWidgetBuilder<?> builder) {
        return (ButtonWidget) new ButtonWidget().setOnClick((clickData, widget) -> setMuffled(!isMuffled()))
            .setPlayClickSound(true)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                if (isMuffled()) {
                    ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                    ret.add(GTUITextures.OVERLAY_BUTTON_MUFFLE_ON);
                } else {
                    ret.add(GTUITextures.BUTTON_STANDARD);
                    ret.add(GTUITextures.OVERLAY_BUTTON_MUFFLE_OFF);
                }
                return ret.toArray(new IDrawable[0]);
            })
            .attachSyncer(new FakeSyncWidget.BooleanSyncer(this::isMuffled, this::setMuffled), builder)
            .addTooltip(StatCollector.translateToLocal("GT5U.machines.muffled"))
            .setPos(200, 0)
            .setSize(12, 12);
    }

    public ModularWindow createRecipeOcCountWindow(EntityPlayer player) {
        final int WIDTH = 158;
        final int HEIGHT = 52;
        final int PARENT_WIDTH = getGUIWidth();
        final int PARENT_HEIGHT = getGUIHeight();
        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);
        builder.setBackground(GTUITextures.BACKGROUND_SINGLEBLOCK_DEFAULT);
        builder.setGuiTint(getGUIColorization());
        builder.setDraggable(true);
        builder.setPos(
            (size, window) -> Alignment.Center.getAlignedPos(size, new Size(PARENT_WIDTH, PARENT_HEIGHT))
                .add(
                    Alignment.BottomRight.getAlignedPos(new Size(PARENT_WIDTH, PARENT_HEIGHT), new Size(WIDTH, HEIGHT))
                        .add(WIDTH - 3, 0)
                        .subtract(0, 10)));
        builder.widget(
            TextWidget.localised("Info_SteamMachine_00")
                .setPos(3, 4)
                .setSize(150, 20))
            .widget(
                new NumericWidget().setSetter(val -> recipeOcCount = clampRecipeOcCount((int) val))
                    .setGetter(() -> clampRecipeOcCount(recipeOcCount))
                    .setBounds(0, Integer.MAX_VALUE)
                    .setDefaultValue(0)
                    .setScrollValues(1, 4, 64)
                    .setTextAlignment(Alignment.Center)
                    .setTextColor(Color.WHITE.normal)
                    .setSize(150, 18)
                    .setPos(4, 25)
                    .setBackground(GTUITextures.BACKGROUND_TEXT_FIELD)
                    .attachSyncer(
                        new FakeSyncWidget.IntegerSyncer(
                            () -> clampRecipeOcCount(recipeOcCount),
                            (val) -> recipeOcCount = clampRecipeOcCount(val)),
                        builder));
        return builder.build();
    }

    public int clampRecipeOcCount(int value) {
        return Math.min(4, value);
    }

    public static <T extends SteamMultiMachineBase<T>> HatchElementBuilder<T> buildSteamBigInput(Class<T> typeToken) {
        return buildHatchAdder(typeToken).adder(SteamMultiMachineBase::addToMachineList)
            .hatchIds(BIG_STEAM_INPUT_HATCH.ID)
            .shouldReject(t -> !t.mSteamBigInputFluids.isEmpty());
    }

    public static <T extends SteamMultiMachineBase<T>> HatchElementBuilder<T> buildSteamWirelessInput(
        Class<T> typeToken) {
        return buildHatchAdder(typeToken).adder(SteamMultiMachineBase::addToMachineList)
            .hatchIds(PIPELESS_STEAM_HATCH.ID)
            .shouldReject(t -> !t.mSteamWirelessInputFluids.isEmpty());
    }
}
