package com.science.gtnl.mixins.late.AppliedEnergistics;

import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import com.science.gtnl.common.block.blocks.tile.TileEntityMEChisel;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.ChiselPatternDetails;
import com.science.gtnl.utils.DireCraftingPatternDetails;
import com.science.gtnl.utils.LargeInventoryCrafting;
import com.science.gtnl.utils.Utils;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.crafting.MECraftingInventory;
import appeng.me.cache.CraftingGridCache;
import appeng.me.cluster.implementations.CraftingCPUCluster;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class MixinCraftingCPUCluster {

    @Shadow
    private int remainingOperations;
    @Shadow
    private MachineSource machineSrc;
    @Shadow
    private MECraftingInventory inventory;

    @SuppressWarnings({ "FieldCanBeLocal", "FieldMayBeFinal" })
    @Unique
    private boolean r$IgnoreParallel = false;

    /**
     * Calculates the maximum pattern batch size at the start of crafting execution.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getKey()Ljava/lang/Object;"))
    private Object getKeyR(Map.Entry<ICraftingPatternDetails, AccessorTaskProgress> instance,
        Operation<ICraftingPatternDetails> original, @Share("snl$craftingFrequency") LocalLongRef craftingFrequencyR) {
        var key = original.call(instance);

        long max = 0;
        var list = key.getCondensedOutputs();
        for (IAEItemStack stack : list) {
            long size = stack.getStackSize();
            if (size > max) max = size;
        }

        craftingFrequencyR.set(
            instance.getValue()
                .getValue());
        return key;
    }

    /**
     * Checks whether the pattern can be batched by Assembler Matrix or ME Chisel, then clamps the batch size.
     */
    @Inject(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/crafting/ICraftingMedium;isBusy()Z",
            shift = At.Shift.AFTER))
    private void executeCraftingI(IEnergyGrid eg, CraftingGridCache cc, CallbackInfo ci,
        @Local(name = "medium") ICraftingMedium instance, @Local(name = "details") ICraftingPatternDetails details,
        @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequencyR) {
        assembly.set(false);
        if ((details instanceof ChiselPatternDetails && instance instanceof TileEntityMEChisel)
            || ((details.isCraftable() || details instanceof DireCraftingPatternDetails)
                && instance instanceof AssemblerMatrix ef
                && !ef.isBusy())) {
            assembly.set(true);
            var craftingFrequency = Math
                .min(craftingFrequencyR.get(), Long.MAX_VALUE / details.getCondensedOutputs()[0].getStackSize());
            for (IAEItemStack input : details.getCondensedInputs()) {
                if (input == null) continue;
                final long size = craftingFrequency;
                var item = this.inventory.extractItems(
                    input.copy()
                        .setStackSize(size),
                    Actionable.SIMULATE,
                    this.machineSrc);
                if (item == null) continue;
                if (item.getStackSize() < size) {
                    long size0 = item.getStackSize();
                    if (size0 < 2) {
                        craftingFrequency = 1;
                    } else {
                        craftingFrequency = size0;
                    }
                }
            }
            craftingFrequencyR.set(craftingFrequency);
        }
    }

    /**
     * Recalculates energy usage for batched patterns.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/energy/IEnergyGrid;extractAEPower(DLappeng/api/config/Actionable;Lappeng/api/config/PowerMultiplier;)D"))
    private double extractAEPowerR(IEnergyGrid eg, double v, Actionable actionable, PowerMultiplier powerMultiplier,
        Operation<Double> original, @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequencyR) {
        if (assembly.get()) {
            var craftingFrequency = craftingFrequencyR.get();
            var sum = v * craftingFrequency;
            var o = eg.extractAEPower(sum, Actionable.SIMULATE, powerMultiplier);
            if (o < sum - 0.01) {
                long s = (long) (o / sum * craftingFrequency);
                craftingFrequencyR.set(s);
                if (s < 1) {
                    return original.call(eg, v, actionable, powerMultiplier);
                } else {
                    return original.call(eg, v * s, Actionable.SIMULATE, powerMultiplier);
                }
            }
            return o;
        }
        return original.call(eg, v, actionable, powerMultiplier);
    }

    /**
     * Extracts ingredients with the computed batch size.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/crafting/MECraftingInventory;extractItems(Lappeng/api/storage/data/IAEStack;Lappeng/api/config/Actionable;)Lappeng/api/storage/data/IAEStack;"))
    private IAEStack<?> extractItemsR(MECraftingInventory instance, IAEStack<?> request, Actionable mode,
        Operation<IAEStack<?>> original, @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequency) {
        if (assembly.get()) {
            request = request.copy()
                .setStackSize(request.getStackSize() * craftingFrequency.get());
        }
        return original.call(instance, request, mode);
    }

    /**
     * Scales stack notifications for batched patterns.
     */
    @Unique
    private void r$postChange(CraftingCPUCluster instance, IAEStack<?> receiver, BaseActionSource single,
        LocalBooleanRef assembly, LocalLongRef craftingFrequency, Operation<Void> original) {
        if (assembly.get()) {
            receiver = receiver.copy()
                .setStackSize(receiver.getStackSize() * craftingFrequency.get());
        }
        original.call(instance, receiver, single);
    }

    /**
     * Scales mutable stack notifications for batched patterns.
     */
    @Unique
    private void r$postChange1(CraftingCPUCluster instance, IAEStack<?> receiver, BaseActionSource single,
        LocalBooleanRef assembly, LocalLongRef craftingFrequency, Operation<Void> original) {
        if (assembly.get()) {
            receiver.setStackSize(receiver.getStackSize() * craftingFrequency.get());
        }
        original.call(instance, receiver, single);
    }

    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;postChange(Lappeng/api/storage/data/IAEStack;Lappeng/api/networking/security/BaseActionSource;)V",
            ordinal = 1))
    private void postChangeR1(CraftingCPUCluster instance, IAEStack<?> receiver, BaseActionSource single,
        Operation<Void> original, @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequencyR) {
        r$postChange1(instance, receiver, single, assembly, craftingFrequencyR, original);
    }

    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;postChange(Lappeng/api/storage/data/IAEStack;Lappeng/api/networking/security/BaseActionSource;)V",
            ordinal = 2))
    private void postChangeR2(CraftingCPUCluster instance, IAEStack<?> receiver, BaseActionSource single,
        Operation<Void> original, @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequencyR) {
        r$postChange(instance, receiver, single, assembly, craftingFrequencyR, original);
    }

    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;postChange(Lappeng/api/storage/data/IAEStack;Lappeng/api/networking/security/BaseActionSource;)V",
            ordinal = 0))
    private void postChangeR0(CraftingCPUCluster instance, IAEStack<?> receiver, BaseActionSource single,
        Operation<Void> original, @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequencyR) {
        r$postChange(instance, receiver, single, assembly, craftingFrequencyR, original);
    }

    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;postChange(Lappeng/api/storage/data/IAEStack;Lappeng/api/networking/security/BaseActionSource;)V",
            ordinal = 3))
    private void postChangeR3(CraftingCPUCluster instance, IAEStack<?> receiver, BaseActionSource single,
        Operation<Void> original, @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequencyR) {
        r$postChange1(instance, receiver, single, assembly, craftingFrequencyR, original);
    }

    /**
     * Scales expected output stacks before adding them to the waiting list.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/storage/data/IItemList;add(Lappeng/api/storage/data/IAEStack;)V",
            ordinal = 0))
    private void addR(IItemList<IAEStack<?>> instance, IAEStack<?> iaeStack, Operation<Void> original,
        @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequency) {
        if (assembly.get()) {
            iaeStack.setStackSize(iaeStack.getStackSize() * craftingFrequency.get());
        }
        original.call(instance, iaeStack);
    }

    /**
     * Scales crafting status updates for batched patterns.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;postCraftingStatusChange(Lappeng/api/storage/data/IAEStack;)V",
            ordinal = 0))
    private void postCraftingStatusChangeR(CraftingCPUCluster instance, IAEStack<?> iaeStack, Operation<Void> original,
        @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequency) {
        if (assembly.get()) {
            iaeStack.setStackSize(iaeStack.getStackSize() * craftingFrequency.get());
        }
        original.call(instance, iaeStack);
    }

    /**
     * Lets batched patterns pass the complete input extraction check.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(value = "INVOKE", target = "Lappeng/api/storage/data/IAEStack;getStackSize()J", ordinal = 0),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lappeng/util/inv/MEInventoryCrafting;setInventorySlotContents(ILappeng/api/storage/data/IAEStack;)V"),
            to = @At(
                value = "INVOKE",
                target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;postChange(Lappeng/api/storage/data/IAEStack;Lappeng/api/networking/security/BaseActionSource;)V",
                ordinal = 1)))
    private long getCountR(IAEStack<?> instance, Operation<Long> original,
        @Share("snl$assembly") LocalBooleanRef assembly) {
        if (assembly.get()) return 1L;
        return original.call(instance);
    }

    /**
     * Passes the batch size to large crafting inventories.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/crafting/ICraftingMedium;pushPattern(Lappeng/api/networking/crafting/ICraftingPatternDetails;Lnet/minecraft/inventory/InventoryCrafting;)Z"))
    private boolean pushPatternR(ICraftingMedium instance, ICraftingPatternDetails details,
        InventoryCrafting inventoryCrafting, Operation<Boolean> original,
        @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequency) {
        if (assembly.get()) ((LargeInventoryCrafting) inventoryCrafting).setAssemblerSize(craftingFrequency.get());
        return original.call(instance, details, inventoryCrafting);
    }

    /**
     * Consumes the correct number of pending task operations for batched patterns.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;", ordinal = 2))
    private Object getValueR(Map.Entry<ICraftingPatternDetails, AccessorTaskProgress> instance,
        Operation<AccessorTaskProgress> original, @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequency) {
        if (assembly.get()) {
            if (!this.r$IgnoreParallel) {
                this.remainingOperations -= (int) (craftingFrequency.get() - 1);
            }
            var value = original.call(instance);
            value.setValue(value.getValue() - (craftingFrequency.get() - 1));
            return value;
        }
        return original.call(instance);
    }

    @Inject(method = "translateFromNetwork", at = @At("HEAD"), cancellable = true)
    private static void injectTranslateFromNetwork(String name, CallbackInfoReturnable<String> cir) {
        if (!MainConfig.machine.enableHatchInterfaceTerminalEnhance) return;
        if (name == null) return;
        cir.setReturnValue(Utils.getExtraInterfaceName(name));
    }

}
