package com.science.gtnl.client.quantum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.client.quantum.CraftingTreeRings.Ring;

import appeng.client.gui.widgets.GuiCraftingTree;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

/**
 * Contracts each crafting cycle of a crafting tree into the request it is entered by. The crafts and requests of the
 * cycle leave the tree, and what the cycle takes from outside hangs under that request in their place, so the tree
 * goes on below the cycle as it would below a single craft. The nodes keep their own parents, so a ring can still be
 * made of them; the tree is laid out by the parents and children here instead.
 */
public final class CraftingTreeContraction {

    // Per cycle node, what hangs under it
    private final Map<Object, List<Object>> children = new Reference2ObjectOpenHashMap<>();
    // Per node under a cycle node, that node
    private final Map<Object, Object> parents = new Reference2ObjectOpenHashMap<>();
    // Per node of a cycle, the node it is contracted into
    private final Map<Object, Object> cycleNodes = new Reference2ObjectOpenHashMap<>();
    // Every node of the tree, those taken out of it too
    private final List<Object> all;

    private CraftingTreeContraction(List<Object> all) {
        this.all = all;
    }

    /**
     * Takes the cycles out of the tree.
     */
    public static CraftingTreeContraction of(GuiCraftingTree tree, Map<Object, Ring> rings) {
        var nodes = CraftingTreeRings.allNodes(tree);
        var contraction = new CraftingTreeContraction(new ObjectArrayList<>(nodes));
        for (var ring : rings.values()) {
            var entry = ring.entry();
            var inRing = new ReferenceOpenHashSet<>();
            inRing.addAll(ring.members());
            var under = new ObjectArrayList<Object>();
            // What else the entry is resolved by stays first, then what the cycle takes, in tree order
            for (var child : CraftingTreeRings.children(entry)) {
                if (!inRing.contains(child)) under.add(child);
            }
            for (var member : ring.members()) {
                if (member == entry) continue;
                contraction.cycleNodes.put(member, entry);
                for (var child : CraftingTreeRings.children(member)) {
                    if (inRing.contains(child)) continue;
                    under.add(child);
                    contraction.parents.put(child, entry);
                }
            }
            contraction.children.put(entry, under);
            CraftingTreeRings.setCollapsed(entry, false);
        }
        nodes.removeIf(contraction.cycleNodes::containsKey);
        return contraction;
    }

    public List<Object> allNodes() {
        return this.all;
    }

    /**
     * The node a node of a cycle is contracted into, null for a node still in the tree.
     */
    public @Nullable Object cycleNodeOf(Object node) {
        return this.cycleNodes.get(node);
    }

    public List<Object> childrenOf(Object node) {
        var under = this.children.get(node);
        return under != null ? under : CraftingTreeRings.children(node);
    }

    public @Nullable Object parentOf(Object node) {
        var parent = this.parents.get(node);
        return parent != null ? parent : CraftingTreeRings.parent(node);
    }

    /**
     * Lays out the tree as it lays itself out, by the parents and children of the contracted tree.
     */
    public void layout(GuiCraftingTree tree) {
        var nodes = CraftingTreeRings.allNodes(tree);
        for (int i = nodes.size() - 1; i >= 0; i--) {
            var node = nodes.get(i);
            int width = 0;
            if (!CraftingTreeRings.collapsed(node)) {
                for (var child : childrenOf(node)) width += CraftingTreeRings.subtreeWidth(child);
            }
            CraftingTreeRings.setSubtreeWidth(node, Math.max(GuiCraftingTree.X_SPACING, width));
        }
        var rows = CraftingTreeRings.rows(tree);
        rows.clear();
        int treeWidth = 0, treeHeight = 0;
        for (var node : nodes) {
            var parent = parentOf(node);
            boolean visible = parent == null
                || (CraftingTreeRings.visible(parent) && !CraftingTreeRings.collapsed(parent));
            CraftingTreeRings.setVisible(node, visible);
            if (!visible) continue;
            int x = CraftingTreeRings.x(node), y = CraftingTreeRings.y(node);
            rows.computeIfAbsent(y, k -> new ArrayList<>())
                .add(node);
            treeWidth = Math.max(treeWidth, x + 16);
            treeHeight = Math.max(treeHeight, y + 16);
            int childX = x;
            int childY = y + 16
                + (CraftingTreeRings.isRequest(node) ? GuiCraftingTree.REQUEST_RESOLVER_Y_SPACING
                    : GuiCraftingTree.RESOLVER_CHILD_Y_SPACING);
            for (var child : childrenOf(node)) {
                CraftingTreeRings.moveTo(child, childX, childY);
                childX += CraftingTreeRings.subtreeWidth(child);
            }
        }
        // What moved up under a cycle node joins rows of other depths, which are searched by x
        for (var row : rows.values()) row.sort(Comparator.comparingInt(CraftingTreeRings::x));
        // The nodes of a cycle stand where it is, so the lines up from what it takes end there
        for (var entry : this.cycleNodes.entrySet()) {
            var at = entry.getValue();
            CraftingTreeRings.moveTo(entry.getKey(), CraftingTreeRings.x(at), CraftingTreeRings.y(at));
            CraftingTreeRings.setVisible(entry.getKey(), false);
        }
        CraftingTreeRings.setTreeSize(tree, treeWidth, treeHeight);
    }
}
