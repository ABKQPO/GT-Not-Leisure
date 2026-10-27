package com.science.gtnl.mixins.late.gregtech;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.common.machine.voidminer.IVoidMinerHost;
import com.science.gtnl.common.machine.voidminer.VoidMinerHooks;
import com.science.gtnl.common.machine.voidminer.VoidMinerState;
import com.science.gtnl.compat.vmtweak.VoidMinerTweak;

import bwcrossmod.galacticgreg.MTEVoidMinerBase;
import galacticgreg.api.ModDimensionDef;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;

@Mixin(value = MTEVoidMinerBase.class, remap = false)
public abstract class MixinMTEVoidMinerBase extends MTEEnhancedMultiBlockBase<MixinMTEVoidMinerBase>
    implements IVoidMinerHost {

    @Shadow
    @Final
    protected byte TIER_MULTIPLIER;

    @Shadow
    protected abstract int getMinTier();

    @Shadow
    protected abstract ItemStack nextOre();

    @Shadow
    private ModDimensionDef dimensionDef;

    @Shadow
    private boolean canVoidMine;

    @Shadow
    private int multiplier;

    @Shadow
    private float totalWeight;

    @Unique
    private static boolean gtnl$enableMixin = VoidMinerTweak.enabled();

    @Unique
    public VoidMinerState gtnl$state = new VoidMinerState();

    public MixinMTEVoidMinerBase(String aName) {
        super(aName);
    }

    @Override
    public VoidMinerState gtnl$state() {
        return gtnl$state;
    }

    @Override
    public byte gtnl$tierMultiplier() {
        return TIER_MULTIPLIER;
    }

    @Override
    public int gtnl$minTier() {
        return getMinTier();
    }

    @Override
    public ItemStack gtnl$nextOre() {
        return nextOre();
    }

    @Override
    public ModDimensionDef gtnl$dimensionDef() {
        return dimensionDef;
    }

    @Override
    public void gtnl$setDimensionDef(ModDimensionDef dimensionDef) {
        this.dimensionDef = dimensionDef;
    }

    @Override
    public void gtnl$setCanVoidMine(boolean canVoidMine) {
        this.canVoidMine = canVoidMine;
    }

    @Override
    public int gtnl$multiplier() {
        return multiplier;
    }

    @Override
    public float gtnl$totalWeight() {
        return totalWeight;
    }

    @Override
    public void gtnl$setTotalWeight(float totalWeight) {
        this.totalWeight = totalWeight;
    }

    @Unique
    private MTEVoidMinerBase<?> gtnl$self() {
        return (MTEVoidMinerBase<?>) (Object) this;
    }

    @Inject(method = "saveNBTData", at = @At("HEAD"), require = 1, remap = false)
    public void vmTweak$saveNBT(NBTTagCompound aNBT, CallbackInfo c) {
        if (!gtnl$enableMixin) return;
        gtnl$state.writeDimensionToNBT(aNBT);
    }

    @Inject(method = "loadNBTData", at = @At("HEAD"), require = 1, remap = false)
    public void vmTweak$loadNBT(NBTTagCompound aNBT, CallbackInfo c) {
        if (!gtnl$enableMixin) return;
        gtnl$state.readDimensionFromNBT(aNBT);
    }

    @Inject(method = "onFirstTick", at = @At("HEAD"), require = 1, remap = false, cancellable = true)
    private void vmTweak$onFirstTick(IGregTechTileEntity aBaseMetaTileEntity, CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        ci.cancel();
        super.onFirstTick(aBaseMetaTileEntity);
        VoidMinerHooks.recalculateDropMap(gtnl$self());
        VoidMinerHooks.resizeSelected(gtnl$self());
    }

    @Inject(method = "calculateDropMap", at = @At("HEAD"), require = 1, remap = false, cancellable = true)
    private void vmTweak$calculateDropMap(CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        VoidMinerHooks.recalculateDropMap(gtnl$self());
        ci.cancel();
    }

    @Inject(method = "checkProcessing", at = @At("HEAD"), require = 1, remap = false)
    private void vmTweak$refreshDimensionOverride(CallbackInfoReturnable<CheckRecipeResult> cir) {
        if (!gtnl$enableMixin) return;
        VoidMinerHooks.refreshDimensionOverride(gtnl$self());
    }

    @Inject(method = "getCopiedData", at = @At("HEAD"), require = 1, remap = false, cancellable = true)
    private void vmTweak$getCopiedData(EntityPlayer player, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (!gtnl$enableMixin) return;
        cir.setReturnValue(VoidMinerHooks.copiedData(gtnl$self()));
    }

    @Inject(method = "pasteCopiedData", at = @At("HEAD"), require = 1, remap = false, cancellable = true)
    private void vmTweak$pasteCopiedData(EntityPlayer player, NBTTagCompound nbt, CallbackInfoReturnable<Boolean> cir) {
        if (!gtnl$enableMixin) return;
        cir.setReturnValue(VoidMinerHooks.pasteCopiedData(gtnl$self(), nbt));
    }

    @Override
    public long getMaxInputVoltage() {
        if (!gtnl$enableMixin) return super.getMaxInputVoltage();
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
        long exoticEu = ExoticEnergyInputHelper.getTotalEuMulti(mExoticEnergyHatches);
        long normalEu = ExoticEnergyInputHelper.getTotalEuMulti(mEnergyHatches);
        return Math.max(exoticEu, normalEu);
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (!gtnl$enableMixin) return super.onRunningTick(aStack);
        if (gtnl$state.getEUt() > 0) {
            addEnergyOutput((gtnl$state.getEUt() * mEfficiency) / 10000);
            return true;
        }
        if (gtnl$state.getEUt() < 0) {
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
        return (-gtnl$state.getEUt() * 10_000) / Math.max(1000, mEfficiency);
    }

    @Inject(method = "loadNBTData", at = @At("TAIL"))
    private void gtnl$injectLoadNBT(NBTTagCompound aNBT, CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        gtnl$state.setEUt(aNBT.getLong("mEUt"));
    }

    @Inject(method = "saveNBTData", at = @At("TAIL"))
    private void gtnl$injectSaveNBT(NBTTagCompound aNBT, CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        aNBT.setLong("mEUt", gtnl$state.getEUt());
    }

    @Override
    public boolean drainEnergyInput(long aEU) {
        if (!gtnl$enableMixin) return super.drainEnergyInput(aEU);
        return VoidMinerHooks.drainEnergyInput(aEU, mEnergyHatches, mExoticEnergyHatches);
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

    @Inject(method = "checkHatches", at = @At("HEAD"), cancellable = true)
    private void gtnl$checkHatches(List<StructureError> errors, CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        checkOneMaintenanceHatch(errors);
        checkHasOutputBus(errors);
        checkHasAnyEnergy(errors);
        ci.cancel();
    }

    @Inject(method = "setElectricityStats", at = @At("HEAD"), cancellable = true)
    private void injectSetElectricityStats(CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        VoidMinerHooks.setElectricityStats(gtnl$self(), batchMode);
        ci.cancel();
    }

    @Inject(method = "handleOutputs", at = @At("HEAD"), cancellable = true)
    private void injectHandleOutputs(CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        VoidMinerHooks.handleOutputs(gtnl$self(), batchMode);
        ci.cancel();
    }

    @Deprecated
    @Inject(method = "drawTexts", at = @At("TAIL"), require = 1, remap = false)
    private void vmTweak$drawDimensionOverride(DynamicPositionedColumn screenElements, SlotWidget inventorySlot,
        CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        screenElements.addChild(
            TextWidget.dynamicText(this::vmTweak$getDimensionDisplayName)
                .setSynced(true)
                .setDefaultColor(EnumChatFormatting.YELLOW)
                .setTextAlignment(Alignment.TopLeft)
                .setEnabled(true));
    }

    @Deprecated
    @Unique
    private Text vmTweak$getDimensionDisplayName() {
        if (!gtnl$enableMixin) return Text.EMPTY;
        return VoidMinerHooks.dimensionDisplayName(gtnl$state);
    }

    @Override
    public String getGtnl$overrideDisplayText() {
        if (!gtnl$enableMixin || !gtnl$state.hasOverride()) return "";
        if (!gtnl$state.getWarning()
            .isEmpty()) {
            return "!" + gtnl$state.getWarning();
        }
        return gtnl$state.getLastDimensionOverride();
    }

    @Override
    public String getGtnl$warning() {
        if (!gtnl$enableMixin) return "";
        return gtnl$state.getWarning();
    }

    @Override
    public int getGtnl$dimensionChangeVersion() {
        if (!gtnl$enableMixin) return 0;
        return gtnl$state.getDimensionChangeVersion();
    }
}
