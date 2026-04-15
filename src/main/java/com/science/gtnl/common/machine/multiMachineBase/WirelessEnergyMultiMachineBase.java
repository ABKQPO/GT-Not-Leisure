package com.science.gtnl.common.machine.multiMachineBase;

import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;

import com.science.gtnl.api.IWirelessEnergy;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;
import com.science.gtnl.utils.recipes.GTNLProcessingLogic;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public abstract class WirelessEnergyMultiMachineBase<T extends WirelessEnergyMultiMachineBase<T>>
    extends MultiMachineBase<T> implements IWirelessEnergy {

    public int totalOverclockedDuration = 0;
    public int maxParallelStored = -1;

    public WirelessEnergyMultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public WirelessEnergyMultiMachineBase(String aName) {
        super(aName);
    }

    public UUID ownerUUID;
    public boolean isRecipeProcessing = false;
    @Getter
    public boolean wirelessMode = getDefaultWirelessMode();
    @Getter
    @Setter
    public boolean wirelessUpgrade = false;
    public BigInteger costingEU = BigInteger.ZERO;
    public String costingEUText = Utils.ZERO_STRING;
    public int cycleNum = 100_000;
    public int cycleNow = 0;

    @Override
    public void setWirelessMode(boolean mode) {
        if (wirelessUpgrade) {
            wirelessMode = mode;
        } else {
            wirelessMode = false;
        }
    }

    @Override
    public void setItemNBT(NBTTagCompound aNBT) {
        if (wirelessUpgrade) aNBT.setBoolean("wirelessUpgrade", true);
        super.setItemNBT(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("wirelessUpgrade", wirelessUpgrade);
        aNBT.setBoolean("wirelessMode", wirelessMode);
        aNBT.setInteger("parallelTier", mParallelTier);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        wirelessUpgrade = aNBT.getBoolean("wirelessUpgrade");
        wirelessMode = aNBT.getBoolean("wirelessMode");
        mParallelTier = aNBT.getInteger("parallelTier");
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        wirelessMode = false;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        resetParallelTier();
        setWirelessMode(mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty());
    }

    @Override
    public void resetParallelTier() {
        super.resetParallelTier();
        mParallelTier = getParallelTier(getControllerSlot());
        for (ParallelControllerHatch module : GTUtility.filterValidMTEs(mParallelControllerHatches)) {
            mParallelTier = module.mTier;
            break;
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.getBoolean("wirelessUpgrade")) {
            currentTip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("Waila_WirelessUpgrade"));
        }
        if (tag.getBoolean("wirelessMode")) {
            currentTip.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            currentTip.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + tag.getString("costingEUText")
                    + EnumChatFormatting.RESET
                    + " EU");
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setBoolean("wirelessUpgrade", wirelessUpgrade);
            tag.setBoolean("wirelessMode", wirelessMode);
            if (wirelessMode) tag.setString("costingEUText", costingEUText);
        }
    }

    @Override
    public String[] getInfoData() {
        List<String> ret = new ArrayList<>(Arrays.asList(super.getInfoData()));
        if (wirelessMode) {
            ret.add(EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("Waila_WirelessMode"));
            ret.add(
                EnumChatFormatting.AQUA + StatCollector.translateToLocal("Waila_CurrentEuCost")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GOLD
                    + costingEUText
                    + EnumChatFormatting.RESET
                    + " EU");
        }
        return ret.toArray(new String[0]);
    }

    @Override
    public void startRecipeProcessing() {
        isRecipeProcessing = true;
        super.startRecipeProcessing();
    }

    @Override
    public void endRecipeProcessing() {
        super.endRecipeProcessing();
        isRecipeProcessing = false;
    }

    @Override
    public boolean getPerfectOC() {
        return true;
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
                return super.validateRecipe(recipe);
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
    public double getEUtDiscount() {
        return (wirelessUpgrade ? 0.4 : 0.6) - (mParallelTier / 50.0);
    }

    @Override
    public double getDurationModifier() {
        return 1.0 / (wirelessUpgrade ? 10.0 : 5.0) * Math.pow(0.75, mParallelTier);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        maxParallelStored = -1;
        resetParallelTier();
        costingEU = BigInteger.ZERO;
        costingEUText = Utils.ZERO_STRING;
        totalOverclockedDuration = 0;
        cycleNow = 0;
        if (!wirelessMode) return super.checkProcessing();

        maxParallelStored = getTrueParallel();

        boolean succeeded = false;
        CheckRecipeResult finalResult = CheckRecipeResultRegistry.SUCCESSFUL;
        for (cycleNow = 0; cycleNow < cycleNum; cycleNow++) {
            CheckRecipeResult r = wirelessModeProcessOnce(null);

            if (!r.wasSuccessful()) {
                finalResult = r;
                break;
            }
            succeeded = true;
            if (maxParallelStored <= -1) {
                finalResult = r;
                break;
            }
        }

        if (!succeeded) {
            maxParallelStored = -1;
            return finalResult;
        }
        updateSlots();
        costingEUText = GTUtility.formatNumbers(costingEU);

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = totalOverclockedDuration;
        maxParallelStored = -1;
        lEUt = 0;
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public CheckRecipeResult wirelessModeProcessOnce(ItemStack stack) {
        if (!isRecipeProcessing) startRecipeProcessing();
        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        if (!result.wasSuccessful()) {
            return result;
        }

        BigInteger costEU = BigInteger.valueOf(processingLogic.getCalculatedEut())
            .multiply(BigInteger.valueOf(processingLogic.getDuration()));

        if (!addEUToGlobalEnergyMap(ownerUUID, costEU.multiply(Utils.NEGATIVE_ONE))) {
            return CheckRecipeResultRegistry.insufficientPower(costEU.longValue());
        }

        costingEU = costingEU.add(costEU);

        mOutputItems = Utils.mergeArray(mOutputItems, processingLogic.getOutputItems());
        mOutputFluids = Utils.mergeArray(mOutputFluids, processingLogic.getOutputFluids());
        totalOverclockedDuration += processingLogic.getDuration();
        maxParallelStored = maxParallelStored - processingLogic.getCurrentParallels();

        endRecipeProcessing();
        return result;
    }

    @Override
    public void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
        logic.setUnlimitedTierSkips();
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        if (wirelessMode) {
            logic.setAvailableVoltage(GTValues.V[Math.min(mParallelTier + 1, 14)]);
            logic.setAvailableAmperage((8L << (2 * mParallelTier)) - 2L);
            logic.setAmperageOC(true);
            logic.enablePerfectOverclock();
        } else {
            logic.setAvailableVoltage(getMaxInputEu());
            logic.setAvailableAmperage(1);
            logic.setAmperageOC(true);
        }
    }

    @Override
    public long getMaxInputVoltage() {
        if (wirelessMode) return GTValues.V[Math.min(mParallelTier + 1, 14)];
        return super.getMaxInputVoltage();
    }

    @Override
    public int getMaxParallelRecipes() {
        if (wirelessMode && maxParallelStored >= 0) {
            return maxParallelStored;
        }
        mParallelTier = getParallelTier(getControllerSlot());

        for (ParallelControllerHatch module : GTUtility.filterValidMTEs(mParallelControllerHatches)) {
            mParallelTier = module.mTier;
            return module.getParallel() << 4;
        }
        if (mParallelTier <= 1) {
            return 8;
        } else {
            return 1 << (2 * (mParallelTier - 2) + 4);
        }
    }

    public boolean getDefaultWirelessMode() {
        return false;
    }

    @Override
    public void checkMaintenance() {}

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    @Override
    public boolean shouldCheckMaintenance() {
        return false;
    }
}
