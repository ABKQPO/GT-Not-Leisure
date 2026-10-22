package com.science.gtnl.mixins.late.appliedEnergistics.quamtumComputer;

import net.minecraft.client.gui.GuiButton;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.quantum.QuantumCraftingClientConfig;
import com.science.gtnl.client.quantum.QuantumCraftingModeButton;
import com.science.gtnl.common.crafting.quantum.ae2.IQuantumCraftAmountGui;
import com.science.gtnl.common.packet.QuantumCraftingModePacket;

import appeng.client.gui.implementations.GuiAmount;
import appeng.client.gui.implementations.GuiCraftAmount;
import appeng.client.gui.widgets.GuiToggleButton;
import appeng.core.AEConfig;

@Mixin(value = GuiCraftAmount.class, remap = false)
public abstract class MixinGuiCraftAmount extends GuiAmount implements IQuantumCraftAmountGui {

    @Shadow(remap = false)
    private GuiToggleButton liteMode;

    @Shadow(remap = false)
    private boolean isControlButtonPressed;

    @Unique
    private QuantumCraftingModeButton gtnl$quantumMode;

    @Unique
    private boolean gtnl$quantumAvailable;

    public MixinGuiCraftAmount() {
        super(null);
    }

    @Inject(method = "initGui", at = @At("TAIL"), remap = true)
    private void gtnl$addQuantumModeButton(CallbackInfo ci) {
        this.gtnl$quantumMode = new QuantumCraftingModeButton(this.guiLeft - 18, this.guiTop + 48);
        this.gtnl$quantumMode.setState(QuantumCraftingClientConfig.isQuantumCraftingMode());
        this.buttonList.add(this.gtnl$quantumMode);
        this.gtnl$updateQuantumModeVisibility();
        // The answer tells whether the network can run quantum jobs
        ScienceNotLeisure.network.sendToServer(new QuantumCraftingModePacket(this.gtnl$quantumMode.getState()));
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true, remap = true)
    private void gtnl$toggleQuantumMode(GuiButton btn, CallbackInfo ci) {
        if (this.gtnl$quantumMode == null) return;

        if (btn == this.gtnl$quantumMode) {
            final boolean enabled = !this.gtnl$quantumMode.getState();
            if (enabled && AEConfig.instance.getUseLiteCraftingMode()) {
                this.liteMode.setState(AEConfig.instance.toggleUseLiteCraftingMode());
            }
            this.gtnl$setQuantumMode(enabled);
            ci.cancel();
        } else if (btn == this.liteMode && this.gtnl$quantumMode.getState()
            && !AEConfig.instance.getUseLiteCraftingMode()) {
                // Lite mode is about to turn on
                this.gtnl$setQuantumMode(false);
            }
    }

    @Inject(method = "setControlButtonPressed", at = @At("TAIL"))
    private void gtnl$hideQuantumModeWhileEditing(boolean pressed, CallbackInfo ci) {
        this.gtnl$updateQuantumModeVisibility();
    }

    @Override
    public void gtnl$setQuantumAvailable(boolean available) {
        this.gtnl$quantumAvailable = available;
        if (available && this.gtnl$quantumMode != null
            && this.gtnl$quantumMode.getState()
            && AEConfig.instance.getUseLiteCraftingMode()) {
            this.liteMode.setState(AEConfig.instance.toggleUseLiteCraftingMode());
        }
        this.gtnl$updateQuantumModeVisibility();
    }

    @Unique
    private void gtnl$setQuantumMode(boolean enabled) {
        this.gtnl$quantumMode.setState(enabled);
        QuantumCraftingClientConfig.setQuantumCraftingMode(enabled);
        ScienceNotLeisure.network.sendToServer(new QuantumCraftingModePacket(enabled));
    }

    @Unique
    private void gtnl$updateQuantumModeVisibility() {
        if (this.gtnl$quantumMode == null) return;
        final boolean visible = this.gtnl$quantumAvailable && !this.isControlButtonPressed;
        this.gtnl$quantumMode.visible = visible;
        this.gtnl$quantumMode.enabled = visible;
    }
}
