package com.science.gtnl.mixins.late.appliedEnergistics.processingPattern;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.mixinHelper.IProcessingPatternInventoryBatchAccess;
import com.science.gtnl.api.mixinHelper.IProcessingPatternOverflowAccess;
import com.science.gtnl.utils.appliedEnergistics.processingPattern.ProcessingPatternCapacity;
import com.science.gtnl.utils.appliedEnergistics.processingPattern.ProcessingPatternInventoryData;

import appeng.api.storage.data.IAEStack;
import appeng.items.contents.WirelessPatternTerminalGuiObject;
import appeng.tile.inventory.IAEStackInventory;
import appeng.tile.inventory.InvOperation;

@Mixin(value = WirelessPatternTerminalGuiObject.class, remap = false)
public abstract class MixinWirelessPatternTerminalGuiObject
    implements IProcessingPatternOverflowAccess, IProcessingPatternInventoryBatchAccess {

    @Shadow
    int craftingInvSize;

    @Shadow
    int outputInvSize;

    @Shadow
    IAEStackInventory crafting;

    @Shadow
    IAEStackInventory output;

    @Shadow
    private boolean inverted;

    @Shadow
    private int activePage;

    @Shadow
    @Final
    private int mode;

    @Shadow
    @Final
    private String nbtPrefix;

    @Shadow
    public abstract void setInverted(boolean inverted);

    @Shadow
    public abstract void setActivePage(int activePage);

    @Shadow
    public abstract void writeInventory();

    @Unique
    private NBTTagCompound gtnl$storedInventory;

    @Unique
    private int gtnl$inventoryUpdateDepth;

    @Override
    public void gtnl$beginInventoryUpdate() {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.mode == 2) {
            this.gtnl$inventoryUpdateDepth++;
        }
    }

    @Override
    public void gtnl$endInventoryUpdate() {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.mode == 2 && this.gtnl$inventoryUpdateDepth > 0 && --this.gtnl$inventoryUpdateDepth == 0) {
            this.writeInventory();
        }
    }

    @Inject(method = "onChangeInventory", at = @At("HEAD"))
    private void gtnl$beginPatternChange(IInventory inventory, int slot, InvOperation operation, ItemStack removedStack,
        ItemStack newStack, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        this.gtnl$beginInventoryUpdate();
    }

    @Inject(method = "onChangeInventory", at = @At("TAIL"))
    private void gtnl$finishPatternChange(IInventory inventory, int slot, InvOperation operation,
        ItemStack removedStack, ItemStack newStack, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.mode == 2 && this.crafting != null && this.output != null) {
            ProcessingPatternInventoryData.clearHiddenSlots(this.inverted ? this.crafting : this.output);
        }
        this.gtnl$endInventoryUpdate();
    }

    @Override
    public void gtnl$clearProcessingPatternOverflow() {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.mode != 2) {
            return;
        }
        ItemStack stack = ((WirelessPatternTerminalGuiObject) (Object) this).getItemStack();
        if (stack.hasTagCompound()) {
            ProcessingPatternInventoryData.clearSavedOverflow(stack.getTagCompound());
        }
        this.gtnl$storedInventory = new NBTTagCompound();
    }

    @Inject(method = "setInventorySize", at = @At("TAIL"))
    private void gtnl$setInventorySize(int inputs, int outputs, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.mode == 2) {
            int size = ProcessingPatternCapacity.largeSideCapacity();
            this.craftingInvSize = size;
            this.outputInvSize = size;
        }
    }

    @Inject(method = "readInventory", at = @At("HEAD"))
    private void gtnl$rememberInventory(CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.mode == 2) {
            ItemStack stack = ((WirelessPatternTerminalGuiObject) (Object) this).getItemStack();
            if (stack.hasTagCompound()) {
                NBTTagCompound root = stack.getTagCompound();
                NBTTagCompound inventory = root.getCompoundTag(this.nbtPrefix);
                ProcessingPatternInventoryData.restoreSavedOverflow(root, inventory, "craftingGrid");
                ProcessingPatternInventoryData.restoreSavedOverflow(root, inventory, "outputList");
                root.setTag(this.nbtPrefix, inventory);
                boolean inverted = inventory.getBoolean("inverted");
                this.gtnl$storedInventory = ProcessingPatternInventoryData.snapshotInventories(
                    inventory,
                    inverted ? this.craftingInvSize / 4 : this.craftingInvSize,
                    inverted ? this.outputInvSize : this.outputInvSize / 4);
                ProcessingPatternInventoryData
                    .limitSlots(inventory, "craftingGrid", inverted ? this.craftingInvSize / 4 : this.craftingInvSize);
                ProcessingPatternInventoryData
                    .limitSlots(inventory, "outputList", inverted ? this.outputInvSize : this.outputInvSize / 4);
            }
        }
    }

    @Inject(method = "readInventory", at = @At("TAIL"))
    private void gtnl$saveCompatibleInventory(CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.mode == 2) {
            this.activePage = Math.max(
                0,
                Math.min(
                    Math.max(0, this.craftingInvSize / ProcessingPatternCapacity.SLOTS_PER_PAGE - 1),
                    this.activePage));
            this.writeInventory();
        }
    }

    @Inject(method = "writeInventory", at = @At("TAIL"))
    private void gtnl$preserveInventory(CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.mode != 2) {
            return;
        }
        WirelessPatternTerminalGuiObject terminal = (WirelessPatternTerminalGuiObject) (Object) this;
        NBTTagCompound root = terminal.getItemStack()
            .getTagCompound();
        NBTTagCompound tag = root.getCompoundTag(this.nbtPrefix);
        tag.setInteger("activePage", ProcessingPatternInventoryData.nativePage(this.activePage));
        ProcessingPatternInventoryData.preserveOverflow(
            tag,
            this.gtnl$storedInventory,
            "craftingGrid",
            this.inverted ? this.craftingInvSize / 4 : this.craftingInvSize);
        ProcessingPatternInventoryData.preserveOverflow(
            tag,
            this.gtnl$storedInventory,
            "outputList",
            this.inverted ? this.outputInvSize : this.outputInvSize / 4);
        this.gtnl$storedInventory = ProcessingPatternInventoryData.snapshotInventories(
            tag,
            this.inverted ? this.craftingInvSize / 4 : this.craftingInvSize,
            this.inverted ? this.outputInvSize : this.outputInvSize / 4);
        int nativeInputs = ProcessingPatternCapacity.DEFAULT_MULTIPLIER
            * (this.inverted ? ProcessingPatternCapacity.SMALL_SIDE_SLOTS_PER_PAGE
                : ProcessingPatternCapacity.SLOTS_PER_PAGE);
        int nativeOutputs = ProcessingPatternCapacity.DEFAULT_MULTIPLIER
            * (this.inverted ? ProcessingPatternCapacity.SLOTS_PER_PAGE
                : ProcessingPatternCapacity.SMALL_SIDE_SLOTS_PER_PAGE);
        ProcessingPatternInventoryData.saveOverflow(root, tag, "craftingGrid", nativeInputs);
        ProcessingPatternInventoryData.saveOverflow(root, tag, "outputList", nativeOutputs);
        ProcessingPatternInventoryData.limitSlots(tag, "craftingGrid", nativeInputs);
        ProcessingPatternInventoryData.limitSlots(tag, "outputList", nativeOutputs);
        root.setTag(this.nbtPrefix, tag);
    }

    @Inject(method = "exPatternTerminalCall", at = @At("HEAD"), cancellable = true)
    private void gtnl$selectOrientation(IAEStack<?>[] inputs, IAEStack<?>[] outputs, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.mode != 2) {
            return;
        }
        this.gtnl$clearProcessingPatternOverflow();
        int inputCount = countNonNull(inputs);
        int outputCount = countNonNull(outputs);
        int smallSideCapacity = this.craftingInvSize / 4;
        this.setInverted(inputCount <= smallSideCapacity && outputCount >= smallSideCapacity);
        this.setActivePage(0);
        callbackInfo.cancel();
    }

    @Inject(method = "setInverted", at = @At("HEAD"))
    private void gtnl$clearHiddenSide(boolean inverted, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.mode != 2 || this.crafting == null || this.output == null) {
            return;
        }
        IAEStackInventory hidden = inverted ? this.crafting : this.output;
        this.gtnl$inventoryUpdateDepth++;
        try {
            ProcessingPatternInventoryData.clearHiddenSlots(hidden);
        } finally {
            this.gtnl$inventoryUpdateDepth--;
        }
    }

    @Inject(method = "writeInventory", at = @At("HEAD"), cancellable = true)
    private void gtnl$deferBulkSave(CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.gtnl$inventoryUpdateDepth > 0) {
            callbackInfo.cancel();
        }
    }

    @ModifyVariable(method = "setActivePage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int gtnl$clampActivePage(int page) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return page;
        }
        return this.mode == 2 ? Math
            .max(0, Math.min(Math.max(0, this.craftingInvSize / ProcessingPatternCapacity.SLOTS_PER_PAGE - 1), page))
            : page;
    }

    private static int countNonNull(IAEStack<?>[] stacks) {
        int count = 0;
        for (IAEStack<?> stack : stacks) {
            if (stack != null) {
                count++;
            }
        }
        return count;
    }
}
