package com.science.gtnl.mixins.late.appliedEnergistics.quamtumComputer;

import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.machine.multiblock.QuantumComputer;
import com.science.gtnl.utils.ECraftingCPUCluster;
import com.science.gtnl.utils.EQuantumComputerCPUStatus;

import appeng.api.networking.crafting.ICraftingCPU;
import appeng.container.implementations.CraftingCPUStatus;

@Mixin(value = CraftingCPUStatus.class, remap = false)
public class MixinCraftingCPUStatus implements EQuantumComputerCPUStatus {

    @Unique
    private byte gtnl$cpuType = EQuantumComputerCPUStatus.NORMAL_CPU;

    @Inject(method = "<init>(Lappeng/api/networking/crafting/ICraftingCPU;I)V", at = @At("RETURN"), require = 1)
    private void injectInit(final ICraftingCPU cluster, final int serial, final CallbackInfo ci) {
        if (!(cluster instanceof ECraftingCPUCluster eCluster)) {
            return;
        }

        final QuantumComputer owner = eCluster.getVirtualCPUOwner();
        if (owner == null) {
            return;
        }

        this.gtnl$cpuType = owner.isVirtualCPU(cluster) ? EQuantumComputerCPUStatus.VIRTUAL_CPU
            : EQuantumComputerCPUStatus.SPLIT_CPU;
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("RETURN"), require = 1)
    private void injectInit(final NBTTagCompound tag, final CallbackInfo ci) {
        final byte cpuType = tag.getByte("gtnlQuantumComputerCPU");
        this.gtnl$cpuType = cpuType == EQuantumComputerCPUStatus.VIRTUAL_CPU
            || cpuType == EQuantumComputerCPUStatus.SPLIT_CPU ? cpuType : EQuantumComputerCPUStatus.NORMAL_CPU;
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"), require = 1)
    private void injectWriteToNBT(final NBTTagCompound tag, final CallbackInfo ci) {
        if (this.gtnl$cpuType == EQuantumComputerCPUStatus.NORMAL_CPU) {
            return;
        }

        tag.setByte("gtnlQuantumComputerCPU", this.gtnl$cpuType);
    }

    @Override
    public byte ec$getCPUType() {
        return this.gtnl$cpuType;
    }
}
