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
import com.science.gtnl.common.me.processingPattern.ProcessingPatternCapacity;
import com.science.gtnl.common.me.processingPattern.ProcessingPatternInventoryData;

import appeng.api.storage.data.IAEStack;
import appeng.items.contents.WirelessPatternTerminalGuiObject;
import appeng.tile.inventory.IAEStackInventory;
import appeng.tile.inventory.InvOperation;

@Mixin(value = WirelessPatternTerminalGuiObject.class, remap = false)
public abstract class MixinWirelessPatternTerminalGuiObject
    implements IProcessingPatternOverflowAccess, IProcessingPatternInventoryBatchAccess {

    @Shadow
    public int craftingInvSize;

    @Shadow
    public int outputInvSize;

    @Shadow
    public IAEStackInventory crafting;

    @Shadow
    public IAEStackInventory output;

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
    private void gtnl$beginPatternChange(IInventory inv, int slot, InvOperation mc, ItemStack removedStack,
        ItemStack newStack, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        this.gtnl$beginInventoryUpdate();
    }

    @Inject(method = "onChangeInventory", at = @At("TAIL"))
    private void gtnl$finishPatternChange(IInventory inv, int slot, InvOperation mc, ItemStack removedStack,
        ItemStack newStack, CallbackInfo callbackInfo) {
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
                this.gtnl$storedInventory = ProcessingPatternInventoryData
                    .rememberInventory(root, inventory, this.craftingInvSize, this.outputInvSize);
                root.setTag(this.nbtPrefix, inventory);
                boolean inverted = inventory.getBoolean("inverted");
                ProcessingPatternInventoryData.limitSlots(
                    inventory,
                    "craftingGrid",
                    ProcessingPatternInventoryData.visibleInputs(inverted, this.craftingInvSize));
                ProcessingPatternInventoryData.limitSlots(
                    inventory,
                    "outputList",
                    ProcessingPatternInventoryData.visibleOutputs(inverted, this.outputInvSize));
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
        this.gtnl$storedInventory = ProcessingPatternInventoryData.preserveInventory(
            root,
            tag,
            this.gtnl$storedInventory,
            this.inverted,
            this.craftingInvSize,
            this.outputInvSize);
        root.setTag(this.nbtPrefix, tag);
    }

    @Inject(method = "exPatternTerminalCall", at = @At("HEAD"), cancellable = true)
    private void gtnl$selectOrientation(IAEStack<?>[] in, IAEStack<?>[] out, CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return;
        }
        if (this.mode != 2) {
            return;
        }
        this.gtnl$clearProcessingPatternOverflow();
        this.setInverted(ProcessingPatternInventoryData.invertFor(in, out, this.craftingInvSize));
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
    private int gtnl$clampActivePage(int activePage) {
        if (!ProcessingPatternCapacity.isEnabled()) {
            return activePage;
        }
        return this.mode == 2
            ? Math.max(
                0,
                Math.min(Math.max(0, this.craftingInvSize / ProcessingPatternCapacity.SLOTS_PER_PAGE - 1), activePage))
            : activePage;
    }
}
