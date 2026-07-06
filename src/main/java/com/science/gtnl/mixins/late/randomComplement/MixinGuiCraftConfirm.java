package com.science.gtnl.mixins.late.randomComplement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.client.GTNLInputHandler;

import appeng.client.gui.implementations.GuiCraftConfirm;
import appeng.client.gui.widgets.GuiAeButton;

@Mixin(value = GuiCraftConfirm.class, remap = false)
public abstract class MixinGuiCraftConfirm {

    @Shadow
    private GuiAeButton start;

    @Shadow
    private GuiAeButton startWithFollow;

    // TODO: Restore this redirect if AE2 reintroduces switchToOriginalGUI.
    // @Redirect(
    // method = "actionPerformed",
    // at = @At(
    // value = "INVOKE",
    // target = "Lappeng/client/gui/implementations/GuiCraftConfirm;switchToOriginalGUI()V"))
    // public void onActionPerformed0(GuiCraftConfirm instance) {
    // GuiScreen oldGui;
    // if ((oldGui = GTNLInputHandler.LAST_GUI_SCREEN) != null) {
    // ScienceNotLeisure.network.sendToServer(new ContainerRollBACK());
    // return;
    // }
    // this.switchToOriginalGUI();
    // }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    public void onActionPerformed1(GuiButton btn, CallbackInfo ci) {
        if (btn == this.start || btn == this.startWithFollow) {
            GuiScreen oldGui;
            if ((oldGui = GTNLInputHandler.LAST_GUI_SCREEN) != null) {
                GTNLInputHandler.DELAY_METHOD = () -> Minecraft.getMinecraft()
                    .displayGuiScreen(oldGui);
            }
        }
    }

}
