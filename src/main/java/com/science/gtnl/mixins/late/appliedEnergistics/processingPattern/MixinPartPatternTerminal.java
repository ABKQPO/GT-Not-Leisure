package com.science.gtnl.mixins.late.appliedEnergistics.processingPattern;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.mixinHelper.IProcessingPatternInventoryAccess;
import com.science.gtnl.common.me.processingPattern.ProcessingPatternCapacity;
import com.science.gtnl.common.me.processingPattern.ProcessingPatternInventoryData;

import appeng.api.parts.IPatternTerminalEx;
import appeng.parts.reporting.AbstractPartTerminal;
import appeng.parts.reporting.PartPatternTerminal;
import appeng.parts.reporting.PartPatternTerminalEx;
import appeng.tile.inventory.IAEStackInventory;
import appeng.tile.inventory.IIAEStackInventory;
import appeng.tile.inventory.InvOperation;
import appeng.util.Platform;

@Mixin(value = PartPatternTerminal.class, remap = false)
public abstract class MixinPartPatternTerminal extends AbstractPartTerminal
    implements IProcessingPatternInventoryAccess {

    @Shadow
    @Final
    @Mutable
    private IAEStackInventory crafting;

    @Shadow
    @Final
    @Mutable
    private IAEStackInventory output;

    @Unique
    private NBTTagCompound gtnl$storedInventory;

    protected MixinPartPatternTerminal(ItemStack itemStack) {
        super(itemStack);
    }

    @Inject(method = "onChangeInventory", at = @At("TAIL"))
    private void gtnl$normalizeLoadedPattern(IInventory inv, int slot, InvOperation mc, ItemStack removedStack,
        ItemStack newStack, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if ((Object) this instanceof PartPatternTerminalEx) {
            boolean inverted = ((IPatternTerminalEx) this).isInverted();
            ProcessingPatternInventoryData.clearHiddenSlots(inverted ? this.crafting : this.output);
        }
    }

    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void gtnl$rememberInventory(NBTTagCompound data, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if ((Object) this instanceof PartPatternTerminalEx) {
            this.gtnl$storedInventory = ProcessingPatternInventoryData
                .rememberInventory(data, data, this.crafting.getSizeInventory(), this.output.getSizeInventory());
        }
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void gtnl$preserveInventory(NBTTagCompound data, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (!((Object) this instanceof PartPatternTerminalEx)) {
            return;
        }
        this.gtnl$storedInventory = ProcessingPatternInventoryData.preserveInventory(
            data,
            data,
            this.gtnl$storedInventory,
            ((IPatternTerminalEx) this).isInverted(),
            this.crafting.getSizeInventory(),
            this.output.getSizeInventory());
    }

    @Override
    public void gtnl$clearProcessingPatternOverflow() {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        this.gtnl$storedInventory = null;
    }

    @Override
    public void gtnl$resizeProcessingPatternInventory() {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        int size = ProcessingPatternCapacity.largeSideCapacity();
        if (this.crafting.getSizeInventory() == size && this.output.getSizeInventory() == size) {
            return;
        }

        var resized = ProcessingPatternInventoryData.resize(
            this.crafting,
            this.output,
            this.gtnl$storedInventory,
            ((IPatternTerminalEx) this).isInverted(),
            size,
            (IIAEStackInventory) this);
        this.crafting = resized.crafting();
        this.output = resized.output();
        this.gtnl$storedInventory = resized.stored();
        if (Platform.isServer()) {
            this.getHost()
                .markForSave();
        }
    }
}
