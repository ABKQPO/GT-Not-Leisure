package com.science.gtnl.mixins.late.mouseTweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.client.metoolbox.ToolboxClient;

@Pseudo
@Mixin(targets = "yalter.mousetweaks.DeobfuscationLayer", remap = false)
public abstract class MixinToolboxMouseTweaks {

    @Inject(method = "windowClick(IIII)V", at = @At("HEAD"), cancellable = true, require = 1)
    private static void gtnl$shiftClickToToolbox(int windowId, int slotNumber, int mouseButton, int clickMode,
        CallbackInfo ci) {
        if (clickMode != 1) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || !(mc.currentScreen instanceof GuiContainer gui)
            || gui.inventorySlots == null
            || gui.inventorySlots != mc.thePlayer.openContainer
            || gui.inventorySlots.windowId != windowId
            || slotNumber < 0
            || slotNumber >= gui.inventorySlots.inventorySlots.size()) return;
        if (ToolboxClient.shiftClickToME(gui.inventorySlots.getSlot(slotNumber), mouseButton, clickMode)) ci.cancel();
    }
}
