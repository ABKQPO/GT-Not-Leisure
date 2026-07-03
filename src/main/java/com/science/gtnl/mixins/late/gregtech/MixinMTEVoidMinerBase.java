package com.science.gtnl.mixins.late.gregtech;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import bwcrossmod.galacticgreg.VoidMinerUtility;
import galacticgreg.api.ModDimensionDef;
import galacticgreg.api.enums.DimensionDef;
import gregtech.api.enums.GTValues;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.ExoticEnergyInputHelper;
import gregtech.api.util.GTUtility;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gtneioreplugin.plugin.block.ModBlocks;
import gtneioreplugin.plugin.item.ItemDimensionDisplay;
import gtneioreplugin.util.DimensionHelper;

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
    public boolean blacklist;

    @Shadow
    private ModDimensionDef dimensionDef;

    @Shadow
    private boolean canVoidMine;

    @Shadow
    public VoidMinerUtility.DropMap dropMap;

    @Shadow
    public VoidMinerUtility.DropMap extraDropMap;

    @Shadow
    private int multiplier;
    @Shadow
    private float totalWeight;

    @Unique
    private static boolean gtnl$enableMixin = !ModList.VMTweak.isModLoaded() && MainConfig.machine.enableVoidMinerTweak;

    public MixinMTEVoidMinerBase(String aName) {
        super(aName);
    }

    @Unique
    private String vmTweak$resolveDimensionKey() {
        if (!gtnl$enableMixin) return "None";
        return Optional.ofNullable(this.mInventory[1])
            .filter(s -> s.getItem() instanceof ItemDimensionDisplay)
            .map(ItemDimensionDisplay::getDimension)
            .orElse("None");
    }

    @Unique
    private Optional<String> vmTweak$resolveDimensionNameOverride() {
        String dimensionKey = vmTweak$resolveDimensionKey();
        if ("None".equals(dimensionKey)) return Optional.empty();

        int displayIndex = DimensionHelper.ALL_DISPLAYED_NAMES.indexOf(dimensionKey);
        if (displayIndex >= 0) {
            return Optional.ofNullable(DimensionHelper.ALL_DIM_NAMES.get(displayIndex));
        }

        Integer dimensionId = VMTweakHelper.DIM_MAPPING.inverse()
            .get(dimensionKey);
        if (dimensionId != null) {
            return Optional.ofNullable(VMTweakHelper.getNameForID(dimensionId));
        }

        return Optional.of(dimensionKey);
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
        String dim = vmTweak$resolveDimensionKey();

        if (!Objects.equals(dim, vmTweak$mLastDimensionOverride)) {
            vmTweak$mLastDimensionOverride = dim;
            totalWeight = 0;
        }
    }

    @Inject(method = "calculateDropMap", at = @At("HEAD"), cancellable = true)
    private void vmTweak$calculateDropMap(CallbackInfo ci) {
        if (!gtnl$enableMixin) return;

        Optional<String> dimensionNameOverride = vmTweak$resolveDimensionNameOverride();
        if (dimensionNameOverride.isEmpty()) return;

        this.dropMap = null;
        this.extraDropMap = null;
        this.totalWeight = 0;
        this.canVoidMine = false;

        this.dimensionDef = DimensionDef.getDefByName(dimensionNameOverride.get());
        if (this.dimensionDef == null || !this.dimensionDef.canBeVoidMined()) {
            ci.cancel();
            return;
        }

        this.canVoidMine = true;
        String dimensionName = this.dimensionDef.getDimensionName();
        this.dropMap = VoidMinerUtility.dropMapsByDimName.getOrDefault(dimensionName, new VoidMinerUtility.DropMap());
        this.extraDropMap = VoidMinerUtility.extraDropsByDimName
            .getOrDefault(dimensionName, new VoidMinerUtility.DropMap());
        this.dropMap.isDistributionCached(this.extraDropMap);
        this.totalWeight = this.dropMap.getTotalWeight() + this.extraDropMap.getTotalWeight();

        ci.cancel();
    }

    @Unique
    private Text vmTweak$getDimensionDisplayName() {
        if (!gtnl$enableMixin) return Text.EMPTY;
        String ext = null;
        try {
            Block block = ModBlocks.getBlock(vmTweak$mLastDimensionOverride);
            ext = new ItemStack(block).getDisplayName();
        } catch (Exception e) {
            ScienceNotLeisure.LOG.debug(
                "[VMTweakMixin] Failed to get display name for dimension: {}",
                vmTweak$mLastDimensionOverride,
                e);
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
            .toList();
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

            if (inputOres.isEmpty() || (this.blacklist && inputOres.stream()
                .noneMatch(is -> GTUtility.areStacksEqual(is, output)))
                || (!this.blacklist && inputOres.stream()
                    .anyMatch(is -> GTUtility.areStacksEqual(is, output)))) {
                this.addOutputPartial(stackPart);
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
