package com.science.gtnl.mixins.late.randomComplement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLInputHandler;
import com.science.gtnl.common.packet.ContainerRollBACK;

import appeng.client.gui.implementations.GuiAmount;
import appeng.client.gui.implementations.GuiCraftAmount;

@Mixin(value = GuiCraftAmount.class, remap = false)
public abstract class MixinGuiCraftAmount extends GuiAmount {

    public MixinGuiCraftAmount(Container container) {
        super(container);
    }

    @Inject(
        method = "actionPerformed",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/core/sync/network/NetworkHandler;sendToServer(Lappeng/core/sync/AppEngPacket;)V",
            shift = At.Shift.AFTER))
    public void onActionPerformed(GuiButton btn, CallbackInfo ci) {
        if (isShiftKeyDown()) {
            GuiScreen oldGui;
            if ((oldGui = GTNLInputHandler.LAST_GUI_SCREEN) != null) {
                GTNLInputHandler.DELAY_METHOD = () -> Minecraft.getMinecraft()
                    .displayGuiScreen(oldGui);
                ScienceNotLeisure.network.sendToServer(new ContainerRollBACK());
            }
        }
    }
}
