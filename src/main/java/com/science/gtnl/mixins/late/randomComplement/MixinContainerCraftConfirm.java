package com.science.gtnl.mixins.late.randomComplement;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.ContainerRollBACK;
import com.science.gtnl.utils.RCAEBaseContainer;

import appeng.api.parts.IPart;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerCraftConfirm;

@Mixin(value = ContainerCraftConfirm.class, remap = false)
public abstract class MixinContainerCraftConfirm extends AEBaseContainer implements RCAEBaseContainer {

    public MixinContainerCraftConfirm(InventoryPlayer ip, TileEntity myTile, IPart myPart) {
        super(ip, myTile, myPart);
    }

    @Shadow
    public abstract void switchToOriginalGUI();

    @Redirect(
        method = "startJob(Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/container/implementations/ContainerCraftConfirm;switchToOriginalGUI()V"))
    public void startJob0(ContainerCraftConfirm instance) {
        if (this.rc$getOldContainer() != null) {
            if (this.getPlayerInv().player instanceof EntityPlayerMP entityPlayerMP) {
                var oldContainer = this.rc$getOldContainer();
                if (oldContainer instanceof ContainerPlayer) {
                    entityPlayerMP.closeContainer();
                } else {
                    entityPlayerMP.getNextWindowId();
                    entityPlayerMP.closeContainer();
                    int windowId = entityPlayerMP.currentWindowId;
                    entityPlayerMP.openContainer = oldContainer;
                    entityPlayerMP.openContainer.windowId = windowId;
                }
                ScienceNotLeisure.network.sendTo(new ContainerRollBACK(), entityPlayerMP);
                return;
            }
        }
        this.switchToOriginalGUI();
    }
}
