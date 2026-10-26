package com.science.gtnl.client.quantum;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder.Cycle;

import appeng.api.storage.data.IAEStack;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiCraftingTree;
import appeng.core.localization.ColorUtils;
import appeng.crafting.v2.CraftingRequest;
import appeng.crafting.v2.resolvers.CraftableItemResolver.CraftFromPatternTask;
import appeng.crafting.v2.resolvers.CraftingTask;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

/**
 * Contracts each crafting cycle of the tree into the request it is entered by, marked with a ring, with what the cycle
 * takes from outside under it, and marks the crafts using the patterns of the cycles found for the request. Clicking a
 * cycle, or a craft of a cycle found, shows the cycle as a ring in place of the tree. In a ring a click folds as in the
 * tree, or opens the ring of what is in a cycle of its own, and a shift-click goes back to the node in the tree. One
 * per
 * crafting tree widget, driven by its mixin.
 */
public class CraftingTreeCycleController {

    public static final long FOCUS_MILLIS = 2000;

    private final GuiCraftingTree tree;
    private final Supplier<CraftingRequest> request;
    private Map<Object, CraftingTreeRings.Ring> rings = Map.of();
    private CraftingRequest foldedRequest;
    private CraftingTreeContraction contraction;
    private CraftingCycleIndex index;
    // Per craft of the tree, those of its cycles too, the cycle pattern it uses
    private Map<Object, CraftingCycleIndex.Match> matches;
    // The rings open, each opened from the one before
    private final List<CraftingRingView> views = new ObjectArrayList<>();
    private boolean settingRequest;
    private Object focus;
    private long focusUntil;

    public CraftingTreeCycleController(GuiCraftingTree tree, Supplier<CraftingRequest> request) {
        this.tree = tree;
        this.request = request;
    }

    public @Nullable CraftingRingView ringView() {
        return this.views.isEmpty() ? null : this.views.get(this.views.size() - 1);
    }

    public void beforeSetRequest(CraftingRequest newRequest) {
        this.settingRequest = true;
        // A new tree has none of the nodes a ring of the tree moved
        if (newRequest != this.request.get()) {
            this.views.removeIf(CraftingRingView::ofTree);
            this.contraction = null;
            this.focus = null;
        }
    }

    public void afterSetRequest(CraftingRequest newRequest, String search) {
        try {
            if (newRequest == this.foldedRequest) return;
            this.foldedRequest = newRequest;
            this.rings = CraftingTreeRings.fold(tree);
            this.matches = null;
            if (this.rings.isEmpty()) return;
            this.contraction = CraftingTreeContraction.of(tree, this.rings);
            CraftingTreeRings.rebuildLayout(tree);
            // What was found was found among the nodes now taken out
            tree.updateSearchGoToList(search);
        } finally {
            this.settingRequest = false;
        }
    }

    public boolean layoutContracted() {
        if (this.contraction == null) return false;
        this.contraction.layout(tree);
        return true;
    }

    public void setCycles(CraftingCycleIndex index) {
        this.index = index;
        this.matches = null;
    }

    private Map<Object, CraftingCycleIndex.Match> matches() {
        if (this.matches == null) {
            this.matches = new Reference2ObjectOpenHashMap<>();
            if (this.index != null && this.request.get() != null) {
                var nodes = this.contraction != null ? this.contraction.allNodes() : CraftingTreeRings.allNodes(tree);
                for (var node : nodes) {
                    if (!CraftingTreeRings.isTask(node)
                        || !(CraftingTreeRings.task(node) instanceof CraftFromPatternTask craft)) continue;
                    var match = this.index.match(craft.pattern);
                    if (match != null) this.matches.put(node, match);
                }
            }
        }
        return this.matches;
    }

    public void closeRingView() {
        if (this.views.isEmpty()) return;
        this.views.remove(this.views.size() - 1)
            .close();
        var outer = ringView();
        if (outer != null) outer.refresh();
    }

    private void closeAllRings() {
        while (!this.views.isEmpty()) {
            this.views.remove(this.views.size() - 1)
                .close();
        }
    }

    public boolean openCycle(Cycle cycle, @Nullable IAEStack<?> start) {
        if (this.index == null) return false;
        var world = Minecraft.getMinecraft().theWorld;
        var built = world == null ? null : CraftingCycleRings.build(tree, this.index, cycle, start, world);
        if (built == null) return false;
        this.views.add(new CraftingRingView(tree, built));
        return true;
    }

    /**
     * What opens from a node of the ring shown, as a cycle of its own: a cycle of the tree it enters, or a cycle found
     * that what it asks for is in, other than the one shown.
     */
    private @Nullable Runnable nested(Object node) {
        var view = ringView();
        if (view == null || !view.contains(node)
            || node == view.ring()
                .entry()
            || !CraftingTreeRings.isRequest(node)) return null;
        var ring = this.rings.get(node);
        if (ring != null) return () -> this.views.add(new CraftingRingView(tree, ring));
        if (this.index == null || view.ring()
            .members()
            .contains(node)) return null;
        var stack = CraftingTreeRings.request(node).stack;
        var shown = view.cycle();
        var entryStack = CraftingTreeRings.request(
            view.ring()
                .entry()).stack;
        for (var cycle : this.index.cyclesWith(stack)) {
            if (cycle == shown || shown == null && cycle.items()
                .stream()
                .anyMatch(entryStack::isSameType)) continue;
            return () -> openCycle(cycle, stack);
        }
        return null;
    }

    private CraftingCycleIndex.Match matchOf(Object node) {
        var match = matches().get(node);
        var view = ringView();
        if (match == null && view != null) match = view.matches()
            .get(node);
        return match;
    }

    public List<CraftingCycleIndex.Match> hoveredPatterns() {
        var node = CraftingTreeRings.tooltipNode(tree);
        var view = ringView();
        if (node != null) {
            var match = matchOf(node);
            if (match != null) return List.of(match);
            var ring = this.rings.get(node);
            if (ring != null) return matchesOf(ring.members());
        }
        if (view != null) {
            return matchesOf(
                view.ring()
                    .members());
        }
        return List.of();
    }

    private List<CraftingCycleIndex.Match> matchesOf(List<Object> nodes) {
        var result = new ObjectArrayList<CraftingCycleIndex.Match>();
        for (var node : nodes) {
            var match = matchOf(node);
            if (match != null && result.stream()
                .noneMatch(m -> m.location() == match.location())) {
                result.add(match);
            }
        }
        return result;
    }

    public boolean drawRing(int guiMouseX, int guiMouseY, boolean inScreenshotMode) {
        var view = ringView();
        if (view == null || inScreenshotMode) return false;
        view.draw(guiMouseX, guiMouseY, () -> drawMarks(view.nodes()));
        return true;
    }

    // The cycles, the crafts of cycles found, and the node gone back to from a ring
    public void drawMarks(@Nullable List<Object> shown) {
        var minecraft = Minecraft.getMinecraft();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1, 1, 1, 1);
        if (shown == null) {
            for (var entry : this.rings.keySet()) {
                if (!CraftingTreeRings.visible(entry)) continue;
                RecursivePatternButton
                    .drawIcon(minecraft, CraftingTreeRings.x(entry) + 11, CraftingTreeRings.y(entry) - 5, 9, 300);
            }
        } else {
            for (var node : shown) {
                if (nested(node) == null) continue;
                RecursivePatternButton
                    .drawIcon(minecraft, CraftingTreeRings.x(node) + 11, CraftingTreeRings.y(node) - 5, 9, 300);
            }
        }
        var view = ringView();
        var shownMatches = view != null && !view.ofTree() ? view.matches() : matches();
        for (var entry : shownMatches.entrySet()) {
            var node = entry.getKey();
            if (shown == null ? !CraftingTreeRings.visible(node) : !shown.contains(node)) continue;
            if (entry.getValue()
                .blocking()) GL11.glColor4f(1, 0.4f, 0.4f, 1);
            RecursivePatternButton
                .drawIcon(minecraft, CraftingTreeRings.x(node) - 5, CraftingTreeRings.y(node) - 5, 8, 300);
            GL11.glColor4f(1, 1, 1, 1);
        }
        var focused = this.focus;
        if (focused != null && shown == null) {
            if (System.currentTimeMillis() > this.focusUntil) {
                this.focus = null;
            } else if (CraftingTreeRings.visible(focused)) {
                GL11.glPushMatrix();
                GL11.glTranslatef(0, 0, 250);
                CraftingTreeRings.drawSlotOutline(
                    tree,
                    CraftingTreeRings.x(focused),
                    CraftingTreeRings.y(focused),
                    ColorUtils.searchGoToHighlight.getColor(),
                    CraftingTreeRings.isTask(focused));
                GL11.glPopMatrix();
            }
        }
        GL11.glColor4f(1, 1, 1, 1);
    }

    // Zooms by whole screen pixels to a pixel of the tree while it can, so the one pixel lines are all drawn as wide
    public boolean zoomByPixels(int mouseX, int mouseY, int wheel) {
        if (wheel == 0 || GuiScreen.isShiftKeyDown() || !GuiScreen.isCtrlKeyDown()) return false;
        var gui = (AEBaseGui) CraftingTreeRings.parentGui(tree);
        float scale = (float) gui.mc.displayWidth / gui.width;
        float oldZoom = CraftingTreeRings.zoom(tree);
        float zoom = MathHelper.clamp_float(oldZoom * (float) Math.pow(1.4, wheel), 0.1f, 8.0f);
        float pixels = oldZoom * scale;
        long snapped = Math.round(zoom * scale);
        if (wheel > 0) snapped = Math.max(snapped, (long) Math.floor(pixels) + 1);
        else snapped = Math.min(snapped, (long) Math.ceil(pixels) - 1);
        snapped = Math.min(snapped, (long) Math.floor(8.0f * scale));
        if (snapped >= 1) zoom = snapped / scale;
        mouseX -= tree.widgetX;
        mouseY -= tree.widgetY;
        tree.scrollX += mouseX / oldZoom - mouseX / zoom;
        tree.scrollY += mouseY / oldZoom - mouseY / zoom;
        CraftingTreeRings.setZoom(tree, zoom);
        return true;
    }

    public boolean drawCycleTooltip(int guiMouseX, int guiMouseY) {
        var node = CraftingTreeRings.tooltipNode(tree);
        if (node == null) return false;
        var view = ringView();
        var extra = new StringBuilder();
        var ring = view == null || view.ring()
            .entry() != node ? this.rings.get(node) : null;
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
        } else if (nested(node) != null) {
            extra.append('\n')
                .append(StatCollector.translateToLocal("gtnl.gui.crafting_ring.nested"))
                .append('\n')
                .append(StatCollector.translateToLocal("gtnl.gui.crafting_ring.open"));
        }
        var match = matchOf(node);
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
        if (extra.length() == 0) return false;
        var text = CraftingTreeRings.isRequest(node) ? CraftingTreeRings.request(node)
            .getTooltipText() : ((CraftingTask) CraftingTreeRings.task(node)).getTooltipText();
        ((AEBaseGui) CraftingTreeRings.parentGui(tree)).drawTooltip(guiMouseX, guiMouseY, text + extra);
        return true;
    }

    public boolean mouseReleased(int guiMouseX, int guiMouseY, int button) {
        if (button != 0) return false;
        var view = ringView();
        if (view != null) {
            tree.mouseDragged(guiMouseX, guiMouseY);
            var pressed = CraftingTreeRings.pressedNode(tree);
            CraftingTreeRings.releasePress(tree);
            if (pressed != null && !CraftingTreeRings.dragging(tree) && pressed == view.nodeAt(guiMouseX, guiMouseY)) {
                var nestedRing = nested(pressed);
                if (GuiScreen.isShiftKeyDown()) locate(pressed);
                else if (nestedRing != null) nestedRing.run();
                else view.toggle(pressed);
            }
            return true;
        }
        var pressed = CraftingTreeRings.pressedNode(tree);
        if (pressed == null || CraftingTreeRings.dragging(tree)
            || pressed != CraftingTreeRings.nodeAt(tree, guiMouseX, guiMouseY)) {
            return false;
        }
        var ring = this.rings.get(pressed);
        var match = matches().get(pressed);
        if (ring != null) {
            // A shift-click folds what the cycle takes, as a click does in the tree
            if (GuiScreen.isShiftKeyDown()) return false;
            CraftingTreeRings.releasePress(tree);
            this.views.add(new CraftingRingView(tree, ring));
            return true;
        } else if (match != null) {
            // The tree has no way back for it to show, so the ring is made of the cycle found
            var parent = CraftingTreeRings.parent(pressed);
            var start = parent != null && CraftingTreeRings.isRequest(parent) ? CraftingTreeRings.request(parent).stack
                : null;
            CraftingTreeRings.releasePress(tree);
            openCycle(match.cycle(), start);
            return true;
        }
        return false;
    }

    // Back to the tree, opened down to the node, or for a craft not in the tree, its machines
    private void locate(Object node) {
        var view = ringView();
        if (view != null && !view.ofTree()) {
            var match = view.matches()
                .get(node);
            if (match == null) return;
            Object inTree = null;
            for (var entry : matches().entrySet()) {
                if (entry.getValue()
                    .location() == match.location()) {
                    inTree = entry.getKey();
                    break;
                }
            }
            if (inTree == null) {
                RecursivePatternReport.highlight(Minecraft.getMinecraft(), this.index.request(), List.of(match));
                return;
            }
            node = inTree;
        }
        closeAllRings();
        // A node of a cycle is in the tree as the cycle
        var cycleNode = this.contraction == null ? null : this.contraction.cycleNodeOf(node);
        if (cycleNode != null) node = cycleNode;
        for (Object ancestor = parentOf(node); ancestor != null; ancestor = parentOf(ancestor)) {
            CraftingTreeRings.setCollapsed(ancestor, false);
        }
        CraftingTreeRings.rebuildLayout(tree);
        float zoom = CraftingTreeRings.zoom(tree);
        tree.scrollX = CraftingTreeRings.x(node) + 8 - tree.widgetW / zoom / 2;
        tree.scrollY = CraftingTreeRings.y(node) + 8 - tree.widgetH / zoom / 2;
        this.focus = node;
        this.focusUntil = System.currentTimeMillis() + FOCUS_MILLIS;
    }

    private @Nullable Object parentOf(Object node) {
        return this.contraction != null ? this.contraction.parentOf(node) : CraftingTreeRings.parent(node);
    }

    public void backToTree() {
        if (!this.settingRequest) closeAllRings();
    }
}
