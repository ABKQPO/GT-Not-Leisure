package com.science.gtnl.mixins.late.Gregtech;

import static gregtech.api.util.GTUtility.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.machine.VMTweakHelper;
import com.science.gtnl.utils.recipes.GTNL_OverclockCalculator;

import bwcrossmod.galacticgreg.MTEVoidMinerBase;
import gregtech.api.enums.GTValues;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gtneioreplugin.plugin.block.ModBlocks;
import gtneioreplugin.plugin.item.ItemDimensionDisplay;

@Mixin(value = MTEVoidMinerBase.class, remap = false)
public abstract class MixinMTEVoidMinerBase extends MTEEnhancedMultiBlockBase<MixinMTEVoidMinerBase> {

    @Shadow
    @Final
    protected byte TIER_MULTIPLIER;
    @Unique
    public long lEUt;

    @Shadow
    protected abstract int getMinTier();

    @Shadow
    protected abstract ItemStack nextOre();

    @Shadow
    private boolean mBlacklist;
    @Shadow
    private int multiplier;

    @Unique
    private static final boolean GTNL$enableMixin = !ModList.VMTweak.isModLoaded() && MainConfig.enableVoidMinerTweak;

    public MixinMTEVoidMinerBase(String aName) {
        super(aName);
    }

    @ModifyVariable(method = "handleExtraDrops", at = @At("HEAD"), require = 1, remap = false, argsOnly = true)
    private int vmTweak$mapDimensionIdForExtraDrops(int id) {
        if (!GTNL$enableMixin) return id;
        return VMTweakHelper.dimMapping.inverse()
            .getOrDefault(vmTweak$resolveDimensionKey(), id);
    }

    @ModifyVariable(method = "handleModDimDef", at = @At("HEAD"), require = 1, remap = false, argsOnly = true)
    private int vmTweak$mapDimensionIdForModDef(int id) {
        if (!GTNL$enableMixin) return id;
        return vmTweak$dim = VMTweakHelper.dimMapping.inverse()
            .getOrDefault(vmTweak$resolveDimensionKey(), id);
    }

    @ModifyVariable(method = "handleModDimDef", at = @At("STORE"), require = 1, remap = false)
    private String vmTweak$mapDimensionChunkProviderName(String id) {
        if (!GTNL$enableMixin) return id;
        return VMTweakHelper.cache.getOrDefault(vmTweak$dim, id);
    }

    @Unique
    private int vmTweak$dim;

    @Unique
    private String vmTweak$resolveDimensionKey() {
        if (!GTNL$enableMixin) return "None";
        return Optional.ofNullable(this.mInventory[1])
            .filter(s -> s.getItem() instanceof ItemDimensionDisplay)
            .map(ItemDimensionDisplay::getDimension)
            .orElse("None");
    }

    @Unique
    private String vmTweak$mLastDimensionOverride = "None";

    @Inject(method = "saveNBTData", at = @At("HEAD"), require = 1, remap = false)
    public void vmTweak$saveNBT(NBTTagCompound aNBT, CallbackInfo c) {
        if (!GTNL$enableMixin) return;
        aNBT.setString("mLastDimensionOverride", this.vmTweak$mLastDimensionOverride);
    }

    @Inject(method = "loadNBTData", at = @At("HEAD"), require = 1, remap = false)
    public void vmTweak$loadNBT(NBTTagCompound aNBT, CallbackInfo c) {
        if (!GTNL$enableMixin) return;
        this.vmTweak$mLastDimensionOverride = aNBT.getString("mLastDimensionOverride");
    }

    @Inject(method = "working", at = @At("HEAD"), remap = false)
    public void vmTweak$onWorkingTick(CallbackInfoReturnable<Boolean> cir) {
        if (!GTNL$enableMixin) return;
        String dim = Optional.ofNullable(this.mInventory[1])
            .filter(s -> s.getItem() instanceof ItemDimensionDisplay)
            .map(ItemDimensionDisplay::getDimension)
            .orElse("None");

        if (!Objects.equals(dim, vmTweak$mLastDimensionOverride)) {
            vmTweak$mLastDimensionOverride = dim;
            totalWeight = 0;
        }
    }

    @Unique
    private String vmTweak$getDimensionDisplayName() {
        if (!GTNL$enableMixin) return "";
        String ext = null;
        try {
            Block block = ModBlocks.getBlock(vmTweak$mLastDimensionOverride);
            ext = new ItemStack(block).getDisplayName();
        } catch (Exception ignored) {}

        return EnumChatFormatting.YELLOW + StatCollector.translateToLocal("Info_Dimension_Override")
            + (ext == null ? vmTweak$mLastDimensionOverride : ext);
    }

    @Override
    public long getMaxInputVoltage() {
        long rVoltage = 0;
        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) rVoltage += tHatch.getBaseMetaTileEntity()
            .getInputVoltage();
        for (MTEHatch tHatch : validMTEList(mExoticEnergyHatches)) rVoltage += tHatch.getBaseMetaTileEntity()
            .getInputVoltage();
        return rVoltage;
    }

    @Override
    public long getMaxInputEu() {
        long exoticEu = ExoticEnergyInputHelper.getTotalEuMulti(mExoticEnergyHatches);
        long normalEu = ExoticEnergyInputHelper.getTotalEuMulti(mEnergyHatches);
        return Math.max(exoticEu, normalEu);
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (GTNL$enableMixin) {
            if (this.lEUt > 0) {
                addEnergyOutput((this.lEUt * mEfficiency) / 10000);
                return true;
            }
            if (this.lEUt < 0) {
                if (!drainEnergyInput(getActualEnergyUsage())) {
                    stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                    return false;
                }
            }
        } else {
            if (mEUt > 0) {
                addEnergyOutput(((long) mEUt * mEfficiency) / 10000);
                return true;
            }
            if (mEUt < 0) {
                if (!drainEnergyInput(getActualEnergyUsage())) {
                    stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (!GTNL$enableMixin) return;
        this.lEUt = aNBT.getLong("mEUt");
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (!GTNL$enableMixin) return;
        aNBT.setLong("mEUt", this.lEUt);
    }

    @Override
    public boolean drainEnergyInput(long aEU) {
        if (aEU <= 0) return true;

        for (MTEHatchEnergy tHatch : validMTEList(mEnergyHatches)) {
            long tDrain = Math.min(
                tHatch.getBaseMetaTileEntity()
                    .getStoredEU(),
                aEU);
            tHatch.getBaseMetaTileEntity()
                .decreaseStoredEnergyUnits(tDrain, false);
            aEU -= tDrain;

            if (aEU <= 0) return true;
        }
        for (MTEHatch tHatch : validMTEList(mExoticEnergyHatches)) {
            long tDrain = Math.min(
                tHatch.getBaseMetaTileEntity()
                    .getStoredEU(),
                aEU);
            tHatch.getBaseMetaTileEntity()
                .decreaseStoredEnergyUnits(tDrain, false);
            aEU -= tDrain;

            if (aEU <= 0) return true;
        }

        return false;
    }

    @Redirect(method = "checkHatches", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;isEmpty()Z"))
    private boolean redirectEnergyHatches(ArrayList<?> list) {
        return mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty();
    }

    @Inject(method = "setElectricityStats", at = @At("HEAD"), cancellable = true)
    private void injectSetElectricityStats(CallbackInfo ci) {
        if (!GTNL$enableMixin) return;
        this.lEUt = -Math.abs(Math.toIntExact(GTValues.V[this.getMinTier()]));
        long useEU = getMaxInputEu();

        if (batchMode) {
            this.mMaxProgresstime = 128;
        } else {
            GTNL_OverclockCalculator calculator = new GTNL_OverclockCalculator().setEUt(useEU)
                .setRecipeEUt(-lEUt)
                .setDuration(10)
                .setParallel(1);
            if (TIER_MULTIPLIER == 3) calculator.enablePerfectOC();

            calculator.calculate();
            this.mMaxProgresstime = calculator.getDuration();
        }

        this.mOutputItems = GTValues.emptyItemStackArray;
        this.mProgresstime = 0;
        this.mEfficiency = this.getCurrentEfficiency(null);
        this.mEfficiencyIncrease = 10000;
        this.lEUt = useEU > 0 ? -useEU : useEU;
        ci.cancel();
    }

    @Inject(method = "handleOutputs", at = @At("HEAD"), cancellable = true)
    private void injectHandleOutputs(CallbackInfo ci) {
        if (!GTNL$enableMixin) return;

        List<ItemStack> inputOres = this.getStoredInputs()
            .stream()
            .filter(GTUtility::isOre)
            .collect(Collectors.toList());
        ItemStack output = this.nextOre();

        GTNL_OverclockCalculator calculator = new GTNL_OverclockCalculator().setEUt(getMaxInputEu())
            .setRecipeEUt(Math.abs(Math.toIntExact(GTValues.V[this.getMinTier()])))
            .setDuration(10 * (batchMode ? 16 : 1))
            .setParallel(1);
        if (TIER_MULTIPLIER == 3) calculator.enablePerfectOC();
        calculator.calculate();

        double parallel = calculator.calculateMultiplierUnderOneTick();
        if (batchMode) {
            double multiplierParallel = 128d / calculator.getDuration();
            parallel = (int) (parallel * multiplierParallel);
        }

        long totalCount = (long) (multiplier * parallel);
        if (totalCount <= 0) {
            ci.cancel();
            return;
        }

        while (totalCount > 0) {
            int stackSize = (int) Math.min(Integer.MAX_VALUE, totalCount);
            ItemStack stackPart = output.copy();
            stackPart.stackSize = stackSize;

            if (inputOres.isEmpty() || (this.mBlacklist && inputOres.stream()
                .noneMatch(is -> GTUtility.areStacksEqual(is, output)))
                || (!this.mBlacklist && inputOres.stream()
                    .anyMatch(is -> GTUtility.areStacksEqual(is, output)))) {
                this.addOutput(stackPart);
            }

            totalCount -= stackSize;
        }

        this.updateSlots();
        ci.cancel();
    }

    @Override
    protected void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);
        if (!GTNL$enableMixin) return;
        screenElements.widget(
            TextWidget.dynamicString(this::vmTweak$getDimensionDisplayName)
                .setSynced(true)
                .setDefaultColor(EnumChatFormatting.YELLOW)
                .setTextAlignment(Alignment.CenterLeft)
                .setEnabled(true));
    }

    @Shadow
    private float totalWeight;
}
