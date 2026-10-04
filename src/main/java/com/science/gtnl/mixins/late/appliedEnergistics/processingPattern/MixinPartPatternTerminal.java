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
import com.science.gtnl.utils.appliedEnergistics.processingPattern.ProcessingPatternCapacity;
import com.science.gtnl.utils.appliedEnergistics.processingPattern.ProcessingPatternInventoryData;

import appeng.api.parts.IPatternTerminalEx;
import appeng.api.storage.StorageName;
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
    private void gtnl$normalizeLoadedPattern(IInventory inventory, int slot, InvOperation operation,
        ItemStack removedStack, ItemStack newStack, CallbackInfo callbackInfo) {
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
            ProcessingPatternInventoryData.restoreSavedOverflow(data, data, "craftingGrid");
            ProcessingPatternInventoryData.restoreSavedOverflow(data, data, "outputList");
            boolean inverted = data.getBoolean("inverted");
            this.gtnl$storedInventory = ProcessingPatternInventoryData.snapshotInventories(
                data,
                inverted ? this.crafting.getSizeInventory() / 4 : this.crafting.getSizeInventory(),
                inverted ? this.output.getSizeInventory() : this.output.getSizeInventory() / 4);
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
        boolean inverted = ((IPatternTerminalEx) this).isInverted();
        ProcessingPatternInventoryData.preserveOverflow(
            data,
            this.gtnl$storedInventory,
            "craftingGrid",
            inverted ? this.crafting.getSizeInventory() / 4 : this.crafting.getSizeInventory());
        ProcessingPatternInventoryData.preserveOverflow(
            data,
            this.gtnl$storedInventory,
            "outputList",
            inverted ? this.output.getSizeInventory() : this.output.getSizeInventory() / 4);
        this.gtnl$storedInventory = ProcessingPatternInventoryData.snapshotInventories(
            data,
            inverted ? this.crafting.getSizeInventory() / 4 : this.crafting.getSizeInventory(),
            inverted ? this.output.getSizeInventory() : this.output.getSizeInventory() / 4);
        int nativeInputs = ProcessingPatternCapacity.DEFAULT_MULTIPLIER
            * (inverted ? ProcessingPatternCapacity.SMALL_SIDE_SLOTS_PER_PAGE
                : ProcessingPatternCapacity.SLOTS_PER_PAGE);
        int nativeOutputs = ProcessingPatternCapacity.DEFAULT_MULTIPLIER
            * (inverted ? ProcessingPatternCapacity.SLOTS_PER_PAGE
                : ProcessingPatternCapacity.SMALL_SIDE_SLOTS_PER_PAGE);
        ProcessingPatternInventoryData.saveOverflow(data, data, "craftingGrid", nativeInputs);
        ProcessingPatternInventoryData.saveOverflow(data, data, "outputList", nativeOutputs);
        ProcessingPatternInventoryData.limitSlots(data, "craftingGrid", nativeInputs);
        ProcessingPatternInventoryData.limitSlots(data, "outputList", nativeOutputs);
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

        NBTTagCompound data = new NBTTagCompound();
        this.crafting.writeToNBT(data, "craftingGrid");
        this.output.writeToNBT(data, "outputList");
        boolean inverted = ((IPatternTerminalEx) this).isInverted();
        ProcessingPatternInventoryData.preserveOverflow(
            data,
            this.gtnl$storedInventory,
            "craftingGrid",
            inverted ? this.crafting.getSizeInventory() / 4 : this.crafting.getSizeInventory());
        ProcessingPatternInventoryData.preserveOverflow(
            data,
            this.gtnl$storedInventory,
            "outputList",
            inverted ? this.output.getSizeInventory() : this.output.getSizeInventory() / 4);

        NBTTagCompound visible = (NBTTagCompound) data.copy();
        ProcessingPatternInventoryData.limitSlots(visible, "craftingGrid", inverted ? size / 4 : size);
        ProcessingPatternInventoryData.limitSlots(visible, "outputList", inverted ? size : size / 4);

        IAEStackInventory newCrafting = new IAEStackInventory(
            (IIAEStackInventory) this,
            size,
            StorageName.CRAFTING_INPUT);
        IAEStackInventory newOutput = new IAEStackInventory(
            (IIAEStackInventory) this,
            size,
            StorageName.CRAFTING_OUTPUT);
        newCrafting.readFromNBT(visible, "craftingGrid");
        newOutput.readFromNBT(visible, "outputList");
        this.crafting = newCrafting;
        this.output = newOutput;
        this.gtnl$storedInventory = ProcessingPatternInventoryData
            .snapshotInventories(data, inverted ? size / 4 : size, inverted ? size : size / 4);
        if (Platform.isServer()) {
            this.getHost()
                .markForSave();
        }
    }
}
