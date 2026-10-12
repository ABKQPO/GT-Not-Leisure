package com.science.gtnl.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.RestorePreviousGuiPacket;

import appeng.container.AEBaseContainer;
import appeng.container.PrimaryGui;
import appeng.container.interfaces.IContainerSubGui;

public class PreviousContainerPrimaryGui extends PrimaryGui {

    private final Container previousContainer;

    public PreviousContainerPrimaryGui(Container previousContainer, ItemStack wirelessTerminal) {
        super(null, wirelessTerminal.copy(), null, ForgeDirection.UNKNOWN);
        this.previousContainer = previousContainer;
    }

    @Override
    public void open(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP playerMP)) {
            ScienceNotLeisure.LOG.error("Cannot restore the previous container for non-server player {}", player);
            return;
        }
        if (this.previousContainer == null || this.previousContainer instanceof AEBaseContainer) {
            ScienceNotLeisure.LOG.error(
                "Cannot restore invalid previous container {} for player {}",
                this.previousContainer,
                playerMP.getCommandSenderName());
            return;
        }
        if (!(playerMP.openContainer instanceof AEBaseContainer current)
            || !(playerMP.openContainer instanceof IContainerSubGui)
            || current.getPrimaryGui() != this) {
            ScienceNotLeisure.LOG.error(
                "Cannot restore previous container {} from invalid current container {} for player {}",
                this.previousContainer.getClass()
                    .getName(),
                playerMP.openContainer,
                playerMP.getCommandSenderName());
            return;
        }
        if (this.previousContainer instanceof ContainerPlayer
            && this.previousContainer != playerMP.inventoryContainer) {
            ScienceNotLeisure.LOG.error(
                "Cannot restore foreign player container {} for player {}",
                this.previousContainer,
                playerMP.getCommandSenderName());
            return;
        }

        if (this.previousContainer instanceof ContainerPlayer) {
            playerMP.closeContainer();
        } else {
            int previousWindowId = this.previousContainer.windowId;
            playerMP.closeContainer();
            if (this.previousContainer.windowId != previousWindowId) {
                ScienceNotLeisure.LOG.error(
                    "Previous container window ID changed from {} to {} while closing AE sub GUI for player {}",
                    previousWindowId,
                    this.previousContainer.windowId,
                    playerMP.getCommandSenderName());
                return;
            }
            playerMP.openContainer = this.previousContainer;
        }

        ScienceNotLeisure.network.sendTo(new RestorePreviousGuiPacket(), playerMP);
    }
}
