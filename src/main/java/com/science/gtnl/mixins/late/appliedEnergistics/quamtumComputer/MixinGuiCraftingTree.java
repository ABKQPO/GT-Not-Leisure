package com.science.gtnl.mixins.late.appliedEnergistics.quamtumComputer;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.client.quantum.CraftingCycleIndex;
import com.science.gtnl.client.quantum.CraftingCycleRings;
import com.science.gtnl.client.quantum.CraftingRingView;
import com.science.gtnl.client.quantum.CraftingTreeContraction;
import com.science.gtnl.client.quantum.CraftingTreeRings;
import com.science.gtnl.client.quantum.ICraftingTreeCycles;
import com.science.gtnl.client.quantum.RecursivePatternButton;
import com.science.gtnl.client.quantum.RecursivePatternReport;
import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder.Cycle;

import appeng.api.storage.data.IAEStack;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiCraftingTree;
import appeng.core.localization.ColorUtils;
import appeng.crafting.v2.CraftingRequest;
import appeng.crafting.v2.resolvers.CraftableItemResolver.CraftFromPatternTask;
import appeng.crafting.v2.resolvers.CraftingTask;

/**
 * Contracts each crafting cycle of the tree into the request it is entered by, marked with a ring, with what the cycle
 * takes from outside under it, and marks the crafts using the patterns of the cycles found for the request. Clicking a
 * cycle, or a craft of a cycle found, shows the cycle as a ring in place of the tree. In a ring a click folds as in the
 * tree, or opens the ring of what is in a cycle of its own, and a shift-click goes back to the node in the tree.
 */
@Mixin(value = GuiCraftingTree.class, remap = false)
public abstract class MixinGuiCraftingTree implements ICraftingTreeCycles {

    @Unique
    private static final long GTNL$FOCUS_MILLIS = 2000;

    @Shadow
    private CraftingRequest request;

    @Shadow
    private String search;

    @Unique
    private Map<Object, CraftingTreeRings.Ring> gtnl$rings = Map.of();

    @Unique
    private CraftingRequest gtnl$foldedRequest;

    @Unique
    private CraftingTreeContraction gtnl$contraction;

    @Unique
    private CraftingCycleIndex gtnl$index;

    // Per craft of the tree, those of its cycles too, the cycle pattern it uses
    @Unique
    private Map<Object, CraftingCycleIndex.Match> gtnl$matches;

    // The rings open, each opened from the one before
    @Unique
    private final List<CraftingRingView> gtnl$views = new ArrayList<>();

    @Unique
    private boolean gtnl$settingRequest;

    @Unique
    private Object gtnl$focus;

    @Unique
    private long gtnl$focusUntil;

    @Unique
    private GuiCraftingTree gtnl$tree() {
        return (GuiCraftingTree) (Object) this;
    }

    @Unique
    private @Nullable CraftingRingView gtnl$view() {
        return this.gtnl$views.isEmpty() ? null : this.gtnl$views.get(this.gtnl$views.size() - 1);
    }

    @Inject(method = "setRequest", at = @At("HEAD"))
    private void gtnl$leaveTree(CraftingRequest request, CallbackInfo ci) {
        this.gtnl$settingRequest = true;
        // A new tree has none of the nodes a ring of the tree moved
        if (request != this.request) {
            this.gtnl$views.removeIf(CraftingRingView::ofTree);
            this.gtnl$contraction = null;
            this.gtnl$focus = null;
        }
    }

    @Inject(method = "setRequest", at = @At("TAIL"))
    private void gtnl$contractRings(CraftingRequest request, CallbackInfo ci) {
        try {
            if (request == this.gtnl$foldedRequest) return;
            this.gtnl$foldedRequest = request;
            var tree = gtnl$tree();
            this.gtnl$rings = CraftingTreeRings.fold(tree);
            this.gtnl$matches = null;
            if (this.gtnl$rings.isEmpty()) return;
            this.gtnl$contraction = CraftingTreeContraction.of(tree, this.gtnl$rings);
            CraftingTreeRings.rebuildLayout(tree);
            // What was found was found among the nodes now taken out
            tree.updateSearchGoToList(this.search);
        } finally {
            this.gtnl$settingRequest = false;
        }
    }

    @Inject(method = "rebuildLayout", at = @At("HEAD"), cancellable = true)
    private void gtnl$layoutContracted(CallbackInfo ci) {
        if (this.gtnl$contraction == null) return;
        this.gtnl$contraction.layout(gtnl$tree());
        ci.cancel();
    }

    @Override
    public void gtnl$setCycles(CraftingCycleIndex index) {
        this.gtnl$index = index;
        this.gtnl$matches = null;
    }

    @Unique
    private Map<Object, CraftingCycleIndex.Match> gtnl$matches() {
        if (this.gtnl$matches == null) {
            this.gtnl$matches = new IdentityHashMap<>();
            if (this.gtnl$index != null && this.request != null) {
                var nodes = this.gtnl$contraction != null ? this.gtnl$contraction.allNodes()
                    : CraftingTreeRings.allNodes(gtnl$tree());
                for (var node : nodes) {
                    if (!CraftingTreeRings.isTask(node)
                        || !(CraftingTreeRings.task(node) instanceof CraftFromPatternTask craft)) continue;
                    var match = this.gtnl$index.match(craft.pattern);
                    if (match != null) this.gtnl$matches.put(node, match);
                }
            }
        }
        return this.gtnl$matches;
    }

    @Override
    public @Nullable CraftingRingView gtnl$ringView() {
        return gtnl$view();
    }

    @Override
    public void gtnl$closeRingView() {
        if (this.gtnl$views.isEmpty()) return;
        this.gtnl$views.remove(this.gtnl$views.size() - 1)
            .close();
        var outer = gtnl$view();
        if (outer != null) outer.refresh();
    }

    @Unique
    private void gtnl$closeAllRings() {
        while (!this.gtnl$views.isEmpty()) {
            this.gtnl$views.remove(this.gtnl$views.size() - 1)
                .close();
        }
    }

    @Override
    public boolean gtnl$openCycle(Cycle cycle, @Nullable IAEStack<?> start) {
        if (this.gtnl$index == null) return false;
        var world = Minecraft.getMinecraft().theWorld;
        var built = world == null ? null : CraftingCycleRings.build(gtnl$tree(), this.gtnl$index, cycle, start, world);
        if (built == null) return false;
        this.gtnl$views.add(new CraftingRingView(gtnl$tree(), built));
        return true;
    }

    /**
     * What opens from a node of the ring shown, as a cycle of its own: a cycle of the tree it enters, or a cycle found
     * that what it asks for is in, other than the one shown.
     */
    @Unique
    private @Nullable Runnable gtnl$nested(Object node) {
        var view = gtnl$view();
        if (view == null || !view.contains(node)
            || node == view.ring()
                .entry()
            || !CraftingTreeRings.isRequest(node)) return null;
        var ring = this.gtnl$rings.get(node);
        if (ring != null) return () -> this.gtnl$views.add(new CraftingRingView(gtnl$tree(), ring));
        if (this.gtnl$index == null || view.ring()
            .members()
            .contains(node)) return null;
        var stack = CraftingTreeRings.request(node).stack;
        var shown = view.cycle();
        var entryStack = CraftingTreeRings.request(
            view.ring()
                .entry()).stack;
        for (var cycle : this.gtnl$index.cyclesWith(stack)) {
            if (cycle == shown || shown == null && cycle.items()
                .stream()
                .anyMatch(entryStack::isSameType)) continue;
            return () -> gtnl$openCycle(cycle, stack);
        }
        return null;
    }

    @Unique
    private CraftingCycleIndex.Match gtnl$matchOf(Object node) {
        var match = gtnl$matches().get(node);
        var view = gtnl$view();
        if (match == null && view != null) match = view.matches()
            .get(node);
        return match;
    }

    @Override
    public List<CraftingCycleIndex.Match> gtnl$hoveredPatterns() {
        var node = CraftingTreeRings.tooltipNode(gtnl$tree());
        var view = gtnl$view();
        if (node != null) {
            var match = gtnl$matchOf(node);
            if (match != null) return List.of(match);
            var ring = this.gtnl$rings.get(node);
            if (ring != null) return gtnl$matchesOf(ring.members());
        }
        if (view != null) {
            return gtnl$matchesOf(
                view.ring()
                    .members());
        }
        return List.of();
    }

    @Unique
    private List<CraftingCycleIndex.Match> gtnl$matchesOf(List<Object> nodes) {
        var result = new ArrayList<CraftingCycleIndex.Match>();
        for (var node : nodes) {
            var match = gtnl$matchOf(node);
            if (match != null && result.stream()
                .noneMatch(m -> m.location() == match.location())) {
                result.add(match);
            }
        }
        return result;
    }

    @Inject(method = "draw(IIZ)V", at = @At("HEAD"), cancellable = true)
    private void gtnl$drawRing(int guiMouseX, int guiMouseY, boolean inScreenshotMode, CallbackInfo ci) {
        var view = gtnl$view();
        if (view == null || inScreenshotMode) return;
        view.draw(guiMouseX, guiMouseY, () -> gtnl$drawMarks(view.nodes()));
        ci.cancel();
    }

    @Inject(
        method = "draw(IIZ)V",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V", ordinal = 0))
    private void gtnl$markTree(int guiMouseX, int guiMouseY, boolean inScreenshotMode, CallbackInfo ci) {
        gtnl$drawMarks(null);
    }

    // The cycles, the crafts of cycles found, and the node gone back to from a ring
    @Unique
    private void gtnl$drawMarks(@Nullable List<Object> shown) {
        var minecraft = Minecraft.getMinecraft();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1, 1, 1, 1);
        if (shown == null) {
            for (var entry : this.gtnl$rings.keySet()) {
                if (!CraftingTreeRings.visible(entry)) continue;
                RecursivePatternButton
                    .drawIcon(minecraft, CraftingTreeRings.x(entry) + 11, CraftingTreeRings.y(entry) - 5, 9, 300);
            }
        } else {
            for (var node : shown) {
                if (gtnl$nested(node) == null) continue;
                RecursivePatternButton
                    .drawIcon(minecraft, CraftingTreeRings.x(node) + 11, CraftingTreeRings.y(node) - 5, 9, 300);
            }
        }
        var view = gtnl$view();
        var matches = view != null && !view.ofTree() ? view.matches() : gtnl$matches();
        for (var entry : matches.entrySet()) {
            var node = entry.getKey();
            if (shown == null ? !CraftingTreeRings.visible(node) : !shown.contains(node)) continue;
            if (entry.getValue()
                .blocking()) GL11.glColor4f(1, 0.4f, 0.4f, 1);
            RecursivePatternButton
                .drawIcon(minecraft, CraftingTreeRings.x(node) - 5, CraftingTreeRings.y(node) - 5, 8, 300);
            GL11.glColor4f(1, 1, 1, 1);
        }
        var focus = this.gtnl$focus;
        if (focus != null && shown == null) {
            if (System.currentTimeMillis() > this.gtnl$focusUntil) {
                this.gtnl$focus = null;
            } else if (CraftingTreeRings.visible(focus)) {
                GL11.glPushMatrix();
                GL11.glTranslatef(0, 0, 250);
                CraftingTreeRings.drawSlotOutline(
                    gtnl$tree(),
                    CraftingTreeRings.x(focus),
                    CraftingTreeRings.y(focus),
                    ColorUtils.searchGoToHighlight.getColor(),
                    CraftingTreeRings.isTask(focus));
                GL11.glPopMatrix();
            }
        }
        GL11.glColor4f(1, 1, 1, 1);
    }

    @Inject(method = "getNodeAt", at = @At("HEAD"), cancellable = true)
    private void gtnl$nodeInRing(int guiMouseX, int guiMouseY, CallbackInfoReturnable<Object> cir) {
        var view = gtnl$view();
        if (view != null) cir.setReturnValue(view.nodeAt(guiMouseX, guiMouseY));
    }

    @Inject(method = "drawTooltip", at = @At("HEAD"), cancellable = true)
    private void gtnl$cycleTooltip(int guiMouseX, int guiMouseY, CallbackInfo ci) {
        var tree = gtnl$tree();
        var node = CraftingTreeRings.tooltipNode(tree);
        if (node == null) return;
        var view = gtnl$view();
        var extra = new StringBuilder();
        var ring = view == null || view.ring()
            .entry() != node ? this.gtnl$rings.get(node) : null;
        if (ring != null) {
            extra.append('\n')
                .append(
                    StatCollector.translateToLocalFormatted(
                        "gtnl.gui.crafting_ring.tooltip",
                        ring.crafts(),
                        ring.backEdges()
                            .size()))
                .append('\n')
                .append(StatCollector.translateToLocal("gtnl.gui.crafting_ring.open"));
            if (view == null) {
                extra.append('\n')
                    .append(StatCollector.translateToLocal("gtnl.gui.crafting_ring.fold_inputs"));
            }
        } else if (gtnl$nested(node) != null) {
            extra.append('\n')
                .append(StatCollector.translateToLocal("gtnl.gui.crafting_ring.nested"))
                .append('\n')
                .append(StatCollector.translateToLocal("gtnl.gui.crafting_ring.open"));
        }
        var match = gtnl$matchOf(node);
        if (match != null) {
            extra.append('\n')
                .append(
                    StatCollector.translateToLocalFormatted(
                        match.cycle()
                            .netOutput() ? "gtnl.gui.recursive_patterns.tooltip.net"
                                : "gtnl.gui.recursive_patterns.tooltip.no_net",
                        match.number()))
                .append('\n')
                .append(
                    StatCollector.translateToLocalFormatted(
                        "gtnl.gui.recursive_patterns.tooltip.machines",
                        match.location()
                            .machines()
                            .size(),
                        RecursivePatternReport.highlightKeyName()));
            if (view == null) {
                extra.append('\n')
                    .append(StatCollector.translateToLocal("gtnl.gui.crafting_ring.open"));
            }
        }
        if (view != null && view.contains(node)) {
            int folded = view.foldedCrafts(node);
            if (folded > 0) {
                extra.append('\n')
                    .append(StatCollector.translateToLocalFormatted("gtnl.gui.crafting_ring.folded", folded));
            }
            extra.append('\n')
                .append(StatCollector.translateToLocal("gtnl.gui.crafting_ring.locate"));
        }
        if (extra.length() == 0) return;
        var text = CraftingTreeRings.isRequest(node) ? CraftingTreeRings.request(node)
            .getTooltipText() : ((CraftingTask) CraftingTreeRings.task(node)).getTooltipText();
        ((AEBaseGui) CraftingTreeRings.parentGui(tree)).drawTooltip(guiMouseX, guiMouseY, text + extra);
        ci.cancel();
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void gtnl$clickRing(int guiMouseX, int guiMouseY, int button, CallbackInfo ci) {
        if (button != 0) return;
        var tree = gtnl$tree();
        var view = gtnl$view();
        if (view != null) {
            tree.mouseDragged(guiMouseX, guiMouseY);
            var pressed = CraftingTreeRings.pressedNode(tree);
            CraftingTreeRings.releasePress(tree);
            if (pressed != null && !CraftingTreeRings.dragging(tree) && pressed == view.nodeAt(guiMouseX, guiMouseY)) {
                var nested = gtnl$nested(pressed);
                if (GuiScreen.isShiftKeyDown()) gtnl$locate(pressed);
                else if (nested != null) nested.run();
                else view.toggle(pressed);
            }
            ci.cancel();
            return;
        }
        var pressed = CraftingTreeRings.pressedNode(tree);
        if (pressed == null || CraftingTreeRings.dragging(tree)
            || pressed != CraftingTreeRings.nodeAt(tree, guiMouseX, guiMouseY)) {
            return;
        }
        var ring = this.gtnl$rings.get(pressed);
        var match = gtnl$matches().get(pressed);
        if (ring != null) {
            // A shift-click folds what the cycle takes, as a click does in the tree
            if (GuiScreen.isShiftKeyDown()) return;
            CraftingTreeRings.releasePress(tree);
            this.gtnl$views.add(new CraftingRingView(tree, ring));
            ci.cancel();
        } else if (match != null) {
            // The tree has no way back for it to show, so the ring is made of the cycle found
            var parent = CraftingTreeRings.parent(pressed);
            var start = parent != null && CraftingTreeRings.isRequest(parent) ? CraftingTreeRings.request(parent).stack
                : null;
            CraftingTreeRings.releasePress(tree);
            gtnl$openCycle(match.cycle(), start);
            ci.cancel();
        }
    }

    // Back to the tree, opened down to the node, or for a craft not in the tree, its machines
    @Unique
    private void gtnl$locate(Object node) {
        var view = gtnl$view();
        if (view != null && !view.ofTree()) {
            var match = view.matches()
                .get(node);
            if (match == null) return;
            Object inTree = null;
            for (var entry : gtnl$matches().entrySet()) {
                if (entry.getValue()
                    .location() == match.location()) {
                    inTree = entry.getKey();
                    break;
                }
            }
            if (inTree == null) {
                RecursivePatternReport.highlight(Minecraft.getMinecraft(), this.gtnl$index.request(), List.of(match));
                return;
            }
            node = inTree;
        }
        gtnl$closeAllRings();
        var tree = gtnl$tree();
        // A node of a cycle is in the tree as the cycle
        var cycleNode = this.gtnl$contraction == null ? null : this.gtnl$contraction.cycleNodeOf(node);
        if (cycleNode != null) node = cycleNode;
        for (Object ancestor = gtnl$parentOf(node); ancestor != null; ancestor = gtnl$parentOf(ancestor)) {
            CraftingTreeRings.setCollapsed(ancestor, false);
        }
        CraftingTreeRings.rebuildLayout(tree);
        float zoom = CraftingTreeRings.zoom(tree);
        tree.scrollX = CraftingTreeRings.x(node) + 8 - tree.widgetW / zoom / 2;
        tree.scrollY = CraftingTreeRings.y(node) + 8 - tree.widgetH / zoom / 2;
        this.gtnl$focus = node;
        this.gtnl$focusUntil = System.currentTimeMillis() + GTNL$FOCUS_MILLIS;
    }

    @Unique
    private @Nullable Object gtnl$parentOf(Object node) {
        return this.gtnl$contraction != null ? this.gtnl$contraction.parentOf(node) : CraftingTreeRings.parent(node);
    }

    @Inject(method = { "hideAvailable", "updateSearchGoToList", "searchGoTo", "saveScreenshot" }, at = @At("HEAD"))
    private void gtnl$backToTree(CallbackInfo ci) {
        if (!this.gtnl$settingRequest) gtnl$closeAllRings();
    }
}
