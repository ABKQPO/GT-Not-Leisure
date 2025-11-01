package com.science.gtnl.common.machine.multiMachineBase;

import static com.science.gtnl.utils.Utils.NEGATIVE_ONE;
import static com.science.gtnl.utils.Utils.mergeArray;
import static gregtech.api.enums.GTValues.V;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

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
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;
import com.science.gtnl.utils.recipes.GTNL_ProcessingLogic;

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

    public static final String ZERO_STRING = "0";

    public UUID ownerUUID;
    public boolean isRecipeProcessing = false;
    @Getter
    public boolean wirelessMode = getDefaultWirelessMode();
    @Getter
    @Setter
    public boolean wirelessUpgrade = false;
    public BigInteger costingEU = BigInteger.ZERO;
    public String costingEUText = ZERO_STRING;
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
        super.setItemNBT(aNBT);
        aNBT.setBoolean("wirelessUpgrade", wirelessUpgrade);
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
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if (mParallelControllerHatches.size() == 1 && aTick % 20 == 0) {
                for (ParallelControllerHatch module : mParallelControllerHatches) {
                    setMaxParallel(module.getParallel());
                    mParallelTier = module.mTier;
                }
            }
            if (mEfficiency < 0) mEfficiency = 0;
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        wirelessMode = false;
    }

    @Override
    public void setupParameters() {
        super.setupParameters();
        mParallelTier = getParallelTier(getControllerSlot());
        setWirelessMode(mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty());
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
        return new GTNL_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.mEUt > V[Math.min(mParallelTier + 1, 14)] * 4) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return super.validateRecipe(recipe);
            }

            @Nonnull
            @Override
            public GTNL_OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
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

    @Nonnull
    @Override
    public CheckRecipeResult checkProcessing() {
        maxParallelStored = -1;
        mParallelTier = 0;
        ItemStack controllerItem = getControllerSlot();
        int parallelTierItem = getParallelTier(controllerItem);
        mParallelTier = Math.max(mParallelTier, parallelTierItem);
        costingEU = BigInteger.ZERO;
        costingEUText = ZERO_STRING;
        totalOverclockedDuration = 0;
        cycleNow = 0;
        maxParallelStored = getTrueParallel();
        if (!wirelessMode) return super.checkProcessing();

        boolean succeeded = false;
        CheckRecipeResult finalResult = CheckRecipeResultRegistry.SUCCESSFUL;
        for (cycleNow = 0; cycleNow < cycleNum; cycleNow++) {
            CheckRecipeResult r = wirelessModeProcessOnce();

            if (!r.wasSuccessful()) {
                finalResult = r;
                break;
            }
            succeeded = true;
            if (maxParallelStored <= 0) {
                finalResult = r;
                break;
            }
        }

        if (!succeeded) return finalResult;
        updateSlots();
        if (totalOverclockedDuration > 0) {
            totalOverclockedDuration = (int) Math
                .max(1, totalOverclockedDuration * Math.pow(0.75, mParallelTier - 4) / (cycleNow + 1));
        } else {
            totalOverclockedDuration = 1;
        }
        costingEUText = GTUtility.formatNumbers(costingEU);

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = totalOverclockedDuration;

        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    public CheckRecipeResult wirelessModeProcessOnce() {
        if (!isRecipeProcessing) startRecipeProcessing();
        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        if (!result.wasSuccessful()) {
            return result;
        }

        BigInteger costEU = BigInteger.valueOf(processingLogic.getCalculatedEut())
            .multiply(BigInteger.valueOf(processingLogic.getDuration()));

        if (!addEUToGlobalEnergyMap(ownerUUID, costEU.multiply(NEGATIVE_ONE))) {
            return CheckRecipeResultRegistry.insufficientPower(costEU.longValue());
        }

        costingEU = costingEU.add(costEU);

        mOutputItems = mergeArray(mOutputItems, processingLogic.getOutputItems());
        mOutputFluids = mergeArray(mOutputFluids, processingLogic.getOutputFluids());
        totalOverclockedDuration += processingLogic.getDuration();
        maxParallelStored = maxParallelStored - processingLogic.getCurrentParallels();

        endRecipeProcessing();
        return result;
    }

    @Override
    public void setProcessingLogicPower(ProcessingLogic logic) {
        if (wirelessMode) {
            logic.setAvailableVoltage(V[Math.min(mParallelTier + 1, 14)]);
            logic.setAvailableAmperage((8L << (2 * mParallelTier)) - 2L);
            logic.setAmperageOC(false);
            logic.enablePerfectOverclock();
        } else {
            boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty()
                && getMaxInputAmps() <= 4;
            logic.setAvailableVoltage(getMachineVoltageLimit());
            logic.setAvailableAmperage(useSingleAmp ? 1 : getMaxInputAmps());
            logic.setAmperageOC(!useSingleAmp);
        }
    }

    @Override
    public int getMaxParallelRecipes() {
        if (maxParallelStored >= 0) {
            return maxParallelStored;
        }

        if (mParallelControllerHatches.size() == 1) {
            ParallelControllerHatch module = mParallelControllerHatches.get(0);
            mParallelTier = module.mTier;
            return module.getParallel() << 4;
        } else if (mParallelTier <= 1) {
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
