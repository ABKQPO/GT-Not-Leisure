package com.science.gtnl.mixins.late.appliedEnergistics.quantum;

import java.util.List;

import net.minecraft.client.gui.GuiButton;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.quantum.CraftConfirmCycles;
import com.science.gtnl.client.quantum.CraftingCycleIndex;
import com.science.gtnl.client.quantum.ICraftingCycleHost;
import com.science.gtnl.client.quantum.ICraftingTreeCycles;
import com.science.gtnl.client.quantum.RecursivePatternButton;
import com.science.gtnl.client.quantum.RecursivePatternReport;
import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder.Cycle;
import com.science.gtnl.common.packet.RecursivePatternQueryPacket;

import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.GuiSub;
import appeng.client.gui.implementations.GuiCraftConfirm;
import appeng.client.gui.widgets.GuiAeButton;
import appeng.client.gui.widgets.GuiCraftingTree;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.client.gui.widgets.MEGuiTextField;
import appeng.container.implementations.ContainerCraftConfirm;
import appeng.core.localization.ColorUtils;

@Mixin(value = GuiCraftConfirm.class, remap = false)
public abstract class MixinGuiCraftConfirm extends GuiSub implements ICraftingCycleHost {

    @Shadow
    @Final
    private ContainerCraftConfirm ccc;

    @Shadow
    @Final
    private GuiCraftingTree craftingTree;

    @Shadow
    @Final
    private IItemList<IAEStack<?>> missing;

    @Shadow
    @Final
    private IItemList<IAEStack<?>> storage;

    @Shadow
    @Final
    private IItemList<IAEStack<?>> pending;

    @Shadow
    @Final
    private List<IAEStack<?>> filteredVisual;

    @Shadow
    private int tooltip;

    @Shadow
    private int rows;

    @Shadow
    private GuiTabButton switchDisplayMode;

    @Shadow
    private GuiCraftConfirm.DisplayMode displayMode;

    @Shadow
    private GuiAeButton hideAvailable;

    @Shadow
    private MEGuiTextField searchField;

    @Unique
    private RecursivePatternButton gtnl$recursivePatterns;

    @Unique
    private GuiAeButton gtnl$backToTree;

    @Unique
    private boolean gtnl$queried;

    @Unique
    private CraftingCycleIndex gtnl$index;

    // The ring shown was opened from the list, to which leaving it goes back
    @Unique
    private boolean gtnl$ringFromList;

    @Unique
    private boolean gtnl$listMarked;

    public MixinGuiCraftConfirm() {
        super(null);
    }

    @Unique
    private ICraftingTreeCycles gtnl$tree() {
        return (ICraftingTreeCycles) this.craftingTree;
    }

    @Inject(method = "initGui", at = @At("TAIL"), remap = true)
    private void gtnl$addButtons(CallbackInfo ci) {
        this.gtnl$recursivePatterns = new RecursivePatternButton(
            this.guiLeft + this.xSize + 2,
            this.guiTop + 8 + 20 * 3);
        this.buttonList.add(this.gtnl$recursivePatterns);
        this.gtnl$backToTree = CraftConfirmCycles
            .backToTreeButton(this.guiLeft, this.guiTop, this.craftingTree.widgetW, this.fontRendererObj);
        this.buttonList.add(this.gtnl$backToTree);
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true, remap = true)
    private void gtnl$buttons(GuiButton button, CallbackInfo ci) {
        if (button == this.gtnl$recursivePatterns) {
            ScienceNotLeisure.network.sendToServer(new RecursivePatternQueryPacket(true));
            ci.cancel();
        } else if (button == this.gtnl$backToTree) {
            gtnl$leaveRing();
            ci.cancel();
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), remap = true)
    private void gtnl$update(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        var error = this.ccc.getErrorMessage();
        if (!this.gtnl$queried && (this.ccc.getUsedBytes() > 0 || !error.isEmpty())) {
            this.gtnl$queried = true;
            ScienceNotLeisure.network.sendToServer(new RecursivePatternQueryPacket(false));
        }
        if (this.gtnl$recursivePatterns != null)
            this.gtnl$recursivePatterns.setAttention(CraftConfirmCycles.needsAttention(error, this.gtnl$index));
        gtnl$updateRingButtons();
    }

    @Unique
    private void gtnl$updateRingButtons() {
        if (gtnl$tree().gtnl$ringView() == null) this.gtnl$ringFromList = false;
        boolean inRing = this.displayMode == GuiCraftConfirm.DisplayMode.TREE && gtnl$tree().gtnl$ringView() != null;
        if (this.gtnl$backToTree != null) this.gtnl$backToTree.visible = inRing;
        if (this.hideAvailable != null) this.hideAvailable.visible = !inRing;
    }

    @Inject(method = "drawFG", at = @At("TAIL"))
    private void gtnl$ringSummary(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        var view = gtnl$tree().gtnl$ringView();
        if (view == null || this.displayMode != GuiCraftConfirm.DisplayMode.TREE || this.gtnl$backToTree == null) {
            return;
        }
        var text = CraftConfirmCycles.ringSummary(view);
        int x = this.gtnl$backToTree.xPosition - this.guiLeft + this.gtnl$backToTree.width + 4;
        this.fontRendererObj.drawString(text, x, 25, ColorUtils.guiTextColorGray.getColor());
    }

    @Override
    public void gtnl$cyclesFound(IAEStack<?> request, List<Cycle> cycles, boolean manual) {
        var blocking = CraftConfirmCycles.blocking(
            cycles,
            this.ccc.getErrorMessage(),
            this.ccc.isSimulation(),
            this.missing,
            this.storage,
            this.pending);
        this.gtnl$index = CraftingCycleIndex.of(request, cycles, blocking);
        gtnl$tree().gtnl$setCycles(this.gtnl$index);
        RecursivePatternReport.report(this.mc, request, cycles, this.gtnl$index, manual);
    }

    // One ring back, and from the last one back to where it was opened from
    @Unique
    private void gtnl$leaveRing() {
        gtnl$tree().gtnl$closeRingView();
        if (gtnl$tree().gtnl$ringView() != null) return;
        if (this.gtnl$ringFromList && this.displayMode == GuiCraftConfirm.DisplayMode.TREE) {
            this.actionPerformed(this.switchDisplayMode);
        }
        this.gtnl$ringFromList = false;
    }

    // What the mouse is over in the list
    @Unique
    private IAEStack<?> gtnl$listStack() {
        if (this.displayMode != GuiCraftConfirm.DisplayMode.LIST || this.tooltip < 0 || this.getScrollBar() == null) {
            return null;
        }
        int index = this.getScrollBar()
            .getCurrentScroll() * 3 + this.tooltip;
        return index < this.filteredVisual.size() ? this.filteredVisual.get(index) : null;
    }

    @Inject(method = "drawTreeFG", at = @At("HEAD"), cancellable = true)
    private void gtnl$drawRingWithoutTree(int offsetX, int offsetY, int mouseX, int mouseY, CallbackInfo ci) {
        // A ring of a cycle found is shown whether or not there is a tree
        var view = gtnl$tree().gtnl$ringView();
        if (view != null && !view.ofTree()) {
            this.craftingTree.draw(mouseX, mouseY);
            ci.cancel();
        }
    }

    @Inject(method = "drawListFG", at = @At("HEAD"))
    private void gtnl$startList(CallbackInfo ci) {
        this.gtnl$listMarked = false;
    }

    @Inject(
        method = "drawListFG",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/client/gui/implementations/GuiCraftConfirm;drawTooltip(IILjava/lang/String;)V"))
    private void gtnl$markListUnderTooltip(CallbackInfo ci) {
        gtnl$markList();
    }

    @Inject(method = "drawListFG", at = @At("TAIL"))
    private void gtnl$markListWithoutTooltip(CallbackInfo ci) {
        gtnl$markList();
    }

    @Unique
    private void gtnl$markList() {
        if (this.gtnl$listMarked || this.gtnl$index == null || this.getScrollBar() == null) return;
        this.gtnl$listMarked = true;
        int viewStart = this.getScrollBar()
            .getCurrentScroll() * 3;
        CraftConfirmCycles.markList(this.mc, this.gtnl$index, this.filteredVisual, viewStart, this.rows);
    }

    @ModifyArg(
        method = "drawListFG",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/client/gui/implementations/GuiCraftConfirm;drawTooltip(IILjava/lang/String;)V"),
        index = 2)
    private String gtnl$listTooltip(String text) {
        return CraftConfirmCycles.listTooltip(text, gtnl$listStack(), this.gtnl$index);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, remap = true)
    private void gtnl$openRingFromList(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (button != 0 || !isShiftKeyDown() || this.gtnl$index == null) return;
        var stack = gtnl$listStack();
        var cycles = stack == null ? List.<Cycle>of() : this.gtnl$index.cyclesWith(stack);
        if (cycles.isEmpty()) return;
        this.actionPerformed(this.switchDisplayMode);
        if (gtnl$tree().gtnl$openCycle(cycles.get(0), stack)) this.gtnl$ringFromList = true;
        else this.actionPerformed(this.switchDisplayMode);
        ci.cancel();
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true, remap = true)
    private void gtnl$keys(char character, int key, CallbackInfo ci) {
        var tree = gtnl$tree();
        if (key == Keyboard.KEY_ESCAPE && tree.gtnl$ringView() != null) {
            gtnl$leaveRing();
            ci.cancel();
            return;
        }
        if (!RecursivePatternReport.isHighlightKey(key) || this.searchField.isFocused()) return;
        CraftConfirmCycles.highlight(this.mc, this.gtnl$index, gtnl$hoveredMatches(tree));
        ci.cancel();
    }

    @Unique
    private List<CraftingCycleIndex.Match> gtnl$hoveredMatches(ICraftingTreeCycles tree) {
        if (this.displayMode == GuiCraftConfirm.DisplayMode.TREE) return tree.gtnl$hoveredPatterns();
        var listStack = gtnl$listStack();
        return listStack != null && this.gtnl$index != null ? this.gtnl$index.matchesFor(listStack)
            : List.<CraftingCycleIndex.Match>of();
    }
}
