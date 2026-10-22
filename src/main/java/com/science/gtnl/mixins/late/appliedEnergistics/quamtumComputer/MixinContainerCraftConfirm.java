package com.science.gtnl.mixins.late.appliedEnergistics.quamtumComputer;

import net.minecraft.entity.player.EntityPlayerMP;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.crafting.quantum.ae2.IQuantumCraftConfirm;
import com.science.gtnl.common.crafting.quantum.ae2.QuantumCraftingJob;
import com.science.gtnl.common.crafting.quantum.ae2.QuantumCraftingTree;
import com.science.gtnl.common.packet.QuantumCraftConfirmPacket;
import com.science.gtnl.utils.EQuantumComputerCPUStatus;

import appeng.api.networking.crafting.ICraftingJob;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.container.implementations.CraftingCPUStatus;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketCraftingTreeData;

@Mixin(value = ContainerCraftConfirm.class, remap = false)
public abstract class MixinContainerCraftConfirm implements IQuantumCraftConfirm {

    @Shadow
    private ICraftingJob result;

    @Unique
    private boolean gtnl$quantumJob;

    @Inject(
        method = "detectAndSendChanges",
        at = @At(
            value = "FIELD",
            target = "Lappeng/container/implementations/ContainerCraftConfirm;result:Lappeng/api/networking/crafting/ICraftingJob;",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER),
        require = 1)
    private void gtnl$syncQuantumJob(CallbackInfo ci) {
        final boolean quantumJob = this.result instanceof QuantumCraftingJob;
        if (!(((AEBaseContainer) (Object) this).getPlayerInv().player instanceof EntityPlayerMP player)) return;

        if (quantumJob != this.gtnl$quantumJob) {
            this.gtnl$quantumJob = quantumJob;
            ScienceNotLeisure.network.sendTo(new QuantumCraftConfirmPacket(quantumJob), player);
        }
        // AE2 only sends the crafting tree of its own V2 jobs
        if (this.result instanceof QuantumCraftingJob<?>job) {
            for (PacketCraftingTreeData packet : QuantumCraftingTree.createChunks(player.worldObj, job)) {
                NetworkHandler.instance.sendTo(packet, player);
            }
        }
    }

    @Inject(method = "cpuMatches", at = @At("HEAD"), cancellable = true, require = 1)
    private void gtnl$onlyQuantumCPUs(CraftingCPUStatus c, CallbackInfoReturnable<Boolean> cir) {
        if (this.gtnl$quantumJob
            && ((EQuantumComputerCPUStatus) c).ec$getCPUType() == EQuantumComputerCPUStatus.NORMAL_CPU) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean gtnl$isQuantumJob() {
        return this.gtnl$quantumJob;
    }

    @Override
    public void gtnl$setQuantumJob(boolean quantumJob) {
        this.gtnl$quantumJob = quantumJob;
    }
}
