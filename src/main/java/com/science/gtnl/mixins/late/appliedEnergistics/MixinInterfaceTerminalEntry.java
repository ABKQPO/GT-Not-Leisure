package com.science.gtnl.mixins.late.appliedEnergistics;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.Utils;

import appeng.api.storage.data.IAEStackType;
import appeng.client.gui.implementations.GuiInterfaceTerminal;

@Mixin(targets = "appeng.client.gui.implementations.GuiInterfaceTerminal$InterfaceTerminalEntry", remap = false)
public abstract class MixinInterfaceTerminalEntry {

    @Shadow
    String dispName;

    @Shadow
    ItemStack selfRep;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gtnl$onInit(GuiInterfaceTerminal init, long id, String name, String suffix, int rows, int rowSize,
        int numSlots, boolean online, boolean p2pOutput, IAEStackType<?>[] supportedStackTypes, int priority,
        CallbackInfo ci) {
        if (!MainConfig.machine.enableHatchInterfaceTerminalEnhance) return;
        if (name == null) return;
        this.dispName = Utils.getExtraInterfaceName(name);
    }

    @Redirect(
        method = "mouseClicked",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getDisplayName()Ljava/lang/String;",
            remap = true))
    private String gtnl$getHighlightName(ItemStack itemStack) {
        if (itemStack != null) return itemStack.getDisplayName();
        if (selfRep != null) return selfRep.getDisplayName();
        return "";
    }

}
