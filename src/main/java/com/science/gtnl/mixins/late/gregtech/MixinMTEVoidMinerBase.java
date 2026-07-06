package com.science.gtnl.mixins.late.gregtech;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
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

import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.mixinHelper.IVoidMinerDimensionOverride;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.machine.VMTweakHelper;
import com.science.gtnl.utils.recipes.GTNLOverclockCalculator;

import bwcrossmod.galacticgreg.MTEVoidMinerBase;
import bwcrossmod.galacticgreg.VoidMinerUtility;
import galacticgreg.api.ModDimensionDef;
import galacticgreg.api.enums.DimensionDef;
import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
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
public abstract class MixinMTEVoidMinerBase extends MTEEnhancedMultiBlockBase<MixinMTEVoidMinerBase>
    implements IVoidMinerDimensionOverride {

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

    @Shadow
    public ItemStackHandler selected;

    @Unique
    private static boolean gtnl$enableMixin = !ModList.VMTweak.isModLoaded() && MainConfig.machine.enableVoidMinerTweak;

    @Unique
    private String vmTweak$warning = "";

    @Unique
    private int vmTweak$dimChangeVersion = 0;

    @Unique
    private String vmTweak$activeDropMapDimension = "";

    public MixinMTEVoidMinerBase(String aName) {
        super(aName);
    }

    @Unique
    private String vmTweak$resolveDimensionKey() {
        if (!gtnl$enableMixin) return "None";
        try {
            return Optional.ofNullable(this.mInventory[1])
                .filter(s -> s.getItem() instanceof ItemDimensionDisplay)
                .map(ItemDimensionDisplay::getDimension)
                .orElse("None");
        } catch (Exception e) {
            ScienceNotLeisure.LOG.debug("[VMTweakMixin] Failed to read dimension override slot.", e);
            return "None";
        }
    }

    @Unique
    private Optional<String> vmTweak$resolveDimensionNameOverride() {
        String dimensionKey = vmTweak$resolveDimensionKey();
        if ("None".equals(dimensionKey)) return Optional.empty();

        return vmTweak$toInternalDimensionName(dimensionKey).or(() -> {
            Integer dimensionId = VMTweakHelper.DIM_MAPPING.inverse()
                .get(dimensionKey);
            if (dimensionId == null) return Optional.empty();
            return vmTweak$toInternalDimensionName(VMTweakHelper.getNameForID(dimensionId));
        })
            .or(() -> Optional.of(dimensionKey));
    }

    @Unique
    private Optional<String> vmTweak$toInternalDimensionName(String dimensionName) {
        if (dimensionName == null || dimensionName.isEmpty()) return Optional.empty();

        String internalName = DimensionHelper.ABBR_TO_INTERNAL.get(dimensionName);
        if (internalName != null) return Optional.of(internalName);

        int fullNameIndex = DimensionHelper.ALL_DIM_NAMES.indexOf(dimensionName);
        if (fullNameIndex >= 0) {
            String abbr = DimensionHelper.ALL_DISPLAYED_NAMES.get(fullNameIndex);
            return Optional.ofNullable(DimensionHelper.ABBR_TO_INTERNAL.get(abbr));
        }

        if (VoidMinerUtility.dropMapsByDimName.containsKey(dimensionName)) return Optional.of(dimensionName);
        return Optional.empty();
    }

    @Unique
    private String vmTweak$mLastDimensionOverride = "None";

    @Inject(method = "saveNBTData", at = @At("HEAD"), require = 1, remap = false)
    public void vmTweak$saveNBT(NBTTagCompound aNBT, CallbackInfo c) {
        if (!gtnl$enableMixin) return;
        aNBT.setString("mLastDimensionOverride", this.vmTweak$mLastDimensionOverride);
        aNBT.setString("vmTweak$activeDropMapDimension", this.vmTweak$activeDropMapDimension);
    }

    @Inject(method = "loadNBTData", at = @At("HEAD"), require = 1, remap = false)
    public void vmTweak$loadNBT(NBTTagCompound aNBT, CallbackInfo c) {
        if (!gtnl$enableMixin) return;
        this.vmTweak$mLastDimensionOverride = aNBT.getString("mLastDimensionOverride");
        if (this.vmTweak$mLastDimensionOverride.isEmpty()) {
            this.vmTweak$mLastDimensionOverride = "None";
        }
        this.vmTweak$activeDropMapDimension = aNBT.getString("vmTweak$activeDropMapDimension");
    }

    @Inject(method = "working", at = @At("HEAD"), remap = false)
    public void vmTweak$onWorkingTick(CallbackInfoReturnable<Boolean> cir) {
        if (!gtnl$enableMixin) return;
        String dim = vmTweak$resolveDimensionKey();

        if (!Objects.equals(dim, vmTweak$mLastDimensionOverride)) {
            vmTweak$mLastDimensionOverride = dim;
            vmTweak$dimChangeVersion++;
            vmTweak$recalculateDropMap();
        }
    }

    @Inject(method = "onFirstTick", at = @At("HEAD"), require = 1, remap = false, cancellable = true)
    private void vmTweak$onFirstTick(IGregTechTileEntity aBaseMetaTileEntity, CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        ci.cancel();
        super.onFirstTick(aBaseMetaTileEntity);
        vmTweak$recalculateDropMap();
        vmTweak$resizeSelected();
    }

    @Inject(method = "calculateDropMap", at = @At("HEAD"), require = 1, remap = false, cancellable = true)
    private void vmTweak$calculateDropMap(CallbackInfo ci) {
        if (!gtnl$enableMixin) return;
        vmTweak$recalculateDropMap();
        ci.cancel();
    }

    @Unique
    private void vmTweak$recalculateDropMap() {
        this.dropMap = null;
        this.extraDropMap = null;
        this.totalWeight = 0;
        this.canVoidMine = false;
        this.vmTweak$warning = "";

        String dimensionKey = vmTweak$resolveDimensionKey();
        this.vmTweak$mLastDimensionOverride = dimensionKey;
        Optional<String> dimensionNameOverride = vmTweak$resolveDimensionNameOverride();
        boolean hasOverride = dimensionNameOverride.isPresent();

        if (getBaseMetaTileEntity() != null) {
            this.dimensionDef = DimensionDef.getDefForWorld(getBaseMetaTileEntity().getWorld());
        }

        if (hasOverride) {
            String dimensionName = dimensionNameOverride.get();
            if (VoidMinerUtility.dropMapsByDimName.containsKey(dimensionName)) {
                this.canVoidMine = true;
                vmTweak$setDropMaps(dimensionName);
                vmTweak$resizeSelected();
                return;
            }
            this.vmTweak$warning = "vmtweak.gui.override.error";
        }

        if (this.dimensionDef == null || !this.dimensionDef.canBeVoidMined()) {
            this.dropMap = new VoidMinerUtility.DropMap();
            this.extraDropMap = new VoidMinerUtility.DropMap();
            if (!this.vmTweak$activeDropMapDimension.isEmpty()) {
                this.selected.setSize(0);
            }
            vmTweak$activeDropMapDimension = "";
            vmTweak$resizeSelected();
            return;
        }

        this.canVoidMine = true;
        String dimensionName = this.dimensionDef.getDimensionName();
        vmTweak$setDropMaps(dimensionName);

        if (hasOverride) {
            this.vmTweak$warning = this.totalWeight > 0 ? "vmtweak.gui.override.failed" : "vmtweak.gui.override.error";
        }
        vmTweak$resizeSelected();
    }

    @Unique
    private void vmTweak$setDropMaps(String dimensionName) {
        boolean dimensionChanged = !this.vmTweak$activeDropMapDimension.isEmpty()
            && !Objects.equals(this.vmTweak$activeDropMapDimension, dimensionName);
        this.vmTweak$activeDropMapDimension = dimensionName;
        this.dropMap = VoidMinerUtility.dropMapsByDimName.getOrDefault(dimensionName, new VoidMinerUtility.DropMap());
        this.extraDropMap = VoidMinerUtility.extraDropsByDimName
            .getOrDefault(dimensionName, new VoidMinerUtility.DropMap());
        this.dropMap.isDistributionCached(this.extraDropMap);
        this.totalWeight = this.dropMap.getTotalWeight() + this.extraDropMap.getTotalWeight();
        if (dimensionChanged) {
            vmTweak$resetSelected();
        }
    }

    @Unique
    private void vmTweak$resizeSelected() {
        if (this.dropMap == null || this.selected == null) return;
        var ores = this.dropMap.getOres();
        if (ores == null) return;
        int oreCount = ores.length;
        if (this.selected.getSlots() < oreCount) {
            this.selected.setSize(oreCount);
        }
    }

    @Unique
    private void vmTweak$resetSelected() {
        if (this.dropMap == null || this.selected == null) return;
        var ores = this.dropMap.getOres();
        this.selected.setSize(ores == null ? 0 : ores.length);
    }

    @Inject(method = "getCopiedData", at = @At("HEAD"), require = 1, remap = false, cancellable = true)
    private void vmTweak$getCopiedData(EntityPlayer player, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (!gtnl$enableMixin) return;
        vmTweak$recalculateDropMap();

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("type", MTEVoidMinerBase.COPIED_DATA_IDENTIFIER);
        tag.setString("dimension", vmTweak$getCopyDimension());
        tag.setTag("selected", selected.serializeNBT());
        tag.setBoolean("blacklist", blacklist);
        cir.setReturnValue(tag);
    }

    @Inject(method = "pasteCopiedData", at = @At("HEAD"), require = 1, remap = false, cancellable = true)
    private void vmTweak$pasteCopiedData(EntityPlayer player, NBTTagCompound nbt, CallbackInfoReturnable<Boolean> cir) {
        if (!gtnl$enableMixin) return;
        vmTweak$recalculateDropMap();
        if (nbt == null || !MTEVoidMinerBase.COPIED_DATA_IDENTIFIER.equals(nbt.getString("type"))) {
            cir.setReturnValue(false);
            return;
        }
        if (!vmTweak$getCopyDimension().equals(nbt.getString("dimension"))) {
            cir.setReturnValue(false);
            return;
        }

        this.selected.deserializeNBT(nbt.getCompoundTag("selected"));
        this.blacklist = nbt.getBoolean("blacklist");
        cir.setReturnValue(true);
    }

    @Unique
    private String vmTweak$getCopyDimension() {
        return vmTweak$activeDropMapDimension.isEmpty() ? "unknown" : vmTweak$activeDropMapDimension;
    }

    @Unique
    private Text vmTweak$getDimensionDisplayName() {
        if (!gtnl$enableMixin) return Text.EMPTY;
        if (!vmTweak$warning.isEmpty()) {
            return new Text(EnumChatFormatting.YELLOW + StatCollector.translateToLocal(vmTweak$warning));
        }

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

    @Override
    public String getGtnl$overrideDisplayText() {
        if (!gtnl$enableMixin || "None".equals(vmTweak$mLastDimensionOverride)) return "";
        if (!vmTweak$warning.isEmpty()) {
            return "!" + vmTweak$warning;
        }
        return vmTweak$mLastDimensionOverride;
    }

    @Override
    public String getGtnl$warning() {
        if (!gtnl$enableMixin) return "";
        return vmTweak$warning;
    }

    @Override
    public int getGtnl$dimensionChangeVersion() {
        if (!gtnl$enableMixin) return 0;
        return vmTweak$dimChangeVersion;
    }
}
