package com.science.gtnl.mixins.late.Gregtech;

import static gregtech.api.util.GTUtility.*;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.machine.VMTweakHelper;

import bwcrossmod.galacticgreg.MTEVoidMinerBase;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gtneioreplugin.plugin.block.ModBlocks;
import gtneioreplugin.plugin.item.ItemDimensionDisplay;

@Mixin(value = MTEVoidMinerBase.class, remap = false)
public abstract class MixinMTEVoidMinerBase extends MTEEnhancedMultiBlockBase<MixinMTEVoidMinerBase> {

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
