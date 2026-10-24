package com.science.gtnl.mixins.late.appliedEnergistics.quamtumComputer;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.ScienceNotLeisure;
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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Finds the crafting cycles of the request once it is calculated, marks them in the crafting tree and the list, and
 * tells once where the machines of those the plan cannot start because of are. The button lists them all, the highlight
 * key highlights the cycle of what is under the mouse, and shift-clicking an item of a cycle in the list shows the
 * cycle as a ring in the tree view, left by its button or Esc.
 */
@Mixin(value = GuiCraftConfirm.class, remap = false)
public abstract class MixinGuiCraftConfirm extends GuiSub implements ICraftingCycleHost {

    @Unique
    private static final String GTNL$NO_NET_OUTPUT = "gtnl.quantum_crafting.message.CraftingNoNetOutput";

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
        var back = StatCollector.translateToLocal("gtnl.gui.crafting_ring.back");
        this.gtnl$backToTree = new GuiAeButton(
            0,
            this.guiLeft + 9,
            this.guiTop + 20,
            Math.min(this.craftingTree.widgetW, this.fontRendererObj.getStringWidth(back) + 10),
            18,
            back,
            "");
        this.gtnl$backToTree.visible = false;
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
        if (this.gtnl$recursivePatterns != null) {
            this.gtnl$recursivePatterns.setAttention(
                GTNL$NO_NET_OUTPUT.equals(error) || this.gtnl$index != null && !this.gtnl$index.blocking()
                    .isEmpty());
        }
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
        var ring = view.ring();
        var text = StatCollector.translateToLocalFormatted(
            "gtnl.gui.crafting_ring.summary",
            ring.crafts(),
            ring.backEdges()
                .size(),
            ring.externalInputs()
                .size());
        if (view.omitted() > 0) {
            text += StatCollector.translateToLocalFormatted("gtnl.gui.crafting_ring.omitted", view.omitted());
        }
        int x = this.gtnl$backToTree.xPosition - this.guiLeft + this.gtnl$backToTree.width + 4;
        this.fontRendererObj.drawString(text, x, 25, ColorUtils.guiTextColorGray.getColor());
    }

    @Unique
    private static boolean gtnl$has(IItemList<IAEStack<?>> list, IAEStack<?> item) {
        var found = list.findPrecise(item);
        return found != null && (found.getStackSize() > 0 || found.getCountRequestable() > 0);
    }

    /**
     * The cycles the plan cannot start because of: those without net output when that is the error, those short of
     * something of their own, and when the plan cannot start while showing nothing missing, as the lightweight and
     * standard calculations leave a cycle they could not close, those the plan goes through.
     */
    @Unique
    private List<Cycle> gtnl$blocking(List<Cycle> cycles) {
        var error = this.ccc.getErrorMessage();
        boolean noNetOutput = GTNL$NO_NET_OUTPUT.equals(error);
        var blocking = new ObjectArrayList<Cycle>();
        if (!noNetOutput && !this.ccc.isSimulation()) return blocking;
        for (var cycle : cycles) {
            boolean blocks = noNetOutput && !cycle.netOutput();
            for (var item : cycle.items()) {
                if (blocks) break;
                blocks = gtnl$has(this.missing, item);
            }
            if (blocks) blocking.add(cycle);
        }
        if (blocking.isEmpty() && (noNetOutput || this.missing.isEmpty())) {
            for (var cycle : cycles) {
                for (var item : cycle.items()) {
                    if (gtnl$has(this.storage, item) || gtnl$has(this.pending, item)) {
                        blocking.add(cycle);
                        break;
                    }
                }
            }
        }
        if (blocking.isEmpty() && noNetOutput) blocking.addAll(cycles);
        return blocking;
    }

    @Override
    public void gtnl$cyclesFound(IAEStack<?> request, List<Cycle> cycles, boolean manual) {
        var blocking = gtnl$blocking(cycles);
        this.gtnl$index = CraftingCycleIndex.of(request, cycles, blocking);
        gtnl$tree().gtnl$setCycles(this.gtnl$index);
        if (manual) {
            RecursivePatternReport.show(this.mc, request, cycles);
        } else {
            RecursivePatternReport.showBlocking(this.mc, this.gtnl$index);
        }
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
        int viewEnd = Math.min(viewStart + 3 * this.rows, this.filteredVisual.size());
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for (int z = viewStart; z < viewEnd; z++) {
            var stack = this.filteredVisual.get(z);
            if (stack == null || this.gtnl$index.cyclesWith(stack)
                .isEmpty()) continue;
            int x = (z - viewStart) % 3 * 68 + 57;
            int y = (z - viewStart) / 3 * 23 + 22;
            if (this.gtnl$index.blocks(stack)) GL11.glColor4f(1, 0.4f, 0.4f, 1);
            else GL11.glColor4f(1, 1, 1, 1);
            RecursivePatternButton.drawIcon(this.mc, x - 5, y - 5, 8, 300);
        }
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopAttrib();
    }

    @ModifyArg(
        method = "drawListFG",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/client/gui/implementations/GuiCraftConfirm;drawTooltip(IILjava/lang/String;)V"),
        index = 2)
    private String gtnl$listTooltip(String text) {
        var stack = gtnl$listStack();
        if (stack == null || this.gtnl$index == null) return text;
        var cycles = this.gtnl$index.cyclesWith(stack);
        if (cycles.isEmpty()) return text;
        var lines = new StringBuilder(text);
        for (var cycle : cycles) {
            lines.append('\n')
                .append(
                    StatCollector.translateToLocalFormatted(
                        cycle.netOutput() ? "gtnl.gui.recursive_patterns.tooltip.net"
                            : "gtnl.gui.recursive_patterns.tooltip.no_net",
                        this.gtnl$index.cycles()
                            .indexOf(cycle) + 1));
        }
        int machines = 0;
        for (var match : this.gtnl$index.matchesFor(stack)) {
            machines += match.location()
                .machines()
                .size();
        }
        lines.append('\n')
            .append(
                StatCollector.translateToLocalFormatted(
                    "gtnl.gui.recursive_patterns.tooltip.machines",
                    machines,
                    RecursivePatternReport.highlightKeyName()))
            .append('\n')
            .append(StatCollector.translateToLocal("gtnl.gui.crafting_ring.open_list"));
        return lines.toString();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true, remap = true)
    private void gtnl$openRingFromList(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (button != 0 || !isShiftKeyDown() || this.gtnl$index == null) return;
        var stack = gtnl$listStack();
        var cycles = stack == null ? List.<Cycle>of() : this.gtnl$index.cyclesWith(stack);
        if (cycles.isEmpty()) return;
        this.actionPerformed(this.switchDisplayMode);
        if (gtnl$tree().gtnl$openCycle(cycles.get(0), stack)) {
            this.gtnl$ringFromList = true;
        } else {
            this.actionPerformed(this.switchDisplayMode);
        }
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
        var listStack = gtnl$listStack();
        var hovered = this.displayMode == GuiCraftConfirm.DisplayMode.TREE ? tree.gtnl$hoveredPatterns()
            : listStack != null && this.gtnl$index != null ? this.gtnl$index.matchesFor(listStack)
                : List.<CraftingCycleIndex.Match>of();
        if (!hovered.isEmpty()) {
            RecursivePatternReport.highlight(this.mc, this.gtnl$index.request(), hovered);
        } else if (this.gtnl$index != null) {
            RecursivePatternReport.show(this.mc, this.gtnl$index.request(), this.gtnl$index.cycles());
        } else {
            ScienceNotLeisure.network.sendToServer(new RecursivePatternQueryPacket(true));
        }
        ci.cancel();
    }
}
