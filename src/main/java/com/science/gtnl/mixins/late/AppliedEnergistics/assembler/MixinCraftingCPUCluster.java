package com.science.gtnl.mixins.late.AppliedEnergistics.assembler;

import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.utils.LargeInventoryCrafting;

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
     * 在开头计算合成样板允许的最大并行量
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
     * 检查样板是否是合成，并且是否由装配矩阵进行
     * 若是，重新计算可进行的最大合并项
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
        if (details.isCraftable() && instance instanceof AssemblerMatrix ef) {
            if (!ef.isBusy()) {
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
            } else assembly.set(false);
        } else assembly.set(false);
    }

    /**
     * 若符合条件，重新计算耗电
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
     * 根据允许的最大合并数提取材料
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/crafting/MECraftingInventory;extractItems(Lappeng/api/storage/data/IAEItemStack;Lappeng/api/config/Actionable;Lappeng/api/networking/security/BaseActionSource;)Lappeng/api/storage/data/IAEItemStack;"))
    private IAEItemStack extractItemsR(MECraftingInventory instance, IAEItemStack request, Actionable mode,
        BaseActionSource src, Operation<IAEItemStack> original, @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequency) {
        if (assembly.get()) {
            request = request.copy()
                .setStackSize(request.getStackSize() * craftingFrequency.get());
        }
        return original.call(instance, request, mode, src);
    }

    /**
     * 根据允许的最大合并数提取材料
     */
    @Unique
    private void r$postChange(CraftingCPUCluster instance, IAEItemStack receiver, BaseActionSource single,
        LocalBooleanRef assembly, LocalLongRef craftingFrequency, Operation<Void> original) {
        if (assembly.get()) {
            receiver = receiver.copy()
                .setStackSize(receiver.getStackSize() * craftingFrequency.get());
        }
        original.call(instance, receiver, single);
    }

    /**
     * 根据允许的最大合并数提取材料
     */
    @Unique
    private void r$postChange1(CraftingCPUCluster instance, IAEItemStack receiver, BaseActionSource single,
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
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;postChange(Lappeng/api/storage/data/IAEItemStack;Lappeng/api/networking/security/BaseActionSource;)V",
            ordinal = 1))
    private void postChangeR1(CraftingCPUCluster instance, IAEItemStack receiver, BaseActionSource single,
        Operation<Void> original, @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequencyR) {
        r$postChange1(instance, receiver, single, assembly, craftingFrequencyR, original);
    }

    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;postChange(Lappeng/api/storage/data/IAEItemStack;Lappeng/api/networking/security/BaseActionSource;)V",
            ordinal = 2))
    private void postChangeR2(CraftingCPUCluster instance, IAEItemStack receiver, BaseActionSource single,
        Operation<Void> original, @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequencyR) {
        r$postChange(instance, receiver, single, assembly, craftingFrequencyR, original);
    }

    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;postChange(Lappeng/api/storage/data/IAEItemStack;Lappeng/api/networking/security/BaseActionSource;)V",
            ordinal = 0))
    private void postChangeR0(CraftingCPUCluster instance, IAEItemStack receiver, BaseActionSource single,
        Operation<Void> original, @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequencyR) {
        r$postChange(instance, receiver, single, assembly, craftingFrequencyR, original);
    }

    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;postChange(Lappeng/api/storage/data/IAEItemStack;Lappeng/api/networking/security/BaseActionSource;)V",
            ordinal = 3))
    private void postChangeR3(CraftingCPUCluster instance, IAEItemStack receiver, BaseActionSource single,
        Operation<Void> original, @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequencyR) {
        r$postChange1(instance, receiver, single, assembly, craftingFrequencyR, original);
    }

    /**
     * 将输出产物倍增到正确值加入到等待合成列表
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/storage/data/IItemList;add(Lappeng/api/storage/data/IAEStack;)V",
            ordinal = 0))
    private void addR(IItemList<IAEItemStack> instance, IAEStack<IAEItemStack> iaeStack, Operation<Void> original,
        @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequency) {
        if (assembly.get()) {
            iaeStack.setStackSize(iaeStack.getStackSize() * craftingFrequency.get());
        }
        original.call(instance, iaeStack);
    }

    /**
     * 将输出产物倍增到正确值加入到等待合成列表
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;postCraftingStatusChange(Lappeng/api/storage/data/IAEItemStack;)V",
            ordinal = 0))
    private void postCraftingStatusChangeR(CraftingCPUCluster instance, IAEItemStack iaeStack, Operation<Void> original,
        @Share("snl$assembly") LocalBooleanRef assembly,
        @Share("snl$craftingFrequency") LocalLongRef craftingFrequency) {
        if (assembly.get()) {
            iaeStack.setStackSize(iaeStack.getStackSize() * craftingFrequency.get());
        }
        original.call(instance, iaeStack);
    }

    /**
     * 重定向物品数量通过检查
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "FIELD",
            opcode = Opcodes.GETFIELD,
            target = "Lnet/minecraft/item/ItemStack;stackSize:I",
            remap = true))
    private int getCountR(ItemStack instance, Operation<Integer> original,
        @Share("snl$assembly") LocalBooleanRef assembly) {
        if (assembly.get()) return 1;
        else return original.call(instance);
    }

    /**
     * 重定向物品数量通过检查
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
     * 正确的消耗并行处理单元数量
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

    @Mixin(targets = "appeng.me.cluster.implementations.CraftingCPUCluster$TaskProgress", remap = false)
    public interface AccessorTaskProgress {

        @Accessor
        long getValue();

        @Accessor
        void setValue(long value);
    }
}
