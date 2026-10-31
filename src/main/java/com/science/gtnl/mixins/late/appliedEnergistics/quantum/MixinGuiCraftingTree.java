package com.science.gtnl.mixins.late.appliedEnergistics.quantum;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.client.quantum.CraftingCycleIndex;
import com.science.gtnl.client.quantum.CraftingRingView;
import com.science.gtnl.client.quantum.CraftingTreeCycleController;
import com.science.gtnl.client.quantum.ICraftingTreeCycles;
import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder.Cycle;

import appeng.api.storage.data.IAEStack;
import appeng.client.gui.widgets.GuiCraftingTree;
import appeng.crafting.v2.CraftingRequest;

@Mixin(value = GuiCraftingTree.class, remap = false)
public abstract class MixinGuiCraftingTree implements ICraftingTreeCycles {

    @Shadow
    private CraftingRequest request;

    @Shadow
    private String search;

    @Unique
    private final CraftingTreeCycleController gtnl$cycles = new CraftingTreeCycleController(
        (GuiCraftingTree) (Object) this,
        () -> this.request);

    @Inject(method = "setRequest", at = @At("HEAD"))
    private void gtnl$leaveTree(CraftingRequest request, CallbackInfo ci) {
        gtnl$cycles.beforeSetRequest(request);
    }

    @Inject(method = "setRequest", at = @At("TAIL"))
    private void gtnl$contractRings(CraftingRequest request, CallbackInfo ci) {
        gtnl$cycles.afterSetRequest(request, this.search);
    }

    @Inject(method = "rebuildLayout", at = @At("HEAD"), cancellable = true)
    private void gtnl$layoutContracted(CallbackInfo ci) {
        if (gtnl$cycles.layoutContracted()) ci.cancel();
    }

    @Override
    public void gtnl$setCycles(CraftingCycleIndex index) {
        gtnl$cycles.setCycles(index);
    }

    @Override
    public @Nullable CraftingRingView gtnl$ringView() {
        return gtnl$cycles.ringView();
    }

    @Override
    public void gtnl$closeRingView() {
        gtnl$cycles.closeRingView();
    }

    @Override
    public boolean gtnl$openCycle(Cycle cycle, @Nullable IAEStack<?> start) {
        return gtnl$cycles.openCycle(cycle, start);
    }

    @Override
    public List<CraftingCycleIndex.Match> gtnl$hoveredPatterns() {
        return gtnl$cycles.hoveredPatterns();
    }

    @Inject(method = "draw(IIZ)V", at = @At("HEAD"), cancellable = true)
    private void gtnl$drawRing(int guiMouseX, int guiMouseY, boolean inScreenshotMode, CallbackInfo ci) {
        if (gtnl$cycles.drawRing(guiMouseX, guiMouseY, inScreenshotMode)) ci.cancel();
    }

    @Inject(
        method = "draw(IIZ)V",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V", ordinal = 0))
    private void gtnl$markTree(int guiMouseX, int guiMouseY, boolean inScreenshotMode, CallbackInfo ci) {
        gtnl$cycles.drawMarks(null);
    }

    @Inject(method = "onMouseWheel", at = @At("HEAD"), cancellable = true)
    private void gtnl$zoomByPixels(int mouseX, int mouseY, int wheel, CallbackInfo ci) {
        if (gtnl$cycles.zoomByPixels(mouseX, mouseY, wheel)) ci.cancel();
    }

    @Inject(method = "getNodeAt", at = @At("HEAD"), cancellable = true)
    private void gtnl$nodeInRing(int guiMouseX, int guiMouseY, CallbackInfoReturnable<Object> cir) {
        var view = gtnl$cycles.ringView();
        if (view != null) cir.setReturnValue(view.nodeAt(guiMouseX, guiMouseY));
    }

    @Inject(method = "drawTooltip", at = @At("HEAD"), cancellable = true)
    private void gtnl$cycleTooltip(int guiMouseX, int guiMouseY, CallbackInfo ci) {
        if (gtnl$cycles.drawCycleTooltip(guiMouseX, guiMouseY)) ci.cancel();
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void gtnl$clickRing(int guiMouseX, int guiMouseY, int button, CallbackInfo ci) {
        if (gtnl$cycles.mouseReleased(guiMouseX, guiMouseY, button)) ci.cancel();
    }

    @Inject(method = { "hideAvailable", "updateSearchGoToList", "searchGoTo", "saveScreenshot" }, at = @At("HEAD"))
    private void gtnl$backToTree(CallbackInfo ci) {
        gtnl$cycles.backToTree();
    }
}
