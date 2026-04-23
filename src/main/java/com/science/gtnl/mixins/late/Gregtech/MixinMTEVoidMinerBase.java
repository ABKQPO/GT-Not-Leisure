package com.science.gtnl.mixins.late.Gregtech;

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
import net.minecraftforge.fluids.FluidStack;

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

import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.machine.VMTweakHelper;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;

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
    public long gtnl$lEUt;

    @Shadow
    protected abstract int getMinTier();

    @Shadow
    protected abstract ItemStack nextOre();

    @Shadow
    private boolean mBlacklist;
    @Shadow
    private int multiplier;
    @Shadow
    private float totalWeight;

    @Unique
    private static boolean gtnl$enableMixin = !ModList.VMTweak.isModLoaded() && MainConfig.machine.enableVoidMinerTweak;

    public MixinMTEVoidMinerBase(String aName) {
        super(aName);
    }

    @ModifyVariable(method = "handleExtraDrops", at = @At("HEAD"), require = 1, remap = false, argsOnly = true)
    private int vmTweak$mapDimensionIdForExtraDrops(int id) {
        if (!gtnl$enableMixin) return id;
        return VMTweakHelper.dimMapping.inverse()
            .getOrDefault(vmTweak$resolveDimensionKey(), id);
    }

    @ModifyVariable(method = "handleModDimDef", at = @At("HEAD"), require = 1, remap = false, argsOnly = true)
    private int vmTweak$mapDimensionIdForModDef(int id) {
        if (!gtnl$enableMixin) return id;
        return vmTweak$dim = VMTweakHelper.dimMapping.inverse()
            .getOrDefault(vmTweak$resolveDimensionKey(), id);
    }

    @ModifyVariable(method = "handleModDimDef", at = @At("STORE"), require = 1, remap = false)
    private String vmTweak$mapDimensionChunkProviderName(String id) {
        if (!gtnl$enableMixin) return id;
        return VMTweakHelper.cache.getOrDefault(vmTweak$dim, id);
    }

    @Unique
    private int vmTweak$dim;

    @Unique
    private String vmTweak$resolveDimensionKey() {
        if (!gtnl$enableMixin) return "None";
        return Optional.ofNullable(this.mInventory[1])
            .filter(s -> s.getItem() instanceof ItemDimensionDisplay)
            .map(ItemDimensionDisplay::getDimension)
            .orElse("None");
    }

    @Unique
    private String vmTweak$mLastDimensionOverride = "None";

    @Inject(method = "saveNBTData", at = @At("HEAD"), require = 1, remap = false)
    public void vmTweak$saveNBT(NBTTagCompound aNBT, CallbackInfo c) {
        if (!gtnl$enableMixin) return;
        aNBT.setString("mLastDimensionOverride", this.vmTweak$mLastDimensionOverride);
    }

    @Inject(method = "loadNBTData", at = @At("HEAD"), require = 1, remap = false)
    public void vmTweak$loadNBT(NBTTagCompound aNBT, CallbackInfo c) {
        if (!gtnl$enableMixin) return;
        this.vmTweak$mLastDimensionOverride = aNBT.getString("mLastDimensionOverride");
    }

    @Inject(method = "working", at = @At("HEAD"), remap = false)
    public void vmTweak$onWorkingTick(CallbackInfoReturnable<Boolean> cir) {
        if (!gtnl$enableMixin) return;
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
    private Text vmTweak$getDimensionDisplayName() {
        if (!gtnl$enableMixin) return Text.EMPTY;
        String ext = null;
        try {
            Block block = ModBlocks.getBlock(vmTweak$mLastDimensionOverride);
            ext = new ItemStack(block).getDisplayName();
        } catch (Exception ignored) {
            ScienceNotLeisure.LOG.debug(
                "[VMTweakMixin] Failed to get display name for dimension: {}",
                vmTweak$mLastDimensionOverride,
                ignored);
        }

        return new Text(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocal("Info_Dimension_Override")
                + (ext == null ? vmTweak$mLastDimensionOverride : ext));
    }

    @Override
    public long getMaxInputVoltage() {
        if (!gtnl$enableMixin) return super.getMaxInputVoltage();
        return gtnl$getMaxInputVoltage();
    }

    @Unique
    private long gtnl$getMaxInputVoltage() {
        long rVoltage = 0;
        for (MTEHatchEnergy h : GTUtility.validMTEList(mEnergyHatches)) rVoltage += h.getBaseMetaTileEntity()
            .getInputVoltage();
        for (MTEHatch h : GTUtility.validMTEList(mExoticEnergyHatches)) rVoltage += h.getBaseMetaTileEntity()
            .getInputVoltage();
        return rVoltage;
    }

    @Override
    public long getMaxInputEu() {
        if (!gtnl$enableMixin) return super.getMaxInputEu();
        return gtnl$getMaxInputEu();
    }

    @Unique
    public long gtnl$getMaxInputEu() {
        long exoticEu = ExoticEnergyInputHelper.getTotalEuMulti(mExoticEnergyHatches);
        long normalEu = ExoticEnergyInputHelper.getTotalEuMulti(mEnergyHatches);
        return Math.max(exoticEu, normalEu);
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (!gtnl$enableMixin) return super.onRunningTick(aStack);
        return gtnl$onRunningTick(aStack);
    }

    @Unique
    public boolean gtnl$onRunningTick(ItemStack aStack) {
        if (this.gtnl$lEUt > 0) {
            addEnergyOutput((this.gtnl$lEUt * mEfficiency) / 10000);
            return true;
        }
        if (this.gtnl$lEUt < 0) {
            if (!drainEnergyInput(getActualEnergyUsage())) {
                stopMachine(ShutDownReasonRegistry.POWER_LOSS);
                return false;
            }
        }
        return true;
    }

    @Override
    public long getActualEnergyUsage() {
        if (!gtnl$enableMixin) return super.getActualEnergyUsage();
        return gtnl$getActualEnergyUsage();
    }

    @Unique
    public long gtnl$getActualEnergyUsage() {
        return (-gtnl$lEUt * 10_000) / Math.max(1000, mEfficiency);
    }

    @Inject(method = "loadNBTData", at = @At("TAIL"))
    private void gtnl$injectLoadNBT(NBTTagCompound aNBT, CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        this.gtnl$lEUt = aNBT.getLong("mEUt");
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"))
    private void gtnl$injectSaveNBT(NBTTagCompound aNBT, CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        aNBT.setLong("mEUt", this.gtnl$lEUt);
    }

    @Override
    public boolean drainEnergyInput(long aEU) {
        if (!gtnl$enableMixin) return super.drainEnergyInput(aEU);
        return gtnl$drainEnergyInput(aEU);
    }

    @Unique
    public boolean gtnl$drainEnergyInput(long aEU) {
        if (aEU <= 0) return true;

        for (MTEHatchEnergy tHatch : GTUtility.validMTEList(mEnergyHatches)) {
            long tDrain = Math.min(
                tHatch.getBaseMetaTileEntity()
                    .getStoredEU(),
                aEU);
            tHatch.getBaseMetaTileEntity()
                .decreaseStoredEnergyUnits(tDrain, false);
            aEU -= tDrain;

            if (aEU <= 0) return true;
        }
        for (MTEHatch tHatch : GTUtility.validMTEList(mExoticEnergyHatches)) {
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

    @Inject(method = "consumeNobleGas", at = @At("HEAD"), remap = false, cancellable = true)
    public void consumeNobleGas(FluidStack gasToConsume, CallbackInfoReturnable<Boolean> cir) {
        if (!gtnl$enableMixin) return;
        for (FluidStack s : this.getStoredFluids()) {
            if (s.isFluidEqual(gasToConsume) && s.amount >= 20) {
                s.amount -= 20;
                this.updateSlots();
                cir.setReturnValue(true);
                return;
            }
        }
        cir.setReturnValue(false);
    }

    @Redirect(method = "checkHatches", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;isEmpty()Z"))
    private boolean redirectEnergyHatches(ArrayList<?> list) {
        return mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty();
    }

    @Inject(method = "setElectricityStats", at = @At("HEAD"), cancellable = true)
    private void injectSetElectricityStats(CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        this.gtnl$lEUt = -Math.abs(Math.toIntExact(GTValues.V[this.getMinTier()]));
        long useEU = getMaxInputEu();

        if (batchMode) {
            this.mMaxProgresstime = 128;
        } else {
            GTNLOverclockCalculator calculator = new GTNLOverclockCalculator().setEUt(useEU)
                .setRecipeEUt(-gtnl$lEUt)
                .setDuration(10)
                .setParallel(1);
            if (TIER_MULTIPLIER == 3) calculator = calculator.enablePerfectOC();

            calculator = calculator.calculate();
            this.mMaxProgresstime = calculator.getDuration();
        }

        this.mOutputItems = GTValues.emptyItemStackArray;
        this.mProgresstime = 0;
        this.mEfficiency = this.getCurrentEfficiency(null);
        this.mEfficiencyIncrease = 10000;
        this.gtnl$lEUt = useEU > 0 ? -useEU : useEU;
        ci.cancel();
    }

    @Inject(method = "handleOutputs", at = @At("HEAD"), cancellable = true)
    private void injectHandleOutputs(CallbackInfo ci) {
        if (!gtnl$enableMixin) return;

        List<ItemStack> inputOres = this.getStoredInputs()
            .stream()
            .filter(GTUtility::isOre)
            .collect(Collectors.toList());
        ItemStack output = this.nextOre();

        GTNLOverclockCalculator calculator = new GTNLOverclockCalculator().setEUt(getMaxInputEu())
            .setRecipeEUt(Math.abs(Math.toIntExact(GTValues.V[this.getMinTier()])))
            .setDuration(10 * (batchMode ? 16 : 1))
            .setParallel(1);
        if (TIER_MULTIPLIER == 3) calculator = calculator.enablePerfectOC();
        calculator = calculator.calculate();

        double parallel = calculator.calculateMultiplierUnderOneTick();
        if (batchMode) {
            double multiplierParallel = 128d / calculator.getDuration();
            parallel = (int) Math.max(1, parallel * multiplierParallel);
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
    public void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);
        if (!gtnl$enableMixin) return;
        screenElements.widget(
            TextWidget.dynamicText(this::vmTweak$getDimensionDisplayName)
                .setSynced(true)
                .setDefaultColor(EnumChatFormatting.YELLOW)
                .setTextAlignment(Alignment.CenterLeft)
                .setEnabled(true));
    }
}
