package com.science.gtnl.mixins.late.appliedEnergistics.processingPattern;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.mixinHelper.IProcessingPatternOverflowAccess;
import com.science.gtnl.utils.appliedEnergistics.processingPattern.ProcessingPatternCapacity;
import com.science.gtnl.utils.appliedEnergistics.processingPattern.ProcessingPatternInventoryData;

import appeng.api.storage.StorageName;
import appeng.api.storage.data.IAEStack;
import appeng.parts.reporting.PartPatternTerminal;
import appeng.parts.reporting.PartPatternTerminalEx;
import appeng.tile.inventory.IAEStackInventory;

@Mixin(value = PartPatternTerminalEx.class, remap = false)
public abstract class MixinPartPatternTerminalEx extends PartPatternTerminal {

    protected MixinPartPatternTerminalEx(ItemStack itemStack) {
        super(itemStack);
    }

    @Shadow
    private int activePage;

    @Shadow
    private boolean inverted;

    @Shadow
    public abstract void setInverted(boolean inverted);

    @Shadow
    public abstract void setActivePage(int activePage);

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void gtnl$saveNativePage(NBTTagCompound data, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        data.setInteger("activePage", ProcessingPatternInventoryData.nativePage(this.activePage));
    }

    @Inject(method = "setInverted", at = @At("HEAD"), cancellable = true)
    private void gtnl$setInvertedWithOpenedCapacity(boolean inverted, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        IAEStackInventory hidden = this
            .getAEInventoryByName(inverted ? StorageName.CRAFTING_INPUT : StorageName.CRAFTING_OUTPUT);
        int slotsPerSide = hidden.getSizeInventory();
        if (slotsPerSide / ProcessingPatternCapacity.SLOTS_PER_PAGE == ProcessingPatternCapacity.pageCount()) {
            return;
        }
        this.inverted = inverted;
        ProcessingPatternInventoryData.clearHiddenSlots(hidden);
        callbackInfo.cancel();
    }

    /** Returns the configured number of input pages. */
    @Inject(method = "getPatternInputPages", at = @At("HEAD"), cancellable = true)
    private void gtnl$getPatternInputPages(CallbackInfoReturnable<Integer> callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        callbackInfo.setReturnValue(ProcessingPatternCapacity.pageCount());
    }

    /** Returns the configured number of output pages. */
    @Inject(method = "getPatternOutputPages", at = @At("HEAD"), cancellable = true)
    private void gtnl$getPatternOutputPages(CallbackInfoReturnable<Integer> callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        callbackInfo.setReturnValue(ProcessingPatternCapacity.pageCount());
    }

    /** Selects the orientation that can display the encoded pattern. */
    @Inject(method = "exPatternTerminalCall", at = @At("HEAD"), cancellable = true)
    private void gtnl$exPatternTerminalCall(IAEStack<?>[] in, IAEStack<?>[] out, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        ((IProcessingPatternOverflowAccess) this).gtnl$clearProcessingPatternOverflow();
        int inputsCount = 0;
        int outputCount = 0;

        for (IAEStack<?> input : in) {
            if (input != null) {
                inputsCount++;
            }
        }

        for (IAEStack<?> output : out) {
            if (output != null) {
                outputCount++;
            }
        }

        int smallSideCapacity = this.getAEInventoryByName(StorageName.CRAFTING_INPUT)
            .getSizeInventory() / 4;
        this.setInverted(inputsCount <= smallSideCapacity && outputCount >= smallSideCapacity);
        this.setActivePage(0);
        callbackInfo.cancel();
    }

    /** Stores a page index within the configured range. */
    @Inject(method = "setActivePage", at = @At("HEAD"), cancellable = true)
    private void gtnl$setActivePage(int activePage, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        int pages = this.getAEInventoryByName(StorageName.CRAFTING_INPUT)
            .getSizeInventory() / ProcessingPatternCapacity.SLOTS_PER_PAGE;
        this.activePage = Math.max(0, Math.min(Math.max(0, pages - 1), activePage));
        callbackInfo.cancel();
    }
}
