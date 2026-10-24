package com.science.gtnl.client.quantum;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.science.gtnl.client.quantum.CraftingTreeRings.Ring;
import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder.Cycle;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiCraftingTree;
import appeng.core.localization.ColorUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntMaps;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

/**
 * Shows one crafting cycle in the crafting tree widget, laid out the way the tree lays out a branch: what it makes on
 * top, what each craft takes under it. One round of the cycle runs down the left, from the request it is entered by
 * to the request that leads back, with the branches of the cycle and what it takes from outside to the right, and the
 * way back drawn as a line up the left side. A long cycle is folded every few crafts into the request there, as the
 * tree folds a branch, and unfolded by clicking it. The nodes are drawn the way the tree draws them; those of the tree
 * itself are put back when the view is closed.
 */
public final class CraftingRingView {

    private static final int X_SPACING = GuiCraftingTree.X_SPACING;
    private static final int MAX_OUTSIDE = 4;
    // Crafts shown down the cycle before it is folded
    private static final int SEGMENT = 6;
    private static final int LANE = 6;

    private record Line(int[] points, int color) {}

    // A way back from the craft making an input to it, run along a lane from top to bottom
    private record Route(Object maker, Object to, boolean right, int top, int bottom) {}

    private final GuiCraftingTree tree;
    private final Ring ring;
    private final Set<Object> inRing = new ReferenceOpenHashSet<>();
    // The round of the cycle, from the craft making the entry down to the way back
    private final List<Object> round = new ObjectArrayList<>();
    private final Set<Object> onRound = new ReferenceOpenHashSet<>();
    private final List<Object> nodes = new ObjectArrayList<>();
    private final Reference2IntOpenHashMap<Object> widths = new Reference2IntOpenHashMap<>();
    private final Reference2IntOpenHashMap<Object> hidden = new Reference2IntOpenHashMap<>();
    private final List<Line> lines = new ObjectArrayList<>();
    private final Map<Object, int[]> saved = new Reference2ObjectOpenHashMap<>();
    private final float savedScrollX, savedScrollY, savedZoom;
    private final int[] savedSize;
    private final boolean ofTree;
    private final Map<Object, CraftingCycleIndex.Match> matches;
    private final int omitted;
    private int width, height;

    /**
     * A cycle of the tree, of its own nodes.
     */
    public CraftingRingView(GuiCraftingTree tree, Ring ring) {
        this(tree, ring, Map.of(), 0);
    }

    /**
     * A cycle found in the patterns, of nodes made for it.
     */
    public CraftingRingView(GuiCraftingTree tree, CraftingCycleRings.Built built) {
        this(tree, built.ring(), built.matches(), built.omitted());
    }

    private CraftingRingView(GuiCraftingTree tree, Ring ring, Map<Object, CraftingCycleIndex.Match> matches,
        int omitted) {
        this.tree = tree;
        this.ring = ring;
        this.matches = matches;
        this.omitted = omitted;
        this.ofTree = CraftingTreeRings.allNodes(tree)
            .contains(ring.entry());
        this.savedScrollX = tree.scrollX;
        this.savedScrollY = tree.scrollY;
        this.savedZoom = CraftingTreeRings.zoom(tree);
        this.savedSize = CraftingTreeRings.treeSize(tree);
        this.inRing.addAll(ring.members());
        for (var member : ring.members()) {
            save(member);
            for (var child : CraftingTreeRings.children(member)) save(child);
        }
        findRound();
        // The entry shows what leaves the cycle, not a folded branch
        CraftingTreeRings.setCollapsed(ring.entry(), false);
        for (var member : ring.members()) {
            if (member != ring.entry() && !this.onRound.contains(member)) CraftingTreeRings.setCollapsed(member, false);
        }
        for (int i = 0; i < this.round.size(); i++) {
            var node = this.round.get(i);
            // Folded after every few crafts, unless nothing of the cycle is under it
            boolean fold = CraftingTreeRings.isRequest(node) && (i + 1) % (2 * SEGMENT) == 0
                && i + 1 < this.round.size();
            CraftingTreeRings.setCollapsed(node, fold);
        }
        layout();
        float zoom = Math.min(tree.widgetW / (this.width + 8f), tree.widgetH / (this.height + 8f));
        zoom = MathHelper.clamp_float(zoom, 0.5f, 1.0f);
        // Whole screen pixels to a pixel of the ring, so its one pixel lines are all drawn as wide
        var gui = (AEBaseGui) CraftingTreeRings.parentGui(tree);
        float scale = (float) gui.mc.displayWidth / gui.width;
        float snapped = (float) Math.floor(zoom * scale) / scale;
        if (snapped >= 0.5f) zoom = snapped;
        CraftingTreeRings.setZoom(tree, zoom);
        tree.scrollX = Math.min(0, (this.width - tree.widgetW / zoom) / 2);
        tree.scrollY = Math.min(0, (this.height - tree.widgetH / zoom) / 2);
    }

    private void save(Object node) {
        this.saved.computeIfAbsent(
            node,
            n -> new int[] { CraftingTreeRings.x(n), CraftingTreeRings.y(n), CraftingTreeRings.visible(n) ? 1 : 0,
                CraftingTreeRings.collapsed(n) ? 1 : 0 });
    }

    public Ring ring() {
        return this.ring;
    }

    /**
     * Whether its nodes are the tree's, which a new tree leaves behind.
     */
    public boolean ofTree() {
        return this.ofTree;
    }

    // For a cycle found in the patterns, the pattern each craft uses
    public Map<Object, CraftingCycleIndex.Match> matches() {
        return this.matches;
    }

    public int omitted() {
        return this.omitted;
    }

    // For a cycle found in the patterns, that cycle
    public @Nullable Cycle cycle() {
        for (var match : this.matches.values()) return match.cycle();
        return null;
    }

    /**
     * Lays the ring out again, after a ring opened from it moved its nodes.
     */
    public void refresh() {
        layout();
    }

    // The nodes shown, parents before their children
    public List<Object> nodes() {
        return this.nodes;
    }

    public boolean contains(Object node) {
        return this.widths.containsKey(node);
    }

    /**
     * The crafts of the cycle folded into a request, which clicking it shows.
     */
    public int foldedCrafts(Object node) {
        if (!CraftingTreeRings.collapsed(node) || !contains(node)) return 0;
        int count = 0;
        var pending = new ObjectArrayList<>(CraftingTreeRings.children(node));
        while (!pending.isEmpty()) {
            var next = pending.remove(pending.size() - 1);
            if (!this.inRing.contains(next)) continue;
            if (CraftingTreeRings.isTask(next)) count++;
            pending.addAll(CraftingTreeRings.children(next));
        }
        return count;
    }

    /**
     * Folds or unfolds a request of the cycle, as clicking it in the tree does.
     *
     * @return whether it could
     */
    public boolean toggle(Object node) {
        if (node == this.ring.entry() || !this.inRing.contains(node) || !CraftingTreeRings.isRequest(node)) {
            return false;
        }
        boolean any = false;
        for (var child : CraftingTreeRings.children(node)) any |= this.inRing.contains(child);
        if (!any) return false;
        CraftingTreeRings.setCollapsed(node, !CraftingTreeRings.collapsed(node));
        layout();
        return true;
    }

    /**
     * Puts the nodes back where the tree had them.
     */
    public void close() {
        // Only what the ring changed, which leaves the rows of the tree as they were
        if (this.ofTree) {
            for (var entry : this.saved.entrySet()) {
                var state = entry.getValue();
                CraftingTreeRings.moveTo(entry.getKey(), state[0], state[1]);
                CraftingTreeRings.setVisible(entry.getKey(), state[2] != 0);
                CraftingTreeRings.setCollapsed(entry.getKey(), state[3] != 0);
            }
        }
        CraftingTreeRings.setTreeSize(this.tree, this.savedSize[0], this.savedSize[1]);
        this.tree.scrollX = this.savedScrollX;
        this.tree.scrollY = this.savedScrollY;
        CraftingTreeRings.setZoom(this.tree, this.savedZoom);
    }

    private void findRound() {
        // Depth first from the craft making the entry, the first way back found ends the round
        var pending = new ObjectArrayList<Object>();
        for (var child : CraftingTreeRings.children(this.ring.entry())) {
            if (this.inRing.contains(child)) pending.add(child);
        }
        while (!pending.isEmpty()) {
            var node = pending.remove(pending.size() - 1);
            if (this.ring.backEdges()
                .contains(node)) {
                for (Object step = node; step != null
                    && step != this.ring.entry(); step = CraftingTreeRings.parent(step)) {
                    this.round.add(0, step);
                }
                break;
            }
            var children = CraftingTreeRings.children(node);
            for (int i = children.size() - 1; i >= 0; i--) {
                if (this.inRing.contains(children.get(i))) pending.add(children.get(i));
            }
        }
        this.onRound.addAll(this.round);
    }

    // The round first, so it stays the leftmost column, then the rest of the cycle, then what comes from outside
    private List<Object> shownChildren(Object node) {
        if (node == this.ring.entry()) {
            return this.round.isEmpty() ? List.of() : List.of(this.round.get(0));
        }
        if (!this.inRing.contains(node) || CraftingTreeRings.collapsed(node)) return List.of();
        var ofRing = new ObjectArrayList<Object>();
        var outside = new ObjectArrayList<Object>();
        for (var child : CraftingTreeRings.children(node)) {
            if (this.onRound.contains(child)) ofRing.add(0, child);
            else if (this.inRing.contains(child)) ofRing.add(child);
            else outside.add(child);
        }
        if (outside.size() > MAX_OUTSIDE) {
            this.hidden.put(node, outside.size() - MAX_OUTSIDE);
            outside = new ObjectArrayList<>(outside.subList(0, MAX_OUTSIDE));
        }
        ofRing.addAll(outside);
        return ofRing;
    }

    // As the tree lays out its rows: children side by side under their parent, each as wide as its branch
    private void layout() {
        this.nodes.clear();
        this.widths.clear();
        this.hidden.clear();
        this.lines.clear();
        var children = new Reference2ObjectOpenHashMap<Object, List<Object>>();
        var pending = new ObjectArrayList<Object>();
        pending.add(this.ring.entry());
        while (!pending.isEmpty()) {
            var node = pending.remove(pending.size() - 1);
            this.nodes.add(node);
            var shown = shownChildren(node);
            children.put(node, shown);
            for (int i = shown.size() - 1; i >= 0; i--) pending.add(shown.get(i));
        }
        for (int i = this.nodes.size() - 1; i >= 0; i--) {
            var node = this.nodes.get(i);
            int sum = 0;
            for (var child : children.get(node)) sum += this.widths.getInt(child);
            this.widths.put(node, Math.max(X_SPACING, sum));
        }
        CraftingTreeRings.moveTo(this.ring.entry(), 0, 4);
        int maxX = 0, maxY = 0;
        for (var node : this.nodes) {
            CraftingTreeRings.setVisible(node, true);
            int x = CraftingTreeRings.x(node), y = CraftingTreeRings.y(node);
            maxX = Math.max(maxX, x + 21);
            maxY = Math.max(maxY, y + 21);
            int childY = y + 16
                + (CraftingTreeRings.isRequest(node) ? GuiCraftingTree.REQUEST_RESOLVER_Y_SPACING
                    : GuiCraftingTree.RESOLVER_CHILD_Y_SPACING);
            int childX = x;
            for (var child : children.get(node)) {
                CraftingTreeRings.moveTo(child, childX, childY);
                childX += this.widths.getInt(child);
            }
        }
        // The ways back, in tree order so they keep their lanes from one layout to the next
        var routes = new ObjectArrayList<Route>();
        for (var edge : this.ring.members()) {
            if (!this.ring.backEdges()
                .contains(edge)) continue;
            var maker = CraftingTreeRings.maker(edge);
            Object to = edge;
            while (to != null && !this.widths.containsKey(to)) to = CraftingTreeRings.parent(to);
            if (maker == null || to == null || !this.widths.containsKey(maker) || to == maker) continue;
            int my = CraftingTreeRings.y(maker), ty = CraftingTreeRings.y(to);
            boolean byLeft = outermost(maker, -1) && outermost(to, -1);
            // Both ends last in their rows, as a catalyst returned to its craft: down the right
            boolean right = !byLeft && outermost(maker, 1) && outermost(to, 1);
            int from = right || outermost(maker, -1) ? my + 8 : my + 22;
            int into = right || outermost(to, -1) ? ty + 8 : ty + 22;
            routes.add(new Route(maker, to, right, Math.min(from, into), Math.max(from, into)));
        }
        var lanes = new int[routes.size()];
        int leftLanes = assignLanes(routes, false, lanes);
        int rightLanes = assignLanes(routes, true, lanes);
        // The ways back run up the left of everything
        int left = 3 + LANE * leftLanes + 4;
        for (var node : this.nodes) {
            CraftingTreeRings.moveTo(node, CraftingTreeRings.x(node) + left, CraftingTreeRings.y(node));
        }
        maxX += left;
        for (int i = 0; i < routes.size(); i++) {
            var route = routes.get(i);
            var maker = route.maker();
            var to = route.to();
            int mx = CraftingTreeRings.x(maker), my = CraftingTreeRings.y(maker);
            int tx = CraftingTreeRings.x(to), ty = CraftingTreeRings.y(to);
            var points = new IntArrayList();
            if (route.right()) {
                int lane = maxX + 4 + LANE * lanes[i];
                points.addAll(IntList.of(mx + 22, my + 8, lane, my + 8, lane, ty + 8, tx + 22, ty + 8));
            } else {
                int lane = left - 3 - LANE * (lanes[i] + 1);
                // Out of the side when nothing is left of it in its row, else out of the bottom and under the row
                if (outermost(maker, -1)) {
                    points.addAll(IntList.of(mx - 4, my + 8, lane, my + 8));
                } else {
                    points.addAll(IntList.of(mx + 2, my + 21, mx + 2, my + 22, lane, my + 22));
                }
                // Into the side the same way, or up into it from under its row, as what it takes comes in
                if (outermost(to, -1)) {
                    points.addAll(IntList.of(lane, ty + 8, tx - 4, ty + 8));
                } else {
                    points.addAll(IntList.of(lane, ty + 22, tx + 2, ty + 22, tx + 2, ty + 21));
                }
            }
            var flat = points.toIntArray();
            this.lines.add(new Line(flat, ColorUtils.searchGoToHighlight.getColor()));
        }
        maxX += LANE * rightLanes;
        this.width = maxX + 4;
        this.height = maxY + 4;
        CraftingTreeRings.setTreeSize(this.tree, this.width, this.height);
    }

    /**
     * Puts each way back on that side in the innermost lane free along the rows it runs over, the shortest first, so
     * one inside another stays inside it and those one after another share a lane.
     *
     * @return the lanes used
     */
    private static int assignLanes(List<Route> routes, boolean right, int[] lanes) {
        var order = new IntArrayList();
        for (int i = 0; i < routes.size(); i++) {
            if (routes.get(i)
                .right() == right) order.add(i);
        }
        order.sort((int a, int b) -> Integer.compare(span(routes.get(a)), span(routes.get(b))));
        int count = 0;
        for (int k = 0; k < order.size(); k++) {
            int i = order.getInt(k);
            var route = routes.get(i);
            int lane = 0;
            for (boolean taken = true; taken;) {
                taken = false;
                for (int m = 0; m < k; m++) {
                    int j = order.getInt(m);
                    var other = routes.get(j);
                    if (lanes[j] == lane && other.top() <= route.bottom() && route.top() <= other.bottom()) {
                        taken = true;
                        lane++;
                        break;
                    }
                }
            }
            lanes[i] = lane;
            count = Math.max(count, lane + 1);
        }
        return count;
    }

    private static int span(Route route) {
        return route.bottom() - route.top();
    }

    // Whether nothing is on that side of it in its row
    private boolean outermost(Object node, int side) {
        int x = CraftingTreeRings.x(node), y = CraftingTreeRings.y(node);
        for (var other : this.nodes) {
            if (other != node && CraftingTreeRings.y(other) == y
                && Integer.signum(CraftingTreeRings.x(other) - x) == side) return false;
        }
        return true;
    }

    public void draw(int guiMouseX, int guiMouseY, Runnable marks) {
        var gui = (AEBaseGui) CraftingTreeRings.parentGui(this.tree);
        if (Mouse.isButtonDown(0)) {
            this.tree.mouseDragged(guiMouseX - gui.getGuiLeft(), guiMouseY - gui.getGuiTop());
        }
        float zoom = CraftingTreeRings.zoom(this.tree);
        this.tree.scrollX = MathHelper.clamp_float(this.tree.scrollX, -this.tree.widgetW / zoom + 4, this.width - 4);
        this.tree.scrollY = MathHelper.clamp_float(this.tree.scrollY, -this.tree.widgetH / zoom + 4, this.height - 4);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        float scale = (float) gui.mc.displayWidth / gui.width;
        int widgetBottomY = gui.getYSize() - this.tree.widgetY - this.tree.widgetH;
        GL11.glScissor(
            (int) ((gui.getGuiLeft() + this.tree.widgetX) * scale),
            (int) ((gui.getGuiTop() + widgetBottomY) * scale),
            (int) (this.tree.widgetW * scale),
            (int) (this.tree.widgetH * scale));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glPushMatrix();
        GL11.glTranslatef(
            this.tree.widgetX - this.tree.scrollX * zoom,
            this.tree.widgetY - this.tree.scrollY * zoom,
            0.0f);
        GL11.glScalef(zoom, zoom, 1.0f);

        for (var node : this.nodes) {
            if (node != this.ring.entry()) CraftingTreeRings.drawParentLine(node);
        }
        for (var line : this.lines) drawLine(line);
        CraftingTreeRings.setTooltipNode(this.tree, nodeAt(guiMouseX - gui.getGuiLeft(), guiMouseY - gui.getGuiTop()));
        for (var node : this.nodes) CraftingTreeRings.draw(node);
        var font = gui.getFontRenderer();
        for (var entry : Reference2IntMaps.fastIterable(this.hidden)) {
            var text = "+" + entry.getIntValue();
            int x = CraftingTreeRings.x(entry.getKey()) + 20;
            int y = CraftingTreeRings.y(entry.getKey()) + 18;
            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, 200);
            GL11.glScalef(0.5f, 0.5f, 1);
            font.drawString(text, x * 2, y * 2, ColorUtils.searchboxText.getColor(), true);
            GL11.glPopMatrix();
        }
        marks.run();
        GL11.glPopMatrix();

        float scrollXPct = MathHelper.clamp_float(this.tree.scrollX / this.width, 0.0f, 1.0f);
        float scrollYPct = MathHelper.clamp_float(this.tree.scrollY / this.height, 0.0f, 1.0f);
        int barX = (int) (this.tree.widgetX + scrollXPct * (this.tree.widgetW - 24));
        int barY = (int) (this.tree.widgetY + scrollYPct * (this.tree.widgetH - 24));
        int bar = ColorUtils.craftingTreeScrollbar.getColor();
        int bottom = this.tree.widgetY + this.tree.widgetH;
        int right = this.tree.widgetX + this.tree.widgetW;
        Gui.drawRect(barX, bottom - 2, barX + 25, bottom - 1, bar);
        Gui.drawRect(right - 2, barY + 1, right - 1, barY + 24, bar);
        GL11.glPopAttrib();
    }

    // In one pixel lines as the tree draws them, with a head where the item arrives
    private void drawLine(Line line) {
        var p = line.points();
        int count = p.length / 2;
        for (int i = 0; i + 1 < count; i++) {
            int x0 = p[i * 2], y0 = p[i * 2 + 1], x1 = p[i * 2 + 2], y1 = p[i * 2 + 3];
            Gui.drawRect(Math.min(x0, x1), Math.min(y0, y1), Math.max(x0, x1) + 1, Math.max(y0, y1) + 1, line.color());
            if (i + 2 == count) {
                int dx = Integer.signum(x1 - x0), dy = Integer.signum(y1 - y0);
                for (int k = 1; k <= 3; k++) {
                    int bx = x1 - dx * k, by = y1 - dy * k;
                    if (dx != 0) Gui.drawRect(bx, by - k, bx + 1, by + k + 1, line.color());
                    else Gui.drawRect(bx - k, by, bx + k + 1, by + 1, line.color());
                }
            }
        }
    }

    public @Nullable Object nodeAt(int guiMouseX, int guiMouseY) {
        if (!this.tree.isPointInWidget(guiMouseX, guiMouseY)) return null;
        float zoom = CraftingTreeRings.zoom(this.tree);
        float x = (guiMouseX - this.tree.widgetX) / zoom + this.tree.scrollX;
        float y = (guiMouseY - this.tree.widgetY) / zoom + this.tree.scrollY;
        for (var node : this.nodes) {
            int nx = CraftingTreeRings.x(node), ny = CraftingTreeRings.y(node);
            if (x >= nx - 3 && x < nx + 21 && y >= ny - 3 && y < ny + 21) return node;
        }
        return null;
    }
}
